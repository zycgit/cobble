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
import net.hasor.cobble.ExceptionUtils;
import net.hasor.cobble.function.ESupplier;
import net.hasor.cobble.loader.AbstractResourceLoader;
import net.hasor.cobble.loader.MatchType;
import net.hasor.cobble.loader.ScanEvent;
import net.hasor.cobble.loader.Scanner;
import net.hasor.cobble.loader.jar.JarFile;
import net.hasor.cobble.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * 可以处理 jar 包的 ResourceLoader，并且支持 jar in jar
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public class JarResourceLoader extends AbstractResourceLoader {
    private static final Logger        logger = Logger.getLogger(JarResourceLoader.class);
    private final        JarFile       jarFile;
    private final        List<JarFile> nestedJarFile;

    public JarResourceLoader(File file) throws IOException {
        this(null, file, Collections.emptyList());
    }

    public JarResourceLoader(ClassLoader parent, File file) throws IOException {
        super(parent);
        this.jarFile = new JarFile(file);
        this.nestedJarFile = new ArrayList<>();
    }

    public JarResourceLoader(ClassLoader parent, File file, List<String> nestedList) throws IOException {
        super(parent);
        this.jarFile = new JarFile(file);
        this.nestedJarFile = new ArrayList<>();
        for (String nestedJar : nestedList) {
            JarEntry jarEntry = this.jarFile.getJarEntry(nestedJar);
            if (jarEntry != null) {
                this.nestedJarFile.add(this.jarFile.getNestedJarFile(jarEntry));
            }
        }
    }

    public JarResourceLoader(ClassLoader parent, File file, Predicate<JarEntry> nestedPredicate) throws IOException {
        super(parent);
        this.jarFile = new JarFile(file);
        this.nestedJarFile = new ArrayList<>();
        if (nestedPredicate != null) {
            for (JarEntry jarEntry : this.jarFile) {
                if (jarEntry != null && nestedPredicate.test(jarEntry)) {
                    this.nestedJarFile.add(this.jarFile.getNestedJarFile(jarEntry));
                }
            }
        }
    }

    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }

    public Manifest getManifest(String nestedJarName) throws IOException {
        for (JarFile jarFile : this.nestedJarFile) {
            if (jarFile.getName().equals(nestedJarName)) {
                return jarFile.getManifest();
            }
        }
        return null;
    }

    @Override
    public URL getResource(String resource) throws IOException {
        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            return new URL(this.jarFile.getUrl(), zipEntry.getName());
        } else if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    return new URL(nestedJar.getUrl(), resource);
                }
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            return this.jarFile.getInputStream(zipEntry);
        } else if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    return nestedJar.getInputStream(nestedZipEntry);
                }
            }
        }
        return null;
    }

    @Override
    public long getResourceSize(String resource) throws IOException {
        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            return zipEntry.getSize();
        } else if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    return nestedZipEntry.getSize();
                }
            }
        }
        return -1;
    }

    @Override
    public List<URL> getResources(String resource) throws IOException {
        List<URL> result = new ArrayList<>();

        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            result.add(new URL(this.jarFile.getUrl(), zipEntry.getName()));
        }
        if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    result.add(new URL(nestedJar.getUrl(), resource));
                }
            }
        }
        return result;
    }

    @Override
    public List<InputStream> getResourcesAsStream(String resource) throws IOException {
        List<InputStream> result = new ArrayList<>();

        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            result.add(this.jarFile.getInputStream(zipEntry));
        }
        if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    result.add(nestedJar.getInputStream(nestedZipEntry));
                }
            }
        }
        return result;
    }

    @Override
    public boolean exist(String resource) {
        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            return true;
        } else if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean testFound(JarEntry entry, Predicate<JarEntry>[] testPredicates) {
        if (testPredicates == null || testPredicates.length == 0) {
            return true;
        } else {
            for (Predicate<JarEntry> predicate : testPredicates) {
                if (predicate.test(entry)) {
                    return true;
                }
            }
            return false;
        }
    }

    private <T> void scanJarFile(List<T> result, JarFile jarFile, Scanner<T> scanner, Predicate<JarEntry>[] scanPaths, boolean matchOnce) throws IOException {
        Iterator<JarEntry> zipEntry = jarFile.stream().iterator();
        while (zipEntry.hasNext()) {
            JarEntry entry = zipEntry.next();
            if (!testFound(entry, scanPaths)) {
                continue;
            }

            try {
                URI uri = new URL(jarFile.getUrl(), entry.getName()).toURI();
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

    @Override
    public <T> List<T> scanResources(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException {
        try {
            List<T> result = new ArrayList<>();
            Predicate<JarEntry>[] tests = buildPredicate(matchType, scanPaths, ZipEntry::getName);

            if (matcher.test(this.jarFile.getUrl().toURI())) {
                this.scanJarFile(result, this.jarFile, scanner, tests, false);
            }

            if (!this.nestedJarFile.isEmpty()) {
                for (JarFile nestedJar : this.nestedJarFile) {
                    if (matcher.test(nestedJar.getUrl().toURI())) {
                        this.scanJarFile(result, nestedJar, scanner, tests, false);
                    }
                }
            }
            return result;
        } catch (URISyntaxException e) {
            logger.error("cannot convert URL to URI jar (" + this.jarFile.getUrl() + ") " + e.getMessage(), e);
            throw ExceptionUtils.toRuntime(e);
        }
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException {
        try {
            List<T> result = new ArrayList<>();
            Predicate<JarEntry>[] tests = buildPredicate(matchType, scanPaths, ZipEntry::getName);

            if (matcher.test(this.jarFile.getUrl().toURI())) {
                this.scanJarFile(result, this.jarFile, scanner, tests, true);
                if (!result.isEmpty()) {
                    return result.get(0);
                }
            }
            if (!this.nestedJarFile.isEmpty()) {
                for (JarFile nestedJar : this.nestedJarFile) {
                    if (matcher.test(nestedJar.getUrl().toURI())) {
                        this.scanJarFile(result, nestedJar, scanner, tests, true);
                        if (!result.isEmpty()) {
                            return result.get(0);
                        }
                    }
                }
            }
            return null;
        } catch (URISyntaxException e) {
            logger.error("cannot convert URL to URI jar (" + this.jarFile.getUrl() + ") " + e.getMessage(), e);
            throw ExceptionUtils.toRuntime(e);
        }
    }
}
