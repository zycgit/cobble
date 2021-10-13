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
import net.hasor.cobble.CollectionUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.ref.SimpleLRU;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 可以将不同的 ResourceLoader 汇聚到一起，在汇聚的时允许为每个 ResourceLoader 指定可见范围。
 * @version : 2021-10-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ImportResourceLoader extends AbstractResourceLoader {
    private final List<String>                  packageList    = new CopyOnWriteArrayList<>();
    private final List<LoaderWrap>              allLoaders     = new CopyOnWriteArrayList<>();
    private final Map<String, List<LoaderWrap>> packageLoaders = new ConcurrentHashMap<>();
    private final Map<String, List<LoaderWrap>> cacheLoaders   = new SimpleLRU<>(1024);

    /** import all path */
    public void addImport(ResourceLoader loader) {
        this.addImport(0, "", loader);
    }

    /** import all path */
    public void addImport(int order, ResourceLoader loader) {
        this.addImport(order, "", loader);
    }

    /** import specified path or resource */
    public void addImport(String importPath, ResourceLoader loader) {
        this.addImport(0, importPath, loader);
    }

    /** import specified path or resource */
    public synchronized void addImport(int order, String importPath, ResourceLoader loader) {
        importPath = pathOrResource(importPath);
        List<LoaderWrap> loaders = this.packageLoaders.computeIfAbsent(importPath, s -> new CopyOnWriteArrayList<>());
        LoaderWrap loaderWrap = new LoaderWrap(order, loader);

        if (!this.packageList.contains(importPath)) {
            this.packageList.add(importPath);
            this.allLoaders.add(loaderWrap);
        }

        if (!loaders.contains(loaderWrap)) {
            loaders.add(loaderWrap);
        } else {
            loaders.get(loaders.indexOf(loaderWrap)).order = order;
        }

        Collections.sort(loaders);
        this.cacheLoaders.clear();
    }

    private static String pathOrResource(String dirPath) {
        dirPath = dirPath.replace("\\", "/");
        if (dirPath.length() > 0 && dirPath.charAt(0) == '/') {
            dirPath = dirPath.substring(1);
        }
        return dirPath;
    }

    private List<LoaderWrap> rootPackageLoaders() {
        if (this.cacheLoaders.containsKey("")) {
            return this.cacheLoaders.get("");
        }
        synchronized (this) {
            if (this.cacheLoaders.containsKey("")) {
                return this.cacheLoaders.get("");
            }

            List<LoaderWrap> loaderList = this.packageLoaders.get("");
            if (loaderList != null) {
                Collections.sort(loaderList);
                loaderList = Collections.unmodifiableList(loaderList);
            } else {
                loaderList = Collections.emptyList();
            }
            this.cacheLoaders.put("", loaderList);
            return loaderList;
        }
    }

    private List<LoaderWrap> asResourcePackageLoaders(String resource) {
        if (this.cacheLoaders.containsKey(resource)) {
            return this.cacheLoaders.get(resource);
        }
        synchronized (this) {
            if (this.cacheLoaders.containsKey(resource)) {
                return this.cacheLoaders.get(resource);
            }

            List<LoaderWrap> loaders = new ArrayList<>(rootPackageLoaders());
            List<LoaderWrap> resLoaders = this.packageLoaders.get(resource);

            if (resLoaders != null) {
                for (LoaderWrap wrap : resLoaders) {
                    if (!loaders.contains(wrap)) {
                        loaders.add(wrap);
                    }
                }
            }

            Collections.sort(loaders);
            this.cacheLoaders.put(resource, Collections.unmodifiableList(loaders));
            return this.cacheLoaders.get(resource);
        }
    }

    protected List<LoaderWrap> getLoader(String resource) {
        final String fmtResource = pathOrResource(resource);
        if (StringUtils.isEmpty(fmtResource)) {
            return rootPackageLoaders();
        }

        // try cache found.
        if (this.cacheLoaders.containsKey(fmtResource)) {
            return this.cacheLoaders.get(fmtResource);
        }

        //maybe is resource
        if (this.packageLoaders.containsKey(fmtResource)) {
            return asResourcePackageLoaders(fmtResource);
        }

        int index = fmtResource.lastIndexOf("/");
        if (index == -1) {
            return rootPackageLoaders();
        }

        //try by path find
        final String fmtDirPath = fmtResource.substring(0, index);
        if (this.cacheLoaders.containsKey(fmtDirPath)) {
            return this.cacheLoaders.get(fmtDirPath);// try from cache found.
        }

        synchronized (this) {
            if (this.cacheLoaders.containsKey(fmtDirPath)) {
                return this.cacheLoaders.get(fmtDirPath);
            }

            List<LoaderWrap> loaders = new ArrayList<>();
            String tmpDirPath = fmtDirPath;
            while (true) {
                List<LoaderWrap> loaderList = this.packageLoaders.get(tmpDirPath);
                if (loaderList != null) {
                    loaders.addAll(loaderList);
                }

                index = tmpDirPath.lastIndexOf("/", index);
                if (index == -1) {
                    loaderList = this.packageLoaders.get("");
                    if (loaderList != null) {
                        loaders.addAll(loaderList);
                    }
                    break;
                }

                tmpDirPath = tmpDirPath.substring(0, index);
            }

            Collections.sort(loaders);
            this.cacheLoaders.put(fmtDirPath, Collections.unmodifiableList(loaders));
            return loaders;
        }
    }

    public ResourceLoader findLoader(String resource) {
        List<LoaderWrap> loaders = getLoader(resource);
        for (LoaderWrap wrap : loaders) {
            if (wrap.loader.exist(resource)) {
                return wrap.loader;
            }
        }
        return null;
    }

    protected ResourceLoader unsafeFindLoader(String resource) {
        for (LoaderWrap wrap : this.allLoaders) {
            if (wrap.loader.exist(resource)) {
                return wrap.loader;
            }
        }
        return null;
    }

    private <T> Scanner<T> scannerProxy(Scanner<T> scanner) {
        return event -> {
            String name = event.getName();
            for (String has : this.packageList) {
                if (name.startsWith(has)) {
                    return scanner.found(event);
                }
            }
            return null;
        };
    }

    private static List<String> paths(MatchType matchType, List<String> paths) {
        if (paths == null) {
            return null;
        }
        switch (matchType) {
            case None:
            case ContainsAny:
            case Suffix:
            case Regex: {
                return Collections.emptyList();
            }
            case Match:
            case Prefix: {
                return paths;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported matchType " + matchType.name());
            }
        }
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        Scanner<T> scannerProxy = scannerProxy(scanner);
        for (LoaderWrap loader : this.allLoaders) {
            T result = loader.loader.scanOneResource(matchType, scannerProxy, scanPaths);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        Scanner<T> scannerProxy = scannerProxy(scanner);
        List<T> result = new ArrayList<>();
        for (LoaderWrap loader : this.allLoaders) {
            List<T> foundResources = loader.loader.scanResources(matchType, scannerProxy, scanPaths);
            if (foundResources != null) {
                result.addAll(foundResources);
            }
        }
        return result;
    }

    @Override
    public URL getResource(String resource) throws IOException {
        List<LoaderWrap> loaders = getLoader(resource);
        for (LoaderWrap wrap : loaders) {
            URL resourceURL = wrap.loader.getResource(resource);
            if (resourceURL != null) {
                return resourceURL;
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        List<LoaderWrap> loaders = getLoader(resource);
        for (LoaderWrap wrap : loaders) {
            InputStream resourceStream = wrap.loader.getResourceAsStream(resource);
            if (resourceStream != null) {
                return resourceStream;
            }
        }
        return null;
    }

    @Override
    public List<URL> getResources(String resource) throws IOException {
        List<URL> result = new ArrayList<>();
        List<LoaderWrap> loaders = getLoader(resource);
        for (LoaderWrap wrap : loaders) {
            List<URL> resourceURLs = wrap.loader.getResources(resource);
            if (CollectionUtils.isNotEmpty(resourceURLs)) {
                result.addAll(resourceURLs);
            }
        }
        return result;
    }

    @Override
    public List<InputStream> getResourcesAsStream(String resource) throws IOException {
        List<InputStream> result = new ArrayList<>();
        List<LoaderWrap> loaders = getLoader(resource);
        for (LoaderWrap wrap : loaders) {
            List<InputStream> resourceStreams = wrap.loader.getResourcesAsStream(resource);
            if (CollectionUtils.isNotEmpty(resourceStreams)) {
                result.addAll(resourceStreams);
            }
        }
        return result;
    }

    @Override
    public boolean exist(String resource) {
        List<LoaderWrap> loaders = getLoader(resource);
        for (LoaderWrap wrap : loaders) {
            boolean result = wrap.loader.exist(resource);
            if (result) {
                return true;
            }
        }
        return false;
    }

    protected static class LoaderWrap implements Comparable<LoaderWrap> {
        int order;
        final ResourceLoader loader;

        public LoaderWrap(int order, ResourceLoader loader) {
            this.order = order;
            this.loader = loader;
        }

        @Override
        public boolean equals(Object o) {
            return Objects.equals(o, this.loader);
        }

        @Override
        public int hashCode() {
            return this.loader.hashCode();
        }

        @Override
        public String toString() {
            return "ResourceLoaderWrap{" + "order=" + order + ", loader=" + loader + '}';
        }

        @Override
        public int compareTo(LoaderWrap o) {
            return Integer.compare(this.order, o.order);
        }
    }
}
