/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.loader.providers;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.function.ESupplier;
import net.hasor.cobble.io.input.AutoCloseInputStream;
import net.hasor.cobble.loader.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 用于创建一个可以从 classpath 中获取资源的 ResourceLoader
 * @version : 2021-10-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ClassPathResourceLoader extends AbstractResourceLoader {
    public static ResourceLoader INSTANCE    = new ClassPathResourceLoader();
    private       ClassLoader    classLoader = null;
    private final List<URL>      classpath;

    public ClassPathResourceLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassPathResourceLoader(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.classpath = ClassUtils.getClassPath(classLoader);
    }

    private String formatResourcePath(String resourcePath) {
        resourcePath = resourcePath.replaceAll("/{2}", "/");
        if (resourcePath.charAt(0) == '/') {
            resourcePath = resourcePath.substring(1);
        }
        return resourcePath;
    }

    @Override
    public URL getResource(String resource) throws IOException {
        return this.classLoader.getResource(formatResourcePath(resource));
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        return this.classLoader.getResourceAsStream(formatResourcePath(resource));
    }

    @Override
    public List<URL> getResources(String resource) throws IOException {
        resource = formatResourcePath(resource);
        List<URL> urls = new ArrayList<>();
        Enumeration<URL> urlEnumeration = this.classLoader.getResources(resource);
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            if (url != null) {
                urls.add(url);
            }
        }
        return urls;
    }

    @Override
    public List<InputStream> getResourcesAsStream(String resource) throws IOException {
        resource = formatResourcePath(resource);
        List<InputStream> ins = new ArrayList<>();
        Enumeration<URL> urlEnumeration = this.classLoader.getResources(resource);
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            InputStream in = (url != null) ? new AutoCloseInputStream(url.openStream()) : null;
            if (in != null) {
                ins.add(in);
            }
        }
        return ins;
    }

    @Override
    public boolean exist(String resource) {
        return this.classLoader.getResource(formatResourcePath(resource)) != null;
    }

    @Override
    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        Predicate<JarEntry>[] testJar = buildPredicate(matchType, scanPaths, ZipEntry::getName);
        Predicate<File>[] testDir = buildPredicate(matchType, scanPaths, File::getPath);
        for (URL url : this.classpath) {
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                File contextDir = new File(URLDecoder.decode(url.getFile()));
                if (contextDir.isDirectory()) {
                    dirScan(result, contextDir, contextDir, testDir, scanner, false);
                } else {
                    JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(contextDir);
                    jarScan(result, jarFile, testJar, scanner, false);
                }
            } else if (protocol.equals("jar")) {
                JarURLConnection jarc = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarc.getJarFile();
                jarScan(result, jarFile, testJar, scanner, false);
            }
        }
        return result;
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        Predicate<JarEntry>[] testJar = buildPredicate(matchType, scanPaths, ZipEntry::getName);
        Predicate<File>[] testDir = buildPredicate(matchType, scanPaths, File::getPath);
        for (URL url : this.classpath) {
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                File contextDir = new File(URLDecoder.decode(url.getFile()));
                if (contextDir.isDirectory()) {
                    dirScan(result, contextDir, contextDir, testDir, scanner, true);
                } else {
                    JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(contextDir);
                    jarScan(result, jarFile, testJar, scanner, true);
                }
            } else if (protocol.equals("jar")) {
                JarURLConnection jarc = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarc.getJarFile();
                jarScan(result, jarFile, testJar, scanner, true);
            }

            if (!result.isEmpty()) {
                return result.get(0);
            }
        }
        return null;
    }

    private static <T> void jarScan(List<T> result, JarFile jarFile, Predicate<JarEntry>[] jarTest, Scanner<T> scanner, boolean matchOnce) throws IOException {
        URL content = new File(jarFile.getName()).toURI().toURL();
        Iterator<JarEntry> zipEntry = jarFile.stream().iterator();
        while (zipEntry.hasNext()) {
            JarEntry entry = zipEntry.next();
            if (!jarTestFound(entry, jarTest)) {
                continue;
            }

            URL url = new URL("jar:" + content + "!/" + entry.getName());
            ESupplier<InputStream, IOException> inputStream = () -> jarFile.getInputStream(entry);
            T res = scanner.found(new ScanEvent(entry.getName(), entry.getSize(), url, inputStream));
            if (res != null) {
                result.add(res);
            }
            if (matchOnce && !result.isEmpty()) {
                return;
            }
        }
    }

    private static boolean jarTestFound(JarEntry entry, Predicate<JarEntry>[] jarTest) {
        if (jarTest == null || jarTest.length == 0) {
            return true;
        } else {
            for (Predicate<JarEntry> predicate : jarTest) {
                if (predicate.test(entry)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static <T> void dirScan(List<T> result, File contextDir, File curFile, Predicate<File>[] testDir, Scanner<T> scanner, boolean matchOnce) throws IOException {
        if (matchOnce && !result.isEmpty()) {
            return;
        }
        File[] listFiles = curFile.listFiles();
        if (listFiles == null) {
            return;
        }

        for (File fileItem : listFiles) {
            if (matchOnce && !result.isEmpty()) {
                return;
            }
            if (fileItem.isHidden() || !fileItem.exists()) {
                continue;
            }
            if (fileItem.isDirectory()) {
                dirScan(result, contextDir, fileItem, testDir, scanner, matchOnce);
                continue;
            }

            String resourceName = fileItem.getAbsolutePath().substring(contextDir.getAbsolutePath().length() + 1);
            if (!dirTestFound(new File(resourceName), testDir)) {
                continue;
            }
            ESupplier<InputStream, IOException> inputStream = () -> {
                if (fileItem.canRead()) {
                    return new AutoCloseInputStream(new FileInputStream(fileItem));
                } else {
                    throw new IOException("file cannot be read :" + fileItem);
                }
            };
            T res = scanner.found(new ScanEvent(resourceName, fileItem.length(), fileItem.toURI().toURL(), inputStream));
            if (res != null) {
                result.add(res);
            }
        }
    }

    private static boolean dirTestFound(File entry, Predicate<File>[] dirTest) {
        if (dirTest == null || dirTest.length == 0) {
            return true;
        } else {
            for (Predicate<File> predicate : dirTest) {
                if (predicate.test(entry)) {
                    return true;
                }
            }
            return false;
        }
    }
}
