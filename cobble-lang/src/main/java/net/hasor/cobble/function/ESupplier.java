/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.function;
import java.util.function.Supplier;
import net.hasor.cobble.ExceptionUtils;

/**
 * Supplier 允许异常抛出。
 * @author 赵永春 (zyc@hasor.net)
 * @version 2021-01-23
 */
@FunctionalInterface
public interface ESupplier<T, E extends Throwable> extends Supplier<T> {
    @Override
    default T get() {
        try {
            return this.eGet();
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    T eGet() throws E;
}
