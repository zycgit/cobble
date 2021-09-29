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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 *
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Loader {
    public default <T> List<T> scanResources(Scanner<T> scanner) throws IOException {
        return scanResources(MatchType.None, scanner, new String[0]);
    }

    public default <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner) throws IOException {
        return scanResources(matchType, scanner, new String[0]);
    }

    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPackages) throws IOException;

    public URL getResource(String resource) throws IOException;

    public InputStream getResourceAsStream(String resource) throws IOException;

    public List<URL> getResources(String resource) throws IOException;

    public List<InputStream> getResourcesAsStream(String resource) throws IOException;

    public boolean exist(String resource);

    public default ClassLoader toClassLoader() {
        return new LoaderClassLoader(this);
    }

    public default ClassLoader toClassLoader(ClassLoader parent) {
        return new LoaderClassLoader(this, parent);
    }
}
