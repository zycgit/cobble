/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package net.hasor.cobble.loader.providers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.io.IOUtils;
import net.hasor.cobble.loader.*;
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link JarFile}.
 */
public class ClassResourceLoaderTests {
    @Test
    public void classpathLoaderTest1() {
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert loader.exist("dir/app-1.jar/app/FooImpl.class");
        assert loader.exist("dir/app-1.jar/app/FooImpl.java");
        assert loader.exist("dir/app-1.jar/META-INF/jar.mf");
        assert loader.exist("dir/app-1.jar/META-INF/ReadMe.txt");
        assert !loader.exist("dir/app-1.jar/abc/abc.text");
    }

    @Test
    public void classpathLoaderTest2() throws Exception {
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert loader.getResource("dir/app-1.jar/app/FooImpl.class").toString().endsWith("dir/app-1.jar/app/FooImpl.class");
        assert loader.getResource("dir/app-1.jar/app/FooImpl.java").toString().endsWith("dir/app-1.jar/app/FooImpl.java");
        assert loader.getResource("dir/app-1.jar/META-INF/ReadMe.txt").toString().endsWith("dir/app-1.jar/META-INF/ReadMe.txt");
    }

    @Test
    public void classpathLoaderTest3() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/META-INF/ReadMe.txt"));

        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourceAsStream("dir/app-1.jar/app/FooImpl.class")));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResourceAsStream("dir/app-1.jar/app/FooImpl.java")));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourceAsStream("dir/app-1.jar/META-INF/ReadMe.txt")));
        assert loader.getResourceAsStream("abc/abc.text") == null;
    }

    @Test
    public void classpathLoaderTest4() throws Exception {
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert loader.getResources("dir/app-1.jar/app/FooImpl.class").get(0).toString().endsWith("dir/app-1.jar/app/FooImpl.class");
        assert loader.getResources("dir/app-1.jar/app/FooImpl.java").get(0).toString().endsWith("dir/app-1.jar/app/FooImpl.java");
        assert loader.getResources("dir/app-1.jar/META-INF/ReadMe.txt").get(0).toString().endsWith("dir/app-1.jar/META-INF/ReadMe.txt");
    }

    @Test
    public void classpathLoaderTest5() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/META-INF/ReadMe.txt"));

        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourcesAsStream("dir/app-1.jar/app/FooImpl.class").get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResourcesAsStream("dir/app-1.jar/app/FooImpl.java").get(0)));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourcesAsStream("/dir/app-1.jar/META-INF/ReadMe.txt").get(0)));
    }

    @Test
    public void classpathLoaderTest6() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/META-INF/ReadMe.txt"));

        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResource("dir/app-1.jar/app/FooImpl.class").openStream()));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResource("dir/app-1.jar/app/FooImpl.java").openStream()));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResource("dir/app-1.jar/META-INF/ReadMe.txt").openStream()));
    }

    @Test
    public void classpathLoaderTest7() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/FooImpl.java"));
        Scanner<InputStream> scanner = event -> {
            if (event.getName().startsWith("dir/app-1.jar/app/F")) {
                return event.getStream();
            } else {
                return null;
            }
        };

        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        InputStream inputStream = loader.scanOneResource(MatchType.Prefix, scanner, new String[] { "dir/app-1.jar/app/F" });
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStream));

        List<InputStream> inputStreams = loader.scanResources(MatchType.Prefix, scanner, new String[] { "dir/app-1.jar/app/F" });
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStreams.get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(inputStreams.get(1)));
    }

    @Test
    public void classpathLoaderTest8() throws Exception {
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        try {
            loader.scanOneResource(MatchType.None, ScanEvent::getStream, new String[] { "dir/app-1.jar/app" });
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("bounded match type"));
        }

        try {
            loader.scanResources(MatchType.Prefix, ScanEvent::getStream, new String[] { "/" });
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("cannot be root"));
        }

        assert loader.scanOneResource(MatchType.Prefix, ScanEvent::getStream, new String[] { "net.hasor" }) == null;
        assert loader.scanResources(MatchType.Prefix, ScanEvent::getStream, new String[] { "net.hasor" }).isEmpty();
    }

    @Test
    public void classpathLoaderTest9() throws ClassNotFoundException {
        ClassPathResourceLoader resLoader1 = new ClassPathResourceLoader();

        CobbleClassLoader classLoader1 = resLoader1.toClassLoader(Thread.currentThread().getContextClassLoader());
        Class<?> clazz1 = classLoader1.loadClass(ClassPathResourceLoader.class.getName());
        assert clazz1 == ClassPathResourceLoader.class;

        ClassPathResourceLoader resLoader2 = new ClassPathResourceLoader();
        CobbleClassLoader classLoader2 = resLoader2.toClassLoader(Thread.currentThread().getContextClassLoader());
        Class<?> clazz2 = classLoader2.loadClass(ClassPathResourceLoader.class.getName());
        assert clazz2 == ClassPathResourceLoader.class;
    }

    @Test
    public void classpathLoaderTest10() throws IOException {
        ClassPathResourceLoader resourceLoader = new ClassPathResourceLoader();

        assert resourceLoader.exist("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class");
        assert resourceLoader.getResource("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class") != null;
        List<URL> resources = resourceLoader.getResources("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class");
        assert !resources.isEmpty();

        try {
            new CobbleClassScanner(resourceLoader).getClassSet(ClassMatchers.subClassesOf(Object.class));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("requires package ranges"));
        }

        Set<Class<?>> classSet = new CobbleClassScanner(resourceLoader).getClassSet(new String[] { "net.hasor.cobble.loader.providers" }, ClassMatchers.subClassesOf(Object.class));
        assert classSet.contains(ClassResourceLoaderTests.class);
        assert classSet.contains(JarResourceLoaderTests.class);
        assert classSet.contains(PathResourceLoaderTests.class);
    }
}
