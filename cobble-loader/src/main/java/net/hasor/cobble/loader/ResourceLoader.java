/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.Manifest;

/**
 * 资源加载器、资源扫描器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public interface ResourceLoader extends Closeable {
    <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException;

    <T> List<T> scanResources(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException;

    <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException;

    <T> T scanOneResource(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException;

    URL getResource(String resource) throws IOException;

    InputStream getResourceAsStream(String resource) throws IOException;

    long getResourceSize(String resource) throws IOException;

    CobbleClassLoader toClassLoader(ClassLoader parent);

    List<URL> getResources(String resource) throws IOException;

    List<InputStream> getResourcesAsStream(String resource) throws IOException;

    boolean exist(String resource);

    Manifest getManifest(String resource) throws IOException;
}
