/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package net.hasor.cobble.loader.jar;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.io.IOUtils;
import org.junit.Test;

/**
 * Tests for {@link JarFile}.
 */
public class JarHandlerTests {

    @Test
    public void handler_1() throws Exception {
        JarFile.registerUrlProtocolHandler();

        byte[] byteDat1 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/basic.jar/app/lib/Foo.class"));
        byte[] byteDat2 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-1.jar/app/onlyjar/FooOnlyJar.class"));
        byte[] byteDat3 = IOUtils.toByteArray(ResourcesUtils.getResourceAsStream("/dir/app-2.zip/app/onlyzip/FooOnlyZip.class"));

        String testResourcesDir = new File(new File("src/test/resources").getAbsolutePath()).toURI().toString();
        URI jarEntry1 = new URI("jar:" + testResourcesDir + "/nested-jars/nested-package-all.jar!/basic.jar!/app/lib/Foo.class");
        URI jarEntry2 = new URI("jar:" + testResourcesDir + "/nested-jars/nested-package-all.jar!/app-1.jar!/app/onlyjar/FooOnlyJar.class");
        URI jarEntry3 = new URI("jar:" + testResourcesDir + "/nested-jars/nested-package-all.jar!/app-2.zip!/app/onlyzip/FooOnlyZip.class");

        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(jarEntry1.toURL().openStream()));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(jarEntry2.toURL().openStream()));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(jarEntry3.toURL().openStream()));
    }
}
