/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.provider;
import java.util.function.Supplier;

/**
 * 多例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2015年6月28日
 */
public class PrototypeScope implements Scope {
    public final static PrototypeScope PROTOTYPE = new PrototypeScope();

    @Override
    public <T> Supplier<T> scope(Object key, Supplier<T> provider) {
        return provider;
    }
}
