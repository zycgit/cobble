/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader.providers;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.ExceptionUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.SystemUtils;
import net.hasor.cobble.function.ESupplier;
import net.hasor.cobble.io.FileUtils;
import net.hasor.cobble.loader.*;
import net.hasor.cobble.loader.Scanner;
import net.hasor.cobble.logging.Logger;

/**
 * 用于创建一个可以从 classpath 中获取资源的 ResourceLoader
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-10-10
 */
public class ClassPathResourceLoader extends AbstractResourceLoader {
    private static final Logger         logger    = Logger.getLogger(ClassPathResourceLoader.class);
    public static        ResourceLoader INSTANCE  = new ClassPathResourceLoader();
    private final        ClassLoader    classLoader;
    private final        List<URL>      classpath;
    private final        String[]       ZIP_TYPES = new String[] { ".jar", ".JAR", ".zip", ".ZIP" };
    private              Manifest       manifest  = null;

    public ClassPathResourceLoader() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.classpath = getClassPath(this.classLoader);
    }

    public ClassPathResourceLoader(ClassLoader parent) {
        this.classLoader = Objects.requireNonNull(parent);
        this.classpath = getClassPath(this.classLoader);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /** 获取ClassPath路径 */
    protected List<URL> getClassPath(ClassLoader classLoader) {
        return ClassUtils.getClassPath(classLoader);
    }

    private String formatResourcePath(String resourcePath) {
        if (StringUtils.isBlank(resourcePath)) {
            return "";
        }
        resourcePath = resourcePath.replaceAll("/{2}", "/");
        if (resourcePath.charAt(0) == '/') {
            resourcePath = resourcePath.substring(1);
        }
        return resourcePath;
    }

    private void checkScanPaths(MatchType matchType, String[] scanPaths) {
        if (matchType == null || matchType == MatchType.None) {
            throw new IllegalArgumentException("ClassPathResourceLoader scan requires a bounded match type.");
        }
        if (scanPaths == null || scanPaths.length == 0) {
            throw new IllegalArgumentException("ClassPathResourceLoader scan requires at least one scan path.");
        }
        for (String scanPath : scanPaths) {
            String path = formatResourcePath(scanPath);
            if (StringUtils.isBlank(path) || ".".equals(path)) {
                throw new IllegalArgumentException("ClassPathResourceLoader scan path cannot be root.");
            }
        }
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
    public long getResourceSize(String resource) throws IOException {
        return -1;
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
            InputStream in = (url != null) ? url.openStream() : null;
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
    public <T> List<T> scanResources(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException {
        checkScanPaths(matchType, scanPaths);
        final Predicate<JarEntry>[] testJar = buildPredicate(matchType, scanPaths, ZipEntry::getName);
        final Predicate<String>[] testDir = buildPredicate(matchType, scanPaths, s -> s);

        List<T> result = new ArrayList<>();
        for (URL url : this.classpath) {
            try {
                if (!matcher.test(url.toURI())) {
                    continue;
                }
            } catch (URISyntaxException e) {
                logger.error("cannot convert URL to URI jar (" + url + ") " + e.getMessage(), e);
                throw ExceptionUtils.toRuntime(e);
            }

            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                File contextDir = FileUtils.toFile(url);
                if (contextDir == null || !contextDir.exists()) {
                    continue;
                }

                if (contextDir.isDirectory()) {
                    dirScan(result, contextDir, contextDir, testDir, scanner, false);
                } else {
                    String jarName = contextDir.getName();
                    if (!StringUtils.endsWithAny(jarName, ZIP_TYPES)) {
                        continue;
                    }

                    try (JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(contextDir)) {
                        jarScan(result, jarFile, testJar, scanner, false);
                    } catch (Exception e) {
                        logger.error("cannot open jar (" + jarName + ") " + e.getMessage(), e);
                    }
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
    public <T> T scanOneResource(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException {
        checkScanPaths(matchType, scanPaths);
        List<T> result = new ArrayList<>();
        Predicate<JarEntry>[] testJar = buildPredicate(matchType, scanPaths, ZipEntry::getName);
        Predicate<String>[] testDir = buildPredicate(matchType, scanPaths, s -> s);

        for (URL url : this.classpath) {
            try {
                if (!matcher.test(url.toURI())) {
                    continue;
                }
            } catch (URISyntaxException e) {
                logger.error("cannot convert URL to URI jar (" + url + ") " + e.getMessage(), e);
                throw ExceptionUtils.toRuntime(e);
            }

            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                File contextDir = FileUtils.toFile(url);
                if (contextDir == null || !contextDir.exists()) {
                    continue;
                }

                if (contextDir.isDirectory()) {
                    dirScan(result, contextDir, contextDir, testDir, scanner, true);
                } else {
                    String jarName = contextDir.getName();
                    if (!StringUtils.endsWithAny(jarName, ZIP_TYPES)) {
                        continue;
                    }

                    try (JarFile jarFile = new JarFile(contextDir)) {
                        jarScan(result, jarFile, testJar, scanner, true);
                    } catch (Exception e) {
                        logger.error("cannot open jar (" + jarName + ") " + e.getMessage(), e);
                    }
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

            try {
                URI uri = new URL("jar:" + content + "!/" + entry.getName()).toURI();
                ESupplier<InputStream, IOException> inputStream = () -> jarFile.getInputStream(entry);
                T res = scanner.found(new ScanEvent(entry.getName(), entry.getSize(), uri, inputStream));
                if (res != null) {
                    result.add(res);
                }
            } catch (URISyntaxException e) {
                if (logger.isDebugEnabled()) {
                    logger.warn("scanJarFile :" + e.getMessage(), e);
                } else {
                    logger.debug("scanJarFile :" + e.getMessage());
                }
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

    private static <T> void dirScan(List<T> result, File contextDir, File curFile, Predicate<String>[] testDir, Scanner<T> scanner, boolean matchOnce) throws IOException {
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
            if (SystemUtils.isWindows()) {
                resourceName = StringUtils.replace(resourceName, File.separator, "/");
            }

            if (!dirTestFound(resourceName, testDir)) {
                continue;
            }
            ESupplier<InputStream, IOException> inputStream = () -> {
                if (fileItem.canRead()) {
                    return Files.newInputStream(fileItem.toPath());
                } else {
                    throw new IOException("file cannot be read :" + fileItem);
                }
            };

            T res = scanner.found(new ScanEvent(resourceName, fileItem.length(), fileItem.toURI(), inputStream));
            if (res != null) {
                result.add(res);
            }
        }
    }

    private static boolean dirTestFound(String entry, Predicate<String>[] dirTest) {
        if (dirTest == null || dirTest.length == 0) {
            return true;
        } else {
            for (Predicate<String> predicate : dirTest) {
                if (predicate.test(entry)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Manifest getManifest(String resource) throws IOException {
        if (manifest != null) {
            return manifest;
        }
        //classpath:/META-INF/MANIFEST.MF, 从classpath中获取
        for (URL url : classpath) {
            if (url.getProtocol().equals("file")) {
                File file = FileUtils.toFile(url);
                if (file != null && file.isDirectory()) {
                    file = new File(file, "META-INF/MANIFEST.MF");
                    if (file.exists()) {
                        try {
                            manifest = new Manifest(Files.newInputStream(file.toPath()));
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
        return manifest;
    }
}
