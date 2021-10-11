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
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Tests for {@link JarFile}.
 */
public class ImportResourceLoaderTests {
    private ImportResourceLoader loader;

    @Before
    public void beforeImport() throws IOException {
        String file1 = ResourcesUtils.getResource("/dir/basic.jar").getFile();
        String file2 = ResourcesUtils.getResource("/dir/app-1.jar").getFile();
        String file3 = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        this.loader = new ImportResourceLoader();
        this.loader.addImport(0, new PathResourceLoader(new File(file1)));
        this.loader.addImport(1, "app/onlyjar", new PathResourceLoader(new File(file2)));
        this.loader.addImport(2, "app/onlyzip", new PathResourceLoader(new File(file3)));

    }

    @Test
    public void importLoaderTest1() throws Exception {
        ImportResourceLoader loader = new ImportResourceLoader();
        JarResourceLoader jar1 = new JarResourceLoader(new File(ResourcesUtils.getResource("/jars/basic.jar").getFile()));
        JarResourceLoader jar2 = new JarResourceLoader(new File(ResourcesUtils.getResource("/jars/app-1.jar").getFile()));
        JarResourceLoader jar3 = new JarResourceLoader(new File(ResourcesUtils.getResource("/jars/app-2.zip").getFile()));
        PathResourceLoader path1 = new PathResourceLoader(new File(""));
        loader.addImport(jar1);
        loader.addImport("app/onlyjar", jar2);
        loader.addImport("app/onlyzip", jar3);
        loader.addImport(ClassPathResourceLoader.INSTANCE);
        loader.addImport(2, ClassPathResourceLoader.INSTANCE);
        loader.addImport(1, path1);

        assert loader.getLoader("app").size() == 3;
        assert loader.getLoader("app").get(0).loader == jar1;
        assert loader.getLoader("app").get(1).loader == path1;
        assert loader.getLoader("app").get(2).loader == ClassPathResourceLoader.INSTANCE;

        assert loader.getLoader("app/onlyzip").size() == 4;
        assert loader.getLoader("app/onlyzip").get(0).loader == jar1 || loader.getLoader("app/onlyzip").get(1).loader == jar2;
        assert loader.getLoader("app/onlyzip").get(2).loader == path1;
        assert loader.getLoader("app/onlyzip").get(3).loader == ClassPathResourceLoader.INSTANCE;

        assert loader.getLoader("app/onlyzip/FooOnlyZip.java").size() == 4;
        assert loader.getLoader("app/onlyzip/FooOnlyZip.java").get(0).loader == jar1 || loader.getLoader("app/onlyzip/FooOnlyZip.java").get(1).loader == jar2;
        assert loader.getLoader("app/onlyzip/FooOnlyZip.java").get(2).loader == path1;
        assert loader.getLoader("app/onlyzip/FooOnlyZip.java").get(3).loader == ClassPathResourceLoader.INSTANCE;

        assert loader.getLoader("app") == loader.getLoader("app");
        assert loader.getLoader("app/onlyjar") == loader.getLoader("app/onlyjar");
    }

    @Test
    public void importLoaderTest2() throws Exception {
        assert !loader.exist("app/FooImpl.java");
        assert loader.exist("app/onlybasic/FooOnlyBasic.java");
        assert loader.exist("app/onlyjar/FooOnlyJar.java");
        assert loader.exist("app/onlyzip/FooOnlyZip.java");
        assert loader.exist("META-INF/MANIFEST.MF");
        assert loader.exist("META-INF/ReadMe.txt");
        assert !loader.exist("abc/abc.text");
    }

    @Test
    public void importLoaderTest3() throws Exception {
        assert loader.getResource("app/FooImpl.java") == null;
        assert loader.getResource("app/onlybasic/FooOnlyBasic.java").toString().endsWith("dir/basic.jar/app/onlybasic/FooOnlyBasic.java");
        assert loader.getResource("app/onlyjar/FooOnlyJar.java").toString().endsWith("dir/app-1.jar/app/onlyjar/FooOnlyJar.java");
        assert loader.getResource("app/onlyzip/FooOnlyZip.java").toString().endsWith("dir/app-2.zip/app/onlyzip/FooOnlyZip.java");
        assert loader.getResource("META-INF/MANIFEST.MF").toString().endsWith("dir/basic.jar/META-INF/MANIFEST.MF");
        assert loader.getResource("META-INF/ReadMe.txt").toString().endsWith("dir/basic.jar/META-INF/ReadMe.txt");
    }

    @Test
    public void importLoaderTest5() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/app/onlybasic/FooOnlyBasic.java"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/onlyjar/FooOnlyJar.java"));
        byte[] byteDat3 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-2.zip/app/onlyzip/FooOnlyZip.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/META-INF/MANIFEST.MF"));
        byte[] byteDat5 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/META-INF/ReadMe.txt"));

        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourceAsStream("app/onlybasic/FooOnlyBasic.java")));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResourceAsStream("app/onlyjar/FooOnlyJar.java")));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(loader.getResourceAsStream("app/onlyzip/FooOnlyZip.java")));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourceAsStream("META-INF/MANIFEST.MF")));
        assert Objects.deepEquals(byteDat5, IOUtils.toByteArray(loader.getResourceAsStream("META-INF/ReadMe.txt")));
    }

    @Test
    public void importLoaderTest6() throws Exception {
        assert loader.getResources("app/FooImpl.java").isEmpty();
        assert loader.getResources("app/onlybasic/FooOnlyBasic.java").get(0).toString().endsWith("dir/basic.jar/app/onlybasic/FooOnlyBasic.java");
        assert loader.getResources("app/onlyjar/FooOnlyJar.java").get(0).toString().endsWith("dir/app-1.jar/app/onlyjar/FooOnlyJar.java");
        assert loader.getResources("app/onlyzip/FooOnlyZip.java").get(0).toString().endsWith("dir/app-2.zip/app/onlyzip/FooOnlyZip.java");
        assert loader.getResources("META-INF/MANIFEST.MF").get(0).toString().endsWith("dir/basic.jar/META-INF/MANIFEST.MF");
        assert loader.getResources("META-INF/ReadMe.txt").get(0).toString().endsWith("dir/basic.jar/META-INF/ReadMe.txt");

        assert loader.getResources("META-INF/MANIFEST.MF").size() == 1;
        assert loader.getResources("META-INF/ReadMe.txt").size() == 1;
    }

    @Test
    public void importLoaderTest7() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/app/onlybasic/FooOnlyBasic.java"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/onlyjar/FooOnlyJar.java"));
        byte[] byteDat3 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-2.zip/app/onlyzip/FooOnlyZip.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/META-INF/MANIFEST.MF"));
        byte[] byteDat5 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/META-INF/ReadMe.txt"));

        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourcesAsStream("app/onlybasic/FooOnlyBasic.java").get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResourcesAsStream("app/onlyjar/FooOnlyJar.java").get(0)));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(loader.getResourcesAsStream("app/onlyzip/FooOnlyZip.java").get(0)));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourcesAsStream("META-INF/MANIFEST.MF").get(0)));
        assert Objects.deepEquals(byteDat5, IOUtils.toByteArray(loader.getResourcesAsStream("META-INF/ReadMe.txt").get(0)));
    }

    @Test
    public void importLoaderTest8() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/app/onlybasic/FooOnlyBasic.java"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/onlyjar/FooOnlyJar.java"));
        byte[] byteDat3 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-2.zip/app/onlyzip/FooOnlyZip.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/META-INF/MANIFEST.MF"));
        byte[] byteDat5 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/META-INF/ReadMe.txt"));

        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResource("app/onlybasic/FooOnlyBasic.java").openStream()));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResource("app/onlyjar/FooOnlyJar.java").openStream()));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(loader.getResource("app/onlyzip/FooOnlyZip.java").openStream()));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResource("META-INF/MANIFEST.MF").openStream()));
        assert Objects.deepEquals(byteDat5, IOUtils.toByteArray(loader.getResource("META-INF/ReadMe.txt").openStream()));
    }

    @Test
    public void importLoaderTest9() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/basic.jar/app/onlybasic/FooOnlyBasic.java"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-1.jar/app/onlyjar/FooOnlyJar.java"));
        byte[] byteDat3 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("dir/app-2.zip/app/onlyzip/FooOnlyZip.java"));

        ResourceLoader.Scanner<InputStream> scanner = event -> {
            if (event.getName().startsWith("app/only") && event.getName().endsWith(".java")) {
                System.out.println(event.getName());
                return event.getStream();
            } else {
                return null;
            }
        };

        assert Objects.deepEquals(//
                IOUtils.toByteArray(loader.scanOneResource(scanner)), //
                IOUtils.toByteArray(loader.scanOneResource(scanner))//
        );
        InputStream inputStream = loader.scanOneResource(scanner);
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStream));

        List<InputStream> inputStreams = loader.scanResources(scanner);
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStreams.get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(inputStreams.get(1)));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(inputStreams.get(2)));
    }

    @Test
    public void importLoaderTest10() throws Exception {
        assert loader.scanOneResource(ResourceLoader.MatchType.Prefix, ResourceLoader.ScanEvent::getStream, new String[] { "net.hasor" }) == null;
        assert loader.scanResources(ResourceLoader.MatchType.Prefix, ResourceLoader.ScanEvent::getStream, new String[] { "net.hasor" }).isEmpty();
    }
}
