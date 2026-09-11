/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package net.hasor.cobble.loader.jar;
import java.io.File;
import java.util.Objects;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.io.IOUtils;
import org.junit.Test;

/**
 * use jar -cvf0 xxx.jar * to package jar.
 * Tests for {@link JarFile}.
 */
public class NestedJarTests {
    @Test
    public void nestedJar_1() throws Exception {
        String file = ResourcesUtils.getResource("/nested-jars/nested-package-all.jar").getFile();
        JarFile jarFile = new JarFile(new File(file));

        JarEntry jarEntry1 = jarFile.getJarEntry("basic.jar");
        JarEntry jarEntry2 = jarFile.getJarEntry("app-1.jar");
        JarEntry jarEntry3 = jarFile.getJarEntry("app-2.zip");

        JarFile nestedJar1 = jarFile.getNestedJarFile(jarEntry1);
        JarFile nestedJar2 = jarFile.getNestedJarFile(jarEntry2);
        JarFile nestedJar3 = jarFile.getNestedJarFile(jarEntry3);

        assert nestedJar1.getJarEntry("app/lib/Foo.class") != null;
        assert nestedJar2.getJarEntry("app/onlyjar/FooOnlyJar.class") != null;
        assert nestedJar3.getJarEntry("app/onlyzip/FooOnlyZip.class") != null;
    }

    @Test
    public void nestedJar_2() throws Exception {
        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/basic.jar/app/lib/Foo.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/app/onlyjar/FooOnlyJar.class"));
        byte[] byteDat3 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/onlyzip/FooOnlyZip.class"));

        String file = ResourcesUtils.getResource("/nested-jars/nested-package-all.jar").getFile();
        JarFile jarFile = new JarFile(new File(file));

        JarEntry jarEntry1 = jarFile.getJarEntry("basic.jar");
        JarEntry jarEntry2 = jarFile.getJarEntry("app-1.jar");
        JarEntry jarEntry3 = jarFile.getJarEntry("app-2.zip");

        JarFile nestedJar1 = jarFile.getNestedJarFile(jarEntry1);
        JarFile nestedJar2 = jarFile.getNestedJarFile(jarEntry2);
        JarFile nestedJar3 = jarFile.getNestedJarFile(jarEntry3);

        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(nestedJar1.getInputStream(nestedJar1.getJarEntry("app/lib/Foo.class"))));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(nestedJar2.getInputStream(nestedJar2.getJarEntry("app/onlyjar/FooOnlyJar.class"))));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(nestedJar3.getInputStream(nestedJar3.getJarEntry("app/onlyzip/FooOnlyZip.class"))));
    }
}
