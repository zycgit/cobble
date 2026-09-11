/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.concurrent.future;
import java.util.EventListener;
import java.util.concurrent.Future;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年11月15日
 */
@FunctionalInterface
public interface FutureListener<F extends Future<?>> extends EventListener {
    /**
     * Invoked when the operation associated with the {@link Future} has been completed.
     * @param future the source {@link Future} which called this callback
     */
    void operationComplete(F future);
}
