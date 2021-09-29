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
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Test;

import java.io.File;

/**
 * Tests for {@link JarFile}.
 */
public class JarResourceLoaderTests {
    @Test
    public void loadClassTest() throws Exception {
        String zipFile = ResourcesUtils.getResource("/jars/app.zip").getFile();
        JarResourceLoader jarFile = new JarResourceLoader(new File(zipFile));
        ClassLoader classLoader = jarFile.toClassLoader();
        //
        Class<?> c = classLoader.loadClass("demo.Application");
        assert c != null;
    }
}
