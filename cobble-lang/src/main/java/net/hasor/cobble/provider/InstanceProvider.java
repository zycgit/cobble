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
 * 对象的{@link Supplier}封装形式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年7月8日
 */
public class InstanceProvider<T> implements Provider<T> {
    private T instance = null;

    public InstanceProvider(final T instance) {
        this.instance = instance;
    }

    @Override
    public T call() throws Exception {
        return this.instance;
    }

    public void set(T instance) {
        this.instance = instance;
    }
}
