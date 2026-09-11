/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.function;
/**
 * @author 赵永春 (zyc@hasor.net)
 * @version 2024-01-08
 */
@FunctionalInterface
public interface Callable {
    /** run */
    void run() throws Exception;
}
