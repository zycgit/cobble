/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hasor.cobble.loader.providers;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.io.IOUtils;
import net.hasor.cobble.loader.ClassMatchers;
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.loader.ResourceLoader.Scanner;
import net.hasor.cobble.loader.isol.IsolImportResourceLoader;
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Tests for {@link JarFile}.
 */
public class ClassResourceLoaderTests {
    @Test
    public void classpathLoaderTest1() {
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert loader.exist("dir/app-1.jar/app/FooImpl.class");
        assert loader.exist("dir/app-1.jar/app/FooImpl.java");
        assert loader.exist("dir/app-1.jar/META-INF/MANIFEST.MF");
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
        InputStream inputStream = loader.scanOneResource(scanner);
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStream));

        List<InputStream> inputStreams = loader.scanResources(scanner);
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStreams.get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(inputStreams.get(1)));
    }

    @Test
    public void classpathLoaderTest8() throws Exception {
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        assert loader.scanOneResource(ResourceLoader.MatchType.Prefix, ResourceLoader.ScanEvent::getStream, new String[] { "net.hasor" }) == null;
        assert loader.scanResources(ResourceLoader.MatchType.Prefix, ResourceLoader.ScanEvent::getStream, new String[] { "net.hasor" }).isEmpty();
    }

    @Test
    public void classpathLoaderTest9() throws ClassNotFoundException {
        ClassPathResourceLoader classLoader1 = new ClassPathResourceLoader();
        Class<?> clazz1 = classLoader1.toClassLoader().loadClass(ClassPathResourceLoader.class.getName());
        assert clazz1 == ClassPathResourceLoader.class;

        ClassPathResourceLoader classLoader2 = new ClassPathResourceLoader();
        classLoader2.bindParentLoader(Thread.currentThread().getContextClassLoader());
        Class<?> clazz2 = classLoader2.toClassLoader().loadClass(ClassPathResourceLoader.class.getName());
        assert clazz2 == ClassPathResourceLoader.class;
    }

    @Test
    public void classpathLoaderTest10() throws IOException {
        ClassPathResourceLoader classLoader = new ClassPathResourceLoader();
        classLoader.bindParentLoader(Thread.currentThread().getContextClassLoader());

        assert classLoader.exist("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class");
        assert classLoader.getResource("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class") != null;
        assert classLoader.getResources("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class").size() == 1;

        Set<Class<?>> classSet = classLoader.toClassFinder().getClassSet(new String[] { "net.hasor.cobble" }, ClassMatchers.subClassesOf(ResourceLoader.class));
        assert classSet.size() == 8;
        assert classSet.contains(ClassPathResourceLoader.class);
        assert classSet.contains(MultiResourceLoader.class);
        assert classSet.contains(ExportResourceLoader.class);
        assert classSet.contains(ImportResourceLoader.class);
        assert classSet.contains(AbstractResourceLoader.class);
        assert classSet.contains(JarResourceLoader.class);
        assert classSet.contains(PathResourceLoader.class);
        assert classSet.contains(IsolImportResourceLoader.class);

        classSet = classLoader.toClassFinder().getClassSet(ClassMatchers.featureWith(ResourceLoader.class));
        assert classSet.size() == 9;
        assert classSet.contains(ClassPathResourceLoader.class);
        assert classSet.contains(PathResourceLoader.class);
        assert classSet.contains(JarResourceLoader.class);
        assert classSet.contains(MultiResourceLoader.class);
        assert classSet.contains(AbstractResourceLoader.class);
        assert classSet.contains(ImportResourceLoader.class);
        assert classSet.contains(ExportResourceLoader.class);
        assert classSet.contains(ResourceLoader.class);
        assert classSet.contains(IsolImportResourceLoader.class);
    }
}