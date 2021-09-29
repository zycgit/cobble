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
package net.hasor.cobble.loader;
import net.hasor.cobble.CollectionUtils;
import net.hasor.cobble.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
class LoaderClassLoader extends ClassLoader {
    private final Loader loader;

    public LoaderClassLoader(Loader loader) {
        this.loader = loader;
    }

    public LoaderClassLoader(Loader loader, ClassLoader parentLoader) {
        super(parentLoader);
        this.loader = loader;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (LoaderClassLoader.class.getName().equals(className)) {
            return LoaderClassLoader.class;
        }
        if (this.loader != null) {
            String resourceName = className.replace(".", "/") + ".class";
            InputStream inStream = this.getResourceAsStream(resourceName);
            if (inStream == null) {
                throw new ClassNotFoundException(className);
            } else {
                try {
                    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                    IOUtils.copy(inStream, byteOutput);
                    byte[] bs = byteOutput.toByteArray();
                    return this.defineClass(className, bs, 0, bs.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(className, e);
                }
            }
        } else {
            return super.findClass(className);
        }
    }

    @Override
    protected URL findResource(String name) {
        try {
            if (this.loader != null) {
                URL resourceURL = this.loader.getResource(name);
                if (resourceURL != null) {
                    return resourceURL;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        try {
            if (this.loader != null) {
                List<URL> resourceURLs = this.loader.getResources(name);
                if (resourceURLs != null && !resourceURLs.isEmpty()) {
                    return CollectionUtils.asEnumeration(resourceURLs);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.findResources(name);
    }

    public InputStream getResourceAsStream(String name) {
        InputStream inputStream = super.getResourceAsStream(name);
        if (inputStream != null) {
            return inputStream;
        } else {
            try {
                return this.loader != null ? this.loader.getResourceAsStream(name) : null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected String findLibrary(String sLib) {
        return super.findLibrary(sLib);
    }
}