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
import java.util.Set;

/**
 *
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ClassFinder {
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param packagePath 要扫描的包名。
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(final String packagePath, final Class<?> compareType);

    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param loadPackages 要扫描的包名。
     * @param featureType 要查找的特征。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(final String[] loadPackages, final Class<?> featureType);

    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(final Class<?> compareType);
}
