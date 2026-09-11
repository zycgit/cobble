/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.provider;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * 单例对象的{@link Supplier}封装形式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年7月8日
 */
public class SingleProvider<T> implements Provider<T> {
    private          Provider<T> provider = null;
    private volatile T           instance = null;
    private final    Object      lock     = new Object();

    public SingleProvider(T instance) {
        this.instance = instance;
    }

    public SingleProvider(Provider<T> provider) {
        this.provider = provider;
    }

    public SingleProvider(Supplier<T> supplier) {
        this.provider = supplier::get;
    }

    public SingleProvider(Callable<T> callable) {
        this.provider = callable::call;
    }

    @Override
    public T call() throws Exception {
        if (this.instance == null) {
            synchronized (this.lock) {
                if (this.instance == null) {
                    this.instance = newInstance(this.provider);
                }
            }
        }
        return this.instance;
    }

    protected T newInstance(Supplier<T> provider) {
        return provider.get();
    }

    public String toString() {
        return "SingleProvider->" + provider.toString();
    }
}
