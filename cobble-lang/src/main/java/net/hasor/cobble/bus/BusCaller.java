/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.bus;
/**
 * 决定如何执行 BusListener 和执行后的返回值
 * @author 赵永春 (zyc@hasor.net)
 * @version 2019年06月20日
 */
public interface BusCaller {
    Object doEvent(BusListener listener, Object lastResult) throws Throwable;
}
