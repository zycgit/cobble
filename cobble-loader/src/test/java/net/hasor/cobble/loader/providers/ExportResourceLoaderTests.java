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
import net.hasor.cobble.loader.ResourceLoader.Scanner;
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Tests for {@link JarFile}.
 */
public class ExportResourceLoaderTests {
    @Test
    public void exportLoaderTest1() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        loader.addExport("/app/");
        loader.addExport("app/");
        loader.addExport("app");
        assert loader.getExportSet().size() == 1;
    }

    @Test
    public void exportLoaderTest2() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert !loader.exist("app/FooImpl.class");
        assert !loader.exist("app/FooImpl.java");
        assert !loader.exist("META-INF/MANIFEST.MF");
        assert !loader.exist("META-INF/License.ASF20.txt");
        assert !loader.exist("abc/abc.text");

        loader.addExport("app");
        assert loader.exist("app/FooImpl.class");
        assert loader.exist("app/FooImpl.java");

        loader.addExport("META-INF");
        assert loader.exist("META-INF/MANIFEST.MF");
        assert loader.exist("META-INF/License.ASF20.txt");

        loader.addExport("abc");
        assert !loader.exist("abc/abc.text");
    }

    @Test
    public void exportLoaderTest3() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert loader.getResource("app/FooImpl.class") == null;
        assert loader.getResource("META-INF/License.ASF20.txt") == null;

        loader.addExport("app");
        assert loader.getResource("app/FooImpl.class").toString().endsWith("jars/app-1.jar!/app/FooImpl.class");
        assert loader.getResource("META-INF/License.ASF20.txt") == null;

        loader.addExport("META-INF");
        assert loader.getResource("app/FooImpl.class").toString().endsWith("jars/app-1.jar!/app/FooImpl.class");
        assert loader.getResource("META-INF/License.ASF20.txt").toString().endsWith("jars/app-1.jar!/META-INF/License.ASF20.txt");
    }

    @Test
    public void exportLoaderTest4() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/META-INF/License.ASF20.txt"));

        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert loader.getResourceAsStream("app/FooImpl.class") == null;
        assert loader.getResourceAsStream("META-INF/License.ASF20.txt") == null;

        loader.addExport("app");
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourceAsStream("app/FooImpl.class")));
        assert loader.getResourceAsStream("META-INF/License.ASF20.txt") == null;

        loader.addExport("META-INF");
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourceAsStream("app/FooImpl.class")));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourceAsStream("META-INF/License.ASF20.txt")));
    }

    @Test
    public void exportLoaderTest5() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert loader.getResources("app/FooImpl.class").isEmpty();
        assert loader.getResources("META-INF/License.ASF20.txt").isEmpty();

        loader.addExport("app");
        assert loader.getResources("app/FooImpl.class").get(0).toString().endsWith("jars/app-1.jar!/app/FooImpl.class");
        assert loader.getResources("META-INF/License.ASF20.txt").isEmpty();

        loader.addExport("META-INF");
        assert loader.getResources("app/FooImpl.class").get(0).toString().endsWith("jars/app-1.jar!/app/FooImpl.class");
        assert loader.getResources("META-INF/License.ASF20.txt").get(0).toString().endsWith("jars/app-1.jar!/META-INF/License.ASF20.txt");
    }

    @Test
    public void exportLoaderTest6() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/META-INF/License.ASF20.txt"));

        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert loader.getResourcesAsStream("app/FooImpl.class").isEmpty();
        assert loader.getResourcesAsStream("META-INF/License.ASF20.txt").isEmpty();

        loader.addExport("app");
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourcesAsStream("app/FooImpl.class").get(0)));
        assert loader.getResources("META-INF/License.ASF20.txt").isEmpty();

        loader.addExport("META-INF");
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourcesAsStream("app/FooImpl.class").get(0)));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourcesAsStream("META-INF/License.ASF20.txt").get(0)));

    }

    @Test
    public void exportLoaderTest7() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/app/FooImpl.java"));
        Scanner<InputStream> scanner = event -> {
            if (event.getName().startsWith("app/F")) {
                return event.getStream();
            } else {
                return null;
            }
        };

        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert loader.scanOneResource(scanner) == null;
        assert loader.scanResources(scanner).isEmpty();

        loader.addExport("app");
        InputStream inputStream = loader.scanOneResource(scanner);
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStream));
        List<InputStream> inputStreams = loader.scanResources(scanner);
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStreams.get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(inputStreams.get(1)));
    }

    @Test
    public void exportLoaderTest8() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        ExportResourceLoader loader = new ExportResourceLoader(new JarResourceLoader(new File(file)));

        assert loader.scanOneResource(ResourceLoader.MatchType.Prefix, ResourceLoader.ScanEvent::getStream, new String[] { "net.hasor" }) == null;
        assert loader.scanResources(ResourceLoader.MatchType.Prefix, ResourceLoader.ScanEvent::getStream, new String[] { "net.hasor" }).isEmpty();
    }
}
