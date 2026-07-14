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
import net.hasor.cobble.loader.MatchType;
import net.hasor.cobble.loader.ScanEvent;
import net.hasor.cobble.loader.Scanner;
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Tests for {@link JarFile}.
 */
public class PathResourceLoaderTests {
    @Test
    public void pathLoaderTest1() throws Exception {
        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert loader.exist("app/FooImpl.class");
        assert loader.exist("app/FooImpl.java");
        assert loader.exist("META-INF/ReadMe.txt");
        assert !loader.exist("abc/abc.text");
    }

    @Test
    public void pathLoaderTest2() throws Exception {
        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert loader.getResource("app/FooImpl.class").toString().endsWith("dir/app-2.zip/app/FooImpl.class");
        assert loader.getResource("app/FooImpl.java").toString().endsWith("dir/app-2.zip/app/FooImpl.java");
        assert loader.getResource("META-INF/ReadMe.txt").toString().endsWith("dir/app-2.zip/META-INF/ReadMe.txt");
    }

    @Test
    public void pathLoaderTest3() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/META-INF/ReadMe.txt"));

        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourceAsStream("app/FooImpl.class")));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResourceAsStream("app/FooImpl.java")));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourceAsStream("META-INF/ReadMe.txt")));
        assert loader.getResourceAsStream("abc/abc.text") == null;
    }

    @Test
    public void pathLoaderTest4() throws Exception {
        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert loader.getResources("app/FooImpl.class").get(0).toString().endsWith("dir/app-2.zip/app/FooImpl.class");
        assert loader.getResources("app/FooImpl.java").get(0).toString().endsWith("dir/app-2.zip/app/FooImpl.java");
        assert loader.getResources("META-INF/ReadMe.txt").get(0).toString().endsWith("dir/app-2.zip/META-INF/ReadMe.txt");
    }

    @Test
    public void pathLoaderTest5() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/META-INF/ReadMe.txt"));

        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResourcesAsStream("app/FooImpl.class").get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResourcesAsStream("app/FooImpl.java").get(0)));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResourcesAsStream("META-INF/ReadMe.txt").get(0)));
    }

    @Test
    public void pathLoaderTest6() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.java"));
        byte[] byteDat4 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/META-INF/ReadMe.txt"));

        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(loader.getResource("app/FooImpl.class").openStream()));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(loader.getResource("app/FooImpl.java").openStream()));
        assert Objects.deepEquals(byteDat4, IOUtils.toByteArray(loader.getResource("META-INF/ReadMe.txt").openStream()));
    }

    @Test
    public void pathLoaderTest7() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/FooImpl.java"));
        Scanner<InputStream> scanner = event -> {
            if (event.getName().startsWith("app/F")) {
                return event.getStream();
            } else {
                return null;
            }
        };

        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        InputStream inputStream = loader.scanOneResource(MatchType.Prefix, scanner, new String[] { "app" });
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStream));

        List<InputStream> inputStreams = loader.scanResources(MatchType.Prefix, scanner, new String[] { "app" });
        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(inputStreams.get(0)));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(inputStreams.get(1)));
    }

    @Test
    public void pathLoaderTest8() throws Exception {
        String file = ResourcesUtils.getResource("/dir/app-2.zip").getFile();
        PathResourceLoader loader = new PathResourceLoader(new File(file));
        assert loader.scanOneResource(MatchType.Prefix, ScanEvent::getStream, new String[] { "net.hasor" }) == null;
        assert loader.scanResources(MatchType.Prefix, ScanEvent::getStream, new String[] { "net.hasor" }).isEmpty();
    }
}
