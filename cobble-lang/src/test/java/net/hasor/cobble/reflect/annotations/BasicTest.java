/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.reflect.annotations;

import java.io.IOException;
import net.hasor.cobble.reflect.Annotations;
import org.junit.Test;

public class BasicTest {
    @Test
    public void test() throws IOException {
        Annotations annotations = Annotations.ofClass(TestBean.class);
        assert annotations.getAnnotation(ResultMap.class) != null;
    }

    @ResultMap()
    class TestBean {
    }
}
