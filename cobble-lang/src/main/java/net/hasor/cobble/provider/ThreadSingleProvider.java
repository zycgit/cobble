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
 * 线程单例对象的{@link Supplier}封装形式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年7月8日
 */
public class ThreadSingleProvider<T> implements Provider<T> {
    private final ThreadLocal<T> instance;

    public ThreadSingleProvider(final T instance) {
        this.instance = ThreadLocal.withInitial(() -> instance);
    }

    public ThreadSingleProvider(final Provider<T> provider) {
        this.instance = ThreadLocal.withInitial(provider);
    }

    public ThreadSingleProvider(final Supplier<T> supplier) {
        this.instance = ThreadLocal.withInitial(supplier);
    }

    public ThreadSingleProvider(final Callable<T> callable) {
        this.instance = ThreadLocal.withInitial((Provider<T>) callable::call);
    }

    @Override
    public T call() throws Exception {
        return this.instance.get();
    }

    public String toString() {
        return "ThreadSingleProvider->" + instance.toString();
    }
}
