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
package net.hasor.cobble.loader.isol;
import net.hasor.cobble.CollectionUtils;
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.loader.providers.ImportResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 将多个 ClassLoader 汇聚为一个，同时提供类之间加载隔离
 * @version : 2021-10-13
 * @author 赵永春 (zyc@hasor.net)
 */
class IsolClassLoader extends ClassLoader {
    private final ImportResourceLoader             importLoader;
    private final Map<ResourceLoader, ClassLoader> loaderLoader;

    IsolClassLoader(ImportResourceLoader importLoader, ClassLoader parentLoader) {
        super(parentLoader);
        this.importLoader = Objects.requireNonNull(importLoader);
        this.loaderLoader = new HashMap<>();
    }

    protected ClassLoader findClassLoader(String className) {
        String resourceName = className.replace(".", "/") + ".class";
        ResourceLoader resourceLoader = this.importLoader.findLoader(resourceName);
        if (resourceLoader == null) {
            return null;
        }

        if (this.loaderLoader.containsKey(resourceLoader)) {
            return this.loaderLoader.get(resourceLoader);
        }
        synchronized (this) {
            if (this.loaderLoader.containsKey(resourceLoader)) {
                return this.loaderLoader.get(resourceLoader);
            }

            ClassLoader classLoader = resourceLoader.toClassLoader();
            this.loaderLoader.put(resourceLoader, classLoader);

            return classLoader;
        }
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (IsolClassLoader.class.getName().equals(className)) {
            return IsolClassLoader.class;
        }

        ClassLoader classLoader = this.findClassLoader(className);
        if (classLoader != null) {
            return classLoader.loadClass(className);
        } else {
            throw new ClassNotFoundException(className);
        }
    }

    @Override
    protected URL findResource(String resource) {
        try {
            ResourceLoader resourceLoader = this.importLoader.findLoader(resource);
            if (resourceLoader == null) {
                return null;
            } else {
                return resourceLoader.getResource(resource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getResourceAsStream(String resource) {
        try {
            ResourceLoader resourceLoader = this.importLoader.findLoader(resource);
            if (resourceLoader == null) {
                return null;
            } else {
                return resourceLoader.getResourceAsStream(resource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        List<URL> resourceURLs = this.importLoader.getResources(name);
        if (resourceURLs != null && !resourceURLs.isEmpty()) {
            return CollectionUtils.asEnumeration(resourceURLs);
        } else {
            return super.findResources(name);
        }
    }
}