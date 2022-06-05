/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.cobble.dynamic;

import java.util.function.Supplier;

/**
 *
 * @version : 2020-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerDynamicProperty {
    public static void setProperty(boolean isProxy, DynamicConfig dynamicConfig, String cacheKey, Object target, String name, Object newValue) throws Throwable {
        Supplier<? extends DynamicProperty> supplier = dynamicConfig.findDynamicProperty(name);
        DynamicProperty dynamicProperty = null;
        if (supplier != null) {
            dynamicProperty = supplier.get();
        }
        if (dynamicProperty == null) {
            throw new IllegalArgumentException(cacheKey + " missing Delegate.");
        }

        dynamicProperty.set(target, newValue);
    }

    public static Object getProperty(boolean isProxy, DynamicConfig dynamicConfig, String cacheKey, Object target, String name) throws Throwable {
        Supplier<? extends DynamicProperty> supplier = dynamicConfig.findDynamicProperty(name);
        DynamicProperty dynamicProperty = null;
        if (supplier != null) {
            dynamicProperty = supplier.get();
        }
        if (dynamicProperty == null) {
            throw new IllegalArgumentException(cacheKey + " missing Delegate.");
        }

        return dynamicProperty.get(target);
    }
}
