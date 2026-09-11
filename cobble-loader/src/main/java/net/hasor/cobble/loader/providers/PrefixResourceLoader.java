/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader.providers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import net.hasor.cobble.loader.*;

/**
 * Presents a prefixed resource directory as a resource loader root.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2026-06-16
 */
public class PrefixResourceLoader extends AbstractResourceLoader {
    private final ResourceLoader resourceLoader;
    private final String         prefix;

    public PrefixResourceLoader(ResourceLoader resourceLoader, String prefix) {
        this.resourceLoader = resourceLoader;
        this.prefix = formatPrefix(prefix);
    }

    private static String formatPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return "";
        }
        prefix = prefix.replace('\\', '/');
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix;
    }

    private String withPrefix(String resource) {
        if (resource == null || resource.isEmpty()) {
            return this.prefix;
        }
        while (resource.startsWith("/")) {
            resource = resource.substring(1);
        }
        return this.prefix + resource;
    }

    private String withoutPrefix(String resource) {
        if (resource != null && resource.startsWith(this.prefix)) {
            return resource.substring(this.prefix.length());
        }
        return resource;
    }

    @Override
    public <T> List<T> scanResources(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException {
        List<T> result = new ArrayList<>();
        List<T> found = this.resourceLoader.scanResources(matchType, matcher, event -> {
            return scanner.found(stripEvent(event));
        }, prefixedScanPaths(scanPaths));
        if (found != null) {
            result.addAll(found);
        }
        return result;
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Predicate<URI> matcher, Scanner<T> scanner, String[] scanPaths) throws IOException {
        return this.resourceLoader.scanOneResource(matchType, matcher, event -> {
            return scanner.found(stripEvent(event));
        }, prefixedScanPaths(scanPaths));
    }

    private String[] prefixedScanPaths(String[] scanPaths) {
        if (scanPaths == null) {
            return new String[0];
        }
        String[] prefixed = new String[scanPaths.length];
        for (int i = 0; i < scanPaths.length; i++) {
            prefixed[i] = withPrefix(scanPaths[i]);
        }
        return prefixed;
    }

    private ScanEvent stripEvent(ScanEvent event) {
        return new ScanEvent(withoutPrefix(event.getName()), event.getSize(), event.getResource(), event::getStream);
    }

    @Override
    public URL getResource(String resource) throws IOException {
        return this.resourceLoader.getResource(withPrefix(resource));
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        return this.resourceLoader.getResourceAsStream(withPrefix(resource));
    }

    @Override
    public long getResourceSize(String resource) throws IOException {
        return this.resourceLoader.getResourceSize(withPrefix(resource));
    }

    @Override
    public List<URL> getResources(String resource) throws IOException {
        return this.resourceLoader.getResources(withPrefix(resource));
    }

    @Override
    public List<InputStream> getResourcesAsStream(String resource) throws IOException {
        return this.resourceLoader.getResourcesAsStream(withPrefix(resource));
    }

    @Override
    public boolean exist(String resource) {
        return this.resourceLoader.exist(withPrefix(resource));
    }

    @Override
    public Manifest getManifest(String resource) throws IOException {
        return this.resourceLoader.getManifest(withPrefix(resource));
    }

    @Override
    public void close() throws IOException {
        this.resourceLoader.close();
    }
}
