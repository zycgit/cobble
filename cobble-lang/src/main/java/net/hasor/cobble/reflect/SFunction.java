/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.reflect;
import java.io.Serializable;
import java.util.function.Function;

/**
 * 辅助 lambda 写法的工具接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-10-31
 */
public interface SFunction<T> extends Function<T, Object>, Serializable {
}
