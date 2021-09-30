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
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.loader.jar.JarFile;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Tests for {@link JarFile}.
 */
public class ClassResourceLoaderTests {
    @Test
    public void loadClassTest() throws IOException {
        ClassPathResourceLoader classLoader = new ClassPathResourceLoader();
        //
        assert classLoader.exist("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class");
        assert classLoader.getResource("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class") != null;
        assert classLoader.getResources("net/hasor/cobble/loader/providers/ClassPathResourceLoader.class").size() == 1;
        //
        Set<Class<?>> classSet = classLoader.toClassFinder().getClassSet("net.hasor.cobble", ResourceLoader.class);
        assert classSet.size() == 6;
        assert classSet.contains(ClassPathResourceLoader.class);
        assert classSet.contains(PathResourceLoader.class);
        assert classSet.contains(JarResourceLoader.class);
        assert classSet.contains(MultiResourceLoader.class);
        assert classSet.contains(AbstractResourceLoader.class);
        assert classSet.contains(ResourceLoader.class);
        //
        classSet = classLoader.toClassFinder().getClassSet(ResourceLoader.class);
        assert classSet.size() == 6;
        assert classSet.contains(ClassPathResourceLoader.class);
        assert classSet.contains(PathResourceLoader.class);
        assert classSet.contains(JarResourceLoader.class);
        assert classSet.contains(MultiResourceLoader.class);
        assert classSet.contains(AbstractResourceLoader.class);
        assert classSet.contains(ResourceLoader.class);
    }
}
