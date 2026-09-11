/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.function;
/**
 * 表示一个释放操作。
 * @author 赵永春 (zyc@hasor.net)
 * @version 2026-03-09
 */
@FunctionalInterface
public interface Release {
    void release();
}
