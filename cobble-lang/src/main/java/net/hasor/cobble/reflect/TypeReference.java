/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.reflect;
import net.hasor.cobble.ClassUtils;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-10-31
 */
public abstract class TypeReference<T> {
    protected Class<?> rawType;

    public TypeReference() {
        if (ClassUtils.getSuperClassGenricTypeCount(getClass()) > 0) {
            rawType = ClassUtils.getSuperClassGenricType(getClass(), 0);
        } else {
            rawType = null;
        }
    }

    public final Class<?> getRawType() {
        return this.rawType;
    }

    @Override
    public String toString() {
        return this.rawType.toString();
    }
}
