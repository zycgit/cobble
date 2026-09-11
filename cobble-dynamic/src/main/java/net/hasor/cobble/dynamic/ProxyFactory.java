/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-29
 */
public interface ProxyFactory<T> {
    T apply(T original) throws ReflectiveOperationException;
}
