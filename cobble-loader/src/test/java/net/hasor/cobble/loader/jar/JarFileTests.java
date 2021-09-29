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

package net.hasor.cobble.loader.jar;
import net.hasor.cobble.ResourcesUtils;
import org.junit.Test;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;

/**
 * Tests for {@link JarFile}.
 */
public class JarFileTests {
    @Test
    public void jarFile_1_1() throws Exception {
        String zipFile = ResourcesUtils.getResource("/jars/app.jar").getFile();
        java.util.jar.JarFile jarFile = new java.util.jar.JarFile(new File(zipFile));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("demo/Application.class");
        assert names.contains("app/Foo.java");
        assert names.contains("app/");
        assert names.contains("META-INF/spring.factories");
        assert names.contains("app/Foo.class");
        assert names.contains("demo/");
        assert names.contains("META-INF/");
        assert names.contains("META-INF/MANIFEST.MF");
    }

    @Test
    public void jarFile_1_2() throws Exception {
        String zipFile = ResourcesUtils.getResource("/jars/app.jar").getFile();
        net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(new File(zipFile));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("demo/Application.class");
        assert names.contains("app/Foo.java");
        assert names.contains("app/");
        assert names.contains("META-INF/spring.factories");
        assert names.contains("app/Foo.class");
        assert names.contains("demo/");
        assert names.contains("META-INF/");
        assert names.contains("META-INF/MANIFEST.MF");
    }

    @Test
    public void jarFile_2_1() throws Exception {
        String zipFile = ResourcesUtils.getResource("/jars/app.zip").getFile();
        java.util.jar.JarFile jarFile = new java.util.jar.JarFile(new File(zipFile));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("demo/Application.class");
        assert names.contains("app/Foo.java");
        assert names.contains("app/");
        assert names.contains("META-INF/spring.factories");
        assert names.contains("app/Foo.class");
        assert names.contains("demo/");
        assert names.contains("META-INF/");
        assert names.contains("META-INF/MANIFEST.MF");
    }

    @Test
    public void jarFile_2_2() throws Exception {
        String zipFile = ResourcesUtils.getResource("/jars/app.zip").getFile();
        net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(new File(zipFile));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("demo/Application.class");
        assert names.contains("app/Foo.java");
        assert names.contains("app/");
        assert names.contains("META-INF/spring.factories");
        assert names.contains("app/Foo.class");
        assert names.contains("demo/");
        assert names.contains("META-INF/");
        assert names.contains("META-INF/MANIFEST.MF");
    }
}
