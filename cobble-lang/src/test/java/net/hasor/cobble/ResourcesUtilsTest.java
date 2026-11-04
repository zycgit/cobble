/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public class ResourcesUtilsTest {
    @TempDir
    private Path directory;

    @Test
    public void getResourceTest1() throws IOException {
        InputStream resource = ResourcesUtils.getResourceAsStream("file:src/test/resources/i18n/authlabel_en_US.properties");
        assert resource != null;
    }

    @Test
    public void getResourceTest2() throws IOException {
        InputStream resource = ResourcesUtils.getResourceAsStream("classpath:/META-INF/License.ASF20.txt");
        assert resource != null;
    }

    @Test
    public void getResourcePreservesNestedJarHandler() throws IOException {
        URL resource = nestedResource();
        URL actual = ResourcesUtils.getResource(resourceLoader(resource), "fixture.properties");

        assertSame(resource, actual);
        try (InputStream stream = actual.openStream()) {
            assertEquals("nested resource", new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void getResourcesPreservesNestedJarHandler() throws IOException {
        URL resource = nestedResource();
        List<URL> actual = ResourcesUtils.getResources(resourceLoader(resource), "fixture.properties");

        assertEquals(1, actual.size());
        assertSame(resource, actual.get(0));
        try (InputStream stream = actual.get(0).openStream()) {
            assertEquals("nested resource", new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void jarResourceWithSpacesRemainsEncodedAndReadable() throws Exception {
        Path jar = this.directory.resolve("library with spaces.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            output.putNextEntry(new JarEntry("fixture.properties"));
            output.write("spaces supported".getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }

        try (URLClassLoader loader = new URLClassLoader(new URL[] { jar.toUri().toURL() }, null)) {
            URL original = loader.getResource("fixture.properties");
            URL actual = ResourcesUtils.getResource(loader, "fixture.properties");
            assertNotNull(original);
            assertEquals(original.toURI(), actual.toURI());
            assertEquals(original.toURI(), ResourcesUtils.getResources(loader, "fixture.properties").get(0).toURI());
            try (InputStream stream = ResourcesUtils.getResourceAsStream(loader, "fixture.properties")) {
                assertNotNull(stream);
                assertEquals("spaces supported", new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
    }

    private static URL nestedResource() throws IOException {
        return new URL(null, "jar:file:/distribution%20with%20spaces/app.jar!/APP-INF/classes!/fixture.properties", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL resource) {
                return new URLConnection(resource) {
                    @Override
                    public void connect() {
                    }

                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream("nested resource".getBytes(StandardCharsets.UTF_8));
                    }
                };
            }
        });
    }

    private static ClassLoader resourceLoader(URL resource) {
        return new ClassLoader(null) {
            @Override
            public URL getResource(String name) {
                return resource;
            }

            @Override
            public Enumeration<URL> getResources(String name) {
                return Collections.enumeration(Collections.singletonList(resource));
            }
        };
    }
}
