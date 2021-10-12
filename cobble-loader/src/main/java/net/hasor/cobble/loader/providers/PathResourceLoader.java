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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 将一个 File 对象所代表的路径作为根路径。
 * @version : 2021-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class PathResourceLoader extends AbstractResourceLoader {
    private final List<File> dirPathList;

    public PathResourceLoader() {
        this.dirPathList = new ArrayList<>();
    }

    public PathResourceLoader(File dirFile) {
        this.dirPathList = new ArrayList<>();
        this.addPath(dirFile);
    }

    public void addPath(File dirFile) {
        if (dirFile.exists() && dirFile.isDirectory()) {
            this.dirPathList.add(dirFile);
        }
    }

    private List<File> formatResourcePath(final String resource) {
        return dirPathList.stream().filter(File::exists).map(file -> new File(file, resource)).collect(Collectors.toList());
    }

    @Override
    public URL getResource(String resource) throws IOException {
        List<File> resourcePaths = formatResourcePath(resource);
        for (File file : resourcePaths) {
            if (file.exists() && file.isFile()) {
                return file.toURI().toURL();
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        List<File> resourcePaths = formatResourcePath(resource);
        for (File file : resourcePaths) {
            if (file.exists() && file.isFile()) {
                return new AutoCloseInputStream(new FileInputStream(file));
            }
        }
        return null;
    }

    @Override
    public List<URL> getResources(String resource) throws IOException {
        List<URL> result = new ArrayList<>();
        List<File> resourcePaths = formatResourcePath(resource);
        for (File file : resourcePaths) {
            if (file.exists() && file.isFile()) {
                result.add(file.toURI().toURL());
            }
        }
        return result;
    }

    @Override
    public List<InputStream> getResourcesAsStream(String resource) throws IOException {
        List<InputStream> result = new ArrayList<>();
        List<File> resourcePaths = formatResourcePath(resource);
        for (File file : resourcePaths) {
            if (file.exists() && file.isFile()) {
                result.add(new AutoCloseInputStream(new FileInputStream(file)));
            }
        }
        return result;
    }

    @Override
    public boolean exist(String resource) {
        List<File> resourcePaths = formatResourcePath(resource);
        for (File file : resourcePaths) {
            if (file.exists() && file.isFile()) {
                return true;
            }
        }
        return false;
    }

    protected boolean testFound(File entry, Predicate<File>[] testPredicates) {
        if (testPredicates == null || testPredicates.length == 0) {
            return true;
        } else {
            for (Predicate<File> predicate : testPredicates) {
                if (predicate.test(entry)) {
                    return true;
                }
            }
            return false;
        }
    }

    private <T> void scanDirFile(File baseDir, List<T> result, File scanDir, Scanner<T> scanner, Predicate<File>[] scanPaths, boolean matchOnce) throws IOException {
        File[] listFiles = scanDir.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File fileItem : listFiles) {
            if (matchOnce && !result.isEmpty()) {
                return;
            }
            if (fileItem.isHidden() || !fileItem.exists()) {
                // 过滤掉：隐藏和不存在的
                continue;
            }
            if (fileItem.isDirectory()) {
                // 目录继续向下搜索
                scanDirFile(baseDir, result, fileItem, scanner, scanPaths, matchOnce);
                continue;
            }
            String resourceName = fileItem.getAbsolutePath().substring(baseDir.getAbsolutePath().length() + 1);
            if (!testFound(new File(resourceName), scanPaths)) {
                // 匹配失败
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

    @Override
    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        Predicate<File>[] tests = buildPredicate(matchType, scanPaths, File::getPath);
        for (File baseDir : this.dirPathList) {
            if (baseDir.exists()) {
                scanDirFile(baseDir, result, baseDir, scanner, tests, false);
            }
        }
        return result;
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        Predicate<File>[] tests = buildPredicate(matchType, scanPaths, File::getPath);
        for (File baseDir : this.dirPathList) {
            if (baseDir.exists()) {
                scanDirFile(baseDir, result, baseDir, scanner, tests, true);
            }
        }
        return result.isEmpty() ? null : result.get(0);
    }
}
