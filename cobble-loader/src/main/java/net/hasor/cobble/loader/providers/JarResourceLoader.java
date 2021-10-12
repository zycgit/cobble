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
import net.hasor.cobble.function.ESupplier;
import net.hasor.cobble.io.input.AutoCloseInputStream;
import net.hasor.cobble.loader.jar.JarFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
 * @version : 2021-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class JarResourceLoader extends AbstractResourceLoader {
    private final JarFile       jarFile;
    private final List<JarFile> nestedJarFile;

    public JarResourceLoader(File file) throws IOException {
        this(file, Collections.emptyList());
    }

    public JarResourceLoader(File file, List<String> nestedList) throws IOException {
        this.jarFile = new JarFile(file);
        this.nestedJarFile = new ArrayList<>();
        for (String nestedJar : nestedList) {
            JarEntry jarEntry = this.jarFile.getJarEntry(nestedJar);
            if (jarEntry != null) {
                this.nestedJarFile.add(this.jarFile.getNestedJarFile(jarEntry));
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
                    return new URL(nestedJar.getUrl(), nestedZipEntry.getName());
                }
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        ZipEntry zipEntry = this.jarFile.getEntry(resource);
        if (zipEntry != null) {
            return new AutoCloseInputStream(this.jarFile.getInputStream(zipEntry));
        } else if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    return new AutoCloseInputStream(nestedJar.getInputStream(nestedZipEntry));
                }
            }
        }
        return null;
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
                    result.add(new URL(nestedJar.getUrl(), nestedJar.getName()));
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
            result.add(new AutoCloseInputStream(this.jarFile.getInputStream(zipEntry)));
        }
        if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : nestedJarFile) {
                ZipEntry nestedZipEntry = nestedJar.getEntry(resource);
                if (nestedZipEntry != null) {
                    result.add(new AutoCloseInputStream(nestedJar.getInputStream(nestedZipEntry)));
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

            URL url = new URL(jarFile.getUrl(), entry.getName());
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

    @Override
    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        Predicate<JarEntry>[] tests = buildPredicate(matchType, scanPaths, ZipEntry::getName);

        this.scanJarFile(result, this.jarFile, scanner, tests, false);
        if (!this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                this.scanJarFile(result, nestedJar, scanner, tests, false);
            }
        }
        return result;
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        Predicate<JarEntry>[] tests = buildPredicate(matchType, scanPaths, ZipEntry::getName);

        this.scanJarFile(result, this.jarFile, scanner, tests, true);
        if (result.isEmpty() && !this.nestedJarFile.isEmpty()) {
            for (JarFile nestedJar : this.nestedJarFile) {
                if (result.isEmpty()) {
                    this.scanJarFile(result, nestedJar, scanner, tests, true);
                }
            }
        }
        return result.isEmpty() ? null : result.get(0);
    }
}
