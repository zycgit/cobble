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
import net.hasor.cobble.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.Objects;

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

        String testResourcesDir = new File("src/test/resources").getAbsolutePath();
        URI jarEntry1 = new URI("jar:file://" + testResourcesDir + "/nested-jars/nested-package-all.jar!/basic.jar!/app/lib/Foo.class");
        URI jarEntry2 = new URI("jar:file://" + testResourcesDir + "/nested-jars/nested-package-all.jar!/app-1.jar!/app/onlyjar/FooOnlyJar.class");
        URI jarEntry3 = new URI("jar:file://" + testResourcesDir + "/nested-jars/nested-package-all.jar!/app-2.zip!/app/onlyzip/FooOnlyZip.class");

        assert Objects.deepEquals(byteDat1, IOUtils.toByteArray(jarEntry1.toURL().openStream()));
        assert Objects.deepEquals(byteDat2, IOUtils.toByteArray(jarEntry2.toURL().openStream()));
        assert Objects.deepEquals(byteDat3, IOUtils.toByteArray(jarEntry3.toURL().openStream()));
    }
}
