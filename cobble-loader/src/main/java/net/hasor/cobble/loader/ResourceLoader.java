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
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;

/**
 * 资源加载器、资源扫描器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public interface ResourceLoader {
    <T> List<T> scanResources(Scanner<T> scanner) throws IOException;

    <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner) throws IOException;

    <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException;

    <T> List<T> scanResources(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException;

    <T> T scanOneResource(Scanner<T> scanner) throws IOException;

    <T> T scanOneResource(MatchType matchType, Scanner<T> scanner) throws IOException;

    <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException;

    <T> T scanOneResource(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException;

    URL getResource(String resource) throws IOException;

    InputStream getResourceAsStream(String resource) throws IOException;

    long getResourceSize(String resource) throws IOException;

    Class<?> getClass(String className) throws ClassNotFoundException;

    List<URL> getResources(String resource) throws IOException;

    List<InputStream> getResourcesAsStream(String resource) throws IOException;

    boolean exist(String resource);
}
