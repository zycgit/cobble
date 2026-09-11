/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.function;
import java.util.function.Function;
import net.hasor.cobble.ExceptionUtils;

/**
 * Function 允许异常抛出。
 * @author 赵永春 (zyc@hasor.net)
 * @version 2021-01-23
 */
@FunctionalInterface
public interface EFunction<T, R, E extends Throwable> extends Function<T, R> {
    @Override
    default R apply(T t) {
        try {
            return this.eApply(t);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    R eApply(T t) throws E;
}
