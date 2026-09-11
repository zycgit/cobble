/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
import java.io.IOException;

/**
 * 扫描classpath时找到资源的回调接口方法
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public interface Scanner<T> {
    /**
     * 找了一个资源
     * @param event 找到资源事件。
     */
    T found(ScanEvent event) throws IOException;
}
