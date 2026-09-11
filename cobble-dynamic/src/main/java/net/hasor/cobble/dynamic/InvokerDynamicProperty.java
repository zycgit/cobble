/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.util.function.Supplier;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-29
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
