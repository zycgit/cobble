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
package net.hasor.cobble.loader.providers;
/**
 * 汇聚多个 ResourceLoader 之后它们之间的类加载的策略
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public interface ClassLoaderStrategy {
    /** 隔离策略，多个资源加载器汇聚在一起之后仅仅起到了一个门户的作用。彼此之间 classloader 不共享。 */
    int ISOLATION = 0;
    /** 合并策略，多个资源加载器汇聚在一起之后它们之间彼此共享同一个 classloader，相互之间类的引用是可见的。 */
    int MERGE     = 1;
}
