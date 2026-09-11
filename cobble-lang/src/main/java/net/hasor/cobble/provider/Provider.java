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

/**
 * 提供者实现这个接口就相当于同时实现了
 * <li>java.util.function.Supplier</li>
 * <li>java.util.concurrent.Callable</li>
 * 三个接口
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2014年5月22日
 */
@FunctionalInterface
public interface Provider<T> extends Supplier<T>, Callable<T> {
    /**
     * Gets a result.
     * @return a result
     */
    default T get() {
        try {
            return this.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    T call() throws Exception;

    default Provider<T> asSingle() {
        if (this instanceof SingleProvider) {
            return this;
        } else {
            return new SingleProvider<>(this);
        }
    }

    default Provider<T> asThread() {
        if (this instanceof ThreadSingleProvider) {
            return this;
        } else {
            return new ThreadSingleProvider<>(this);
        }
    }

    default Provider<T> asLoader() {
        if (this instanceof LoaderSingleProvider) {
            return this;
        } else {
            return new LoaderSingleProvider<>(this);
        }
    }

    static <T> Provider<T> of(T instance) {
        return new SingleProvider<>(instance);
    }

    static <T> Provider<T> of(Supplier<T> supplier) {
        return supplier::get;
    }

    static <T> Provider<T> of(Callable<T> callable) {
        return callable::call;
    }

    static <T> Provider<T> wrap(T target) {
        return new InstanceProvider<>(target);
    }

    static <T> Provider<T> asThread(T target) {
        return new ThreadSingleProvider<>(target);
    }

    static <T> Provider<T> asLoader(T target) {
        return new LoaderSingleProvider<>(target);
    }

    static <T> Provider<T> toSingle(Supplier<T> supplier) {
        if (supplier instanceof SingleProvider || supplier instanceof InstanceProvider) {
            return (Provider<T>) supplier;
        } else {
            return new SingleProvider<>(supplier);
        }
    }

    static <T> Provider<T> toThread(Supplier<T> supplier) {
        if (supplier instanceof ThreadSingleProvider) {
            return (Provider<T>) supplier;
        } else {
            return new ThreadSingleProvider<>(supplier);
        }
    }

    static <T> Provider<T> toLoader(Supplier<T> supplier) {
        if (supplier instanceof LoaderSingleProvider) {
            return (Provider<T>) supplier;
        } else {
            return new LoaderSingleProvider<>(supplier);
        }
    }

    static <T> Provider<T> toSingle(Callable<T> callable) {
        if (callable instanceof SingleProvider || callable instanceof InstanceProvider) {
            return (Provider<T>) callable;
        } else {
            return new SingleProvider<>(callable);
        }
    }

    static <T> Provider<T> toThread(Callable<T> callable) {
        if (callable instanceof ThreadSingleProvider) {
            return (Provider<T>) callable;
        } else {
            return new ThreadSingleProvider<>(callable);
        }
    }

    static <T> Provider<T> toLoader(Callable<T> callable) {
        if (callable instanceof LoaderSingleProvider) {
            return (Provider<T>) callable;
        } else {
            return new LoaderSingleProvider<>(callable);
        }
    }
}
