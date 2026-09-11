/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.hasor.cobble.BeanUtils;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.dynamic.anno.CommonAopInterceptor;
import net.hasor.cobble.dynamic.beans.PropertyBean;
import org.junit.Test;

public class ProxyTest {
    @Test
    public void aopTest1() {
        PropertyBean propertyBean = new PropertyBean();
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        PropertyBean instance = Proxy.newProxyInstance(propertyBean, t -> true, aopInterceptor);

        assert Proxy.isProxyObject(instance);
        assert Proxy.isProxyClass(instance.getClass());
        assert Proxy.getPrototypeType(instance) == PropertyBean.class;
        assert Proxy.getPrototypeType(instance.getClass()) == PropertyBean.class;

        assert !Proxy.isProxyClass(PropertyBean.class);
        assert !Proxy.isProxyObject(new PropertyBean());

        instance.setByteValue((byte) 1);
        assert aopInterceptor.getCallInfo().get("setByteValue").size() == 2;
        assert aopInterceptor.getCallInfo().get("setByteValue").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("setByteValue").contains("AFTER");
        assert propertyBean.getByteValue() == 1;

        instance.setByteValue((byte) 1);
        assert propertyBean.getByteValue() == 1;
        instance.setByteValue2(null);
        assert propertyBean.getByteValue2() == null;
        instance.setByteValue2((byte) 2);
        assert propertyBean.getByteValue2() == 2;

        instance.setShortValue((short) 2);
        assert propertyBean.getShortValue() == 2;
        instance.setShortValue2((short) 2);
        assert propertyBean.getShortValue2() == 2;
        instance.setShortValue2(null);
        assert propertyBean.getShortValue2() == null;

        instance.setIntValue(3);
        assert propertyBean.getIntValue() == 3;
        instance.setIntValue2(3);
        assert propertyBean.getIntValue2() == 3;
        instance.setIntValue2(null);
        assert propertyBean.getIntValue2() == null;

        instance.setLongValue(4);
        assert propertyBean.getLongValue() == 4;
        instance.setLongValue2((long) 4);
        assert propertyBean.getLongValue2() == 4;
        instance.setLongValue2(null);
        assert propertyBean.getLongValue2() == null;

        instance.setFloatValue(5.5f);
        assert propertyBean.getFloatValue() == 5.5f;
        instance.setFloatValue2(5.5f);
        assert propertyBean.getFloatValue2() == 5.5f;
        instance.setFloatValue2(null);
        assert propertyBean.getFloatValue2() == null;

        instance.setDoubleValue(6.6d);
        assert propertyBean.getDoubleValue() == 6.6d;
        instance.setDoubleValue2(6.6d);
        assert propertyBean.getDoubleValue2() == 6.6d;
        instance.setDoubleValue2(null);
        assert propertyBean.getDoubleValue2() == null;

        instance.setBooleanValue(true);
        assert propertyBean.isBooleanValue();
        instance.setBooleanValue2(true);
        assert propertyBean.getBooleanValue2();
        instance.setBooleanValue2(null);
        assert propertyBean.getBooleanValue2() == null;

        instance.setCharValue('$');
        assert propertyBean.getCharValue() == '$';
        instance.setCharValue2('#');
        assert propertyBean.getCharValue2() == '#';
        instance.setCharValue2(null);
        assert propertyBean.getCharValue2() == null;

        instance.setStringValue("abc");
        assert propertyBean.getStringValue().equals("abc");
    }

    @Test
    public void aopTest2() throws Exception {
        final List<Object> data = new ArrayList<>();

        DynamicConfig config = new DynamicConfig(PropertyBean.class);
        config.addImplements(MethodInterceptor.class, (proxy, method, args) -> null);
        config.addImplements(List.class, (proxy, method, args) -> {
            data.add(args[0]);
            return true;
        });
        config.addImplements(Map.class, (proxy, method, args) -> null);
        config.setDebugOutputDir(new File("target/proxy_" + System.currentTimeMillis()));
        config.getDebugOutputDir().deleteOnExit();

        Class<?> instance = Proxy.buildProxyClass(config);
        List list = (List) ClassUtils.newInstance(instance);
        list.add("abc");
        list.add("bcd");

        assert data.size() == 2;
        assert data.contains("abc");
        assert data.contains("bcd");
    }

    @Test
    public void jdkProxyStyle1() {
        final List<Object> data = new ArrayList<>();

        List list = (List) Proxy.newProxyInstance(new Class[] { List.class }, (proxy, method, args) -> {
            data.add(args[0]);
            return true;
        });
        list.add("abc");
        list.add("bcd");

        assert data.size() == 2;
        assert data.contains("abc");
        assert data.contains("bcd");
    }

    @Test
    public void jdkProxyStyle2() {
        final List<Object> data = new ArrayList<>();
        data.add("abc");
        data.add("bcd");

        List list = (List) Proxy.newProxyInstance(new Class[] { List.class }, (proxy, method, args) -> {
            if (method.getName().equals("size")) {
                return data.size();
            } else {
                return BeanUtils.getDefaultValue(method.getReturnType());
            }
        });

        assert list.size() == 2;
    }

    @Test
    public void jdkProxyStyleWithLargeInterface() throws Exception {
        ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(new Class[] { ResultSet.class }, (proxy, method, args) -> {
            if (method.getName().equals("getFetchSize")) {
                return 16;
            } else {
                return BeanUtils.getDefaultValue(method.getReturnType());
            }
        });

        assert resultSet.getFetchSize() == 16;
    }
}
