/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.loader;
import java.io.IOException;

/**
 * 扫描classpath时找到资源的回调接口方法
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Scanner<T> {
    /**
     * 找到资源(返回值为true表示找到预期的资源结束扫描，false表示继续扫描剩下的资源)
     * @param event 找到资源事件。
     */
    public T found(ScanEvent event) throws IOException;
}