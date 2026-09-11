/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.function;
import java.lang.reflect.Field;

/**
 * Property 定义了属性的两个操作方法。
 * @author 赵永春 (zyc@hasor.net)
 * @version 2021-10-20
 */
public interface Property {
    boolean isReadOnly();

    Field getField();

    Object get(Object instance);

    void set(Object instance, Object value);
}
