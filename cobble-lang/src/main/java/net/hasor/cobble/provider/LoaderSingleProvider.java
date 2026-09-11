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
import net.hasor.cobble.ExceptionUtils;
import net.hasor.cobble.ref.ContextClassLoaderLocal;

/**
 * ClassLoader单例对象的{@link Supplier}封装形式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年7月8日
 */
public class LoaderSingleProvider<T> implements Provider<T> {
    private final ContextClassLoaderLocal<T> instance;

    public LoaderSingleProvider(final T instance) {
        this.instance = new ContextClassLoaderLocal<T>() {
            @Override
            protected T initialValue() {
                return instance;
            }
        };
    }

    public LoaderSingleProvider(final Provider<T> provider) {
        this.instance = new ContextClassLoaderLocal<T>() {
            @Override
            protected T initialValue() {
                return provider.get();
            }
        };
    }

    public LoaderSingleProvider(final Supplier<T> supplier) {
        this.instance = new ContextClassLoaderLocal<T>() {
            @Override
            protected T initialValue() {
                return supplier.get();
            }
        };
    }

    public LoaderSingleProvider(final Callable<T> callable) {
        this.instance = new ContextClassLoaderLocal<T>() {
            @Override
            protected T initialValue() {
                try {
                    return callable.call();
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntime(e);
                }
            }
        };
    }

    @Override
    public T call() throws Exception {
        return this.instance.get();
    }

    public String toString() {
        return "ClassLoaderSingleProvider->" + instance.toString();
    }
}
