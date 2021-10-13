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
package net.hasor.cobble.loader.isol;
import net.hasor.cobble.loader.providers.ImportResourceLoader;

/**
 * 继承自 ImportResourceLoader 并在类隔离机制上提供保证。
 *
 * <p>IsolResourceLoader 解决了 ImportResourceLoader 当处理 import 的类依赖了未被 import 的包时无法正确处理类加载的问题。</p>
 * @version : 2021-10-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class IsolResourceLoader extends ImportResourceLoader {
    @Override
    protected ClassLoader createBindClassLoader(ClassLoader bindParentLoader) {
        return new IsolClassLoader(this, bindParentLoader);
    }
}
