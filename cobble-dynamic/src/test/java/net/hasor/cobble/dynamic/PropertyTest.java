/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import net.hasor.cobble.BeanUtils;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.provider.Provider;
import org.junit.Test;

public class PropertyTest {
    @Test
    public void propertyTest1() throws Exception {
        SimpleDynamicProperty delegate = new SimpleDynamicProperty();
        DynamicConfig classConfig = new DynamicConfig();
        classConfig.addProperty("name", String.class, Provider.of(delegate));
        Class<?> buildClass = Proxy.buildProxyClass(classConfig);
        Object instance = ClassUtils.newInstance(buildClass);

        BeanUtils.writeProperty(instance, "name", "abc");
        assert BeanUtils.readProperty(instance, "name").equals("abc");
        assert delegate.getValue().equals("abc");
        BeanUtils.writeProperty(instance, "name", "def");
        assert BeanUtils.readProperty(instance, "name").equals("def");
        assert delegate.getValue().equals("def");
    }

    @Test
    public void propertyTest2() throws Exception {
        DynamicConfig classConfig = new DynamicConfig();
        classConfig.addProperty("name", Boolean.TYPE);
        Class<?> buildClass = Proxy.buildProxyClass(classConfig);
        Object instance = ClassUtils.newInstance(buildClass);

        BeanUtils.writeProperty(instance, "name", true);
        assert BeanUtils.readProperty(instance, "name").equals(true);
        BeanUtils.writeProperty(instance, "name", false);
        assert BeanUtils.readProperty(instance, "name").equals(false);
    }

    @Test
    public void propertyTest3() {
        DynamicConfig classConfig = new DynamicConfig();
        classConfig.addProperty("name", Boolean.TYPE, ReadWriteType.ReadOnly);
        Class<?> buildClass = Proxy.buildProxyClass(classConfig);
        assert BeanUtils.canReadProperty(buildClass, "name");
        assert !BeanUtils.canWriteProperty(buildClass, "name");
    }

    @Test
    public void propertyTest4() {
        DynamicConfig classConfig = new DynamicConfig();
        classConfig.addProperty("name", Boolean.TYPE, ReadWriteType.ReadWrite);
        Class<?> buildClass = Proxy.buildProxyClass(classConfig);
        assert BeanUtils.canReadProperty(buildClass, "name");
        assert BeanUtils.canWriteProperty(buildClass, "name");
    }
}
