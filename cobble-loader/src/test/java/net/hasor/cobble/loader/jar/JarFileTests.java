/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package net.hasor.cobble.loader.jar;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.io.IOUtils;
import org.junit.Test;

/**
 * Tests for {@link JarFile}.
 */
public class JarFileTests {
    @Test
    public void jarFile_1_1() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        java.util.jar.JarFile jarFile = new java.util.jar.JarFile(new File(file));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("app/FooImpl.class");
        assert names.contains("app/FooImpl.java");
        assert names.contains("META-INF/jar.mf");
        assert names.contains("META-INF/ReadMe.txt");
    }

    @Test
    public void jarFile_1_2() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(new File(file));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("app/FooImpl.class");
        assert names.contains("app/FooImpl.java");
        assert names.contains("META-INF/jar.mf");
        assert names.contains("META-INF/ReadMe.txt");
    }

    @Test
    public void jarFile_2_1() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-2.zip").getFile();
        java.util.jar.JarFile jarFile = new java.util.jar.JarFile(new File(file));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("app/FooImpl.class");
        assert names.contains("app/FooImpl.java");
        assert names.contains("META-INF/ReadMe.txt");
    }

    @Test
    public void jarFile_2_2() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-2.zip").getFile();
        net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(new File(file));
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<String> names = new HashSet<>();
        while (entries.hasMoreElements()) {
            names.add(entries.nextElement().getName());
        }
        assert !names.isEmpty();
        assert names.contains("app/FooImpl.class");
        assert names.contains("app/FooImpl.java");
        assert names.contains("META-INF/ReadMe.txt");
    }

    @Test
    public void getManifest() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(new File(file));

        assert jarFile.getManifest().getMainAttributes().getValue("Created-By").length() > 0;
    }

    @Test
    public void getManifestEntry() throws Exception {
        String file = ResourcesUtils.getResource("/jars/app-1.jar").getFile();
        net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(new File(file));

        ZipEntry entry = jarFile.getJarEntry("META-INF/jar.mf");
        Manifest manifest = new Manifest(jarFile.getInputStream(entry));
        assert manifest.getMainAttributes().getValue("Created-By").equals("1.8.0_221 (Oracle Corporation)");
    }

    @Test
    public void multiReleaseManifestValueMustBeTrue() throws Exception {
        File falseJarFile = createMultiReleaseJar("false");
        try (net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(falseJarFile)) {
            assert new String(IOUtils.toByteArray(jarFile.getInputStream("sample.txt")), StandardCharsets.UTF_8).equals("base");
        }

        File trueJarFile = createMultiReleaseJar("true");
        try (net.hasor.cobble.loader.jar.JarFile jarFile = new net.hasor.cobble.loader.jar.JarFile(trueJarFile)) {
            assert new String(IOUtils.toByteArray(jarFile.getInputStream("sample.txt")), StandardCharsets.UTF_8).equals("version9");
        }
    }

    private File createMultiReleaseJar(String multiRelease) throws Exception {
        File jarFile = File.createTempFile("cobble-mr-", ".jar");
        jarFile.deleteOnExit();

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Multi-Release", multiRelease);

        try (JarOutputStream output = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            output.putNextEntry(new JarEntry("sample.txt"));
            output.write("base".getBytes(StandardCharsets.UTF_8));
            output.closeEntry();

            output.putNextEntry(new JarEntry("META-INF/versions/9/sample.txt"));
            output.write("version9".getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }
        return jarFile;
    }
}
