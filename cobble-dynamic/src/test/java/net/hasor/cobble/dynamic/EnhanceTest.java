/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.io.File;
import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.dynamic.anno.CommonAopInterceptor;
import net.hasor.cobble.dynamic.beans.*;
import org.junit.Test;

public class EnhanceTest {
    @Test
    public void aopTest1() throws Exception {
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        DynamicConfig classConfig = new DynamicConfig(PropertyBean.class);
        classConfig.addAopInterceptor(t -> true, aopInterceptor);
        classConfig.addProperty("aacc", String.class);
        Class<?> buildClass = Proxy.buildProxyClass(classConfig);
        PropertyBean instance = (PropertyBean) ClassUtils.newInstance(buildClass);

        assert !Proxy.isProxyObject(instance);
        assert !Proxy.isProxyClass(instance.getClass());
        assert Proxy.isDynamicObject(instance);
        assert Proxy.isDynamicClass(instance.getClass());

        assert Proxy.getPrototypeType(instance) == PropertyBean.class;
        assert Proxy.getPrototypeType(instance.getClass()) == PropertyBean.class;

        assert !Proxy.isProxyClass(PropertyBean.class);
        assert !Proxy.isProxyObject(new PropertyBean());

        instance.setByteValue((byte) 1);
        assert aopInterceptor.getCallInfo().get("setByteValue").size() == 2;
        assert aopInterceptor.getCallInfo().get("setByteValue").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("setByteValue").contains("AFTER");
        assert instance.getByteValue() == 1;

        instance.setByteValue((byte) 1);
        assert instance.getByteValue() == 1;
        instance.setByteValue2(null);
        assert instance.getByteValue2() == null;
        instance.setByteValue2((byte) 2);
        assert instance.getByteValue2() == 2;

        instance.setShortValue((short) 2);
        assert instance.getShortValue() == 2;
        instance.setShortValue2((short) 2);
        assert instance.getShortValue2() == 2;
        instance.setShortValue2(null);
        assert instance.getShortValue2() == null;

        instance.setIntValue(3);
        assert instance.getIntValue() == 3;
        instance.setIntValue2(3);
        assert instance.getIntValue2() == 3;
        instance.setIntValue2(null);
        assert instance.getIntValue2() == null;

        instance.setLongValue(4);
        assert instance.getLongValue() == 4;
        instance.setLongValue2((long) 4);
        assert instance.getLongValue2() == 4;
        instance.setLongValue2(null);
        assert instance.getLongValue2() == null;

        instance.setFloatValue(5.5f);
        assert instance.getFloatValue() == 5.5f;
        instance.setFloatValue2(5.5f);
        assert instance.getFloatValue2() == 5.5f;
        instance.setFloatValue2(null);
        assert instance.getFloatValue2() == null;

        instance.setDoubleValue(6.6d);
        assert instance.getDoubleValue() == 6.6d;
        instance.setDoubleValue2(6.6d);
        assert instance.getDoubleValue2() == 6.6d;
        instance.setDoubleValue2(null);
        assert instance.getDoubleValue2() == null;

        instance.setBooleanValue(true);
        assert instance.isBooleanValue();
        instance.setBooleanValue2(true);
        assert instance.getBooleanValue2();
        instance.setBooleanValue2(null);
        assert instance.getBooleanValue2() == null;

        instance.setCharValue('$');
        assert instance.getCharValue() == '$';
        instance.setCharValue2('#');
        assert instance.getCharValue2() == '#';
        instance.setCharValue2(null);
        assert instance.getCharValue2() == null;

        instance.setStringValue("abc");
        assert instance.getStringValue().equals("abc");
    }

    @Test
    public void aopTest2() throws Exception {
        Class[] initTypes = new Class[] {   //
                Byte.TYPE                   //
                , Byte.class                //
                , Short.TYPE                //
                , Short.class               //
                , Integer.TYPE              //
                , Integer.class             //
                , Long.TYPE                 //
                , Long.class                //
                , Float.TYPE                //
                , Float.class               //
                , Double.TYPE               //
                , Double.class              //
                , Boolean.TYPE              //
                , Boolean.class             //
                , Character.TYPE            //
                , Character.class           //
                , Date.class                //
                , java.sql.Date.class       //
                , Time.class                //
                , Timestamp.class           //
                , String.class              //
                , SelectEnum.class          //
        };
        Object[] initParams = new Object[] {//
                (byte) 1                    //
                , (byte) 1                  //
                , (short) 2                 //
                , (short) 2                 //
                , 3                   //
                , 3                   //
                , (long) 4                  //
                , (long) 4                  //
                , (float) 5.5               //
                , (float) 5.5               //
                , 6.6              //
                , 6.6              //
                , true            //
                , true            //
                , 'a'                //
                , 'a'                //
                , new Date()                //
                , new java.sql.Date(System.currentTimeMillis())     //
                , new Time(System.currentTimeMillis())              //
                , new Timestamp(System.currentTimeMillis())         //
                , "abc"                                             //
                , SelectEnum.Three                                  //
        };

        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        Class<?> buildClass = Proxy.newProxyClass(ConstructorBean.class, t -> true, aopInterceptor);
        ConstructorBean instance = (ConstructorBean) buildClass.getConstructor(initTypes).newInstance(initParams);

        assert !Proxy.isProxyObject(instance);
        assert !Proxy.isProxyClass(instance.getClass());
        assert Proxy.isDynamicObject(instance);
        assert Proxy.isDynamicClass(instance.getClass());

        assert Proxy.getPrototypeType(instance) == ConstructorBean.class;
        assert Proxy.getPrototypeType(instance.getClass()) == ConstructorBean.class;

        assert !Proxy.isProxyClass(ConstructorBean.class);

        assert instance.getByteValue() == 1;
        assert aopInterceptor.getCallInfo().get("getByteValue").size() == 2;
        assert aopInterceptor.getCallInfo().get("getByteValue").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("getByteValue").contains("AFTER");

        assert instance.getByteValue() == 1;
        assert instance.getByteValue2() == 1;

        assert instance.getShortValue() == 2;
        assert instance.getShortValue2() == 2;

        assert instance.getIntValue() == 3;
        assert instance.getIntValue2() == 3;

        assert instance.getLongValue() == 4;
        assert instance.getLongValue2() == 4;

        assert instance.getFloatValue() == 5.5f;
        assert instance.getFloatValue2() == 5.5f;

        assert instance.getDoubleValue() == 6.6d;
        assert instance.getDoubleValue2() == 6.6d;

        assert instance.isBooleanValue();
        assert instance.getBooleanValue2();

        assert instance.getCharValue() == 'a';
        assert instance.getCharValue2() == 'a';

        assert instance.getStringValue().equals("abc");
    }

    @Test
    public void aopTest3() throws Exception {
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        Class<?> buildClass = Proxy.newProxyClass(PropertyBean.class, t -> true, aopInterceptor);
        PropertyBean instance = (PropertyBean) ClassUtils.newInstance(buildClass);

        ClassLoader loader = instance.getClass().getClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(instance.getClass().getName().replace(".", "/") + ".class");

        assert resourceAsStream == null; // DynamicClass not support get class.
        assert buildClass.getName().equals(instance.getClass().getName());
        assert buildClass.getName().contains(PropertyBean.class.getName() + "$proxy$");
    }

    @Test
    public void aopTest4() throws Exception {
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        DynamicConfig classConfig = new DynamicConfig(PropertyBean.class);
        classConfig.addAopInterceptor(t -> true, aopInterceptor);
        classConfig.setDebugOutputDir(new File("target/proxy_" + System.currentTimeMillis()));
        classConfig.getDebugOutputDir().deleteOnExit();

        Class<?> buildClass = Proxy.buildProxyClass(classConfig);
        PropertyBean instance = (PropertyBean) ClassUtils.newInstance(buildClass);

        String fullName = buildClass.getName().replace(".", String.valueOf(File.separatorChar)) + ".class";
        assert new File(classConfig.getDebugOutputDir(), fullName).exists();
        assert buildClass.getName().equals(instance.getClass().getName());
        assert buildClass.getName().contains(PropertyBean.class.getName() + "$proxy$");
    }

    @Test
    public void aopTest5() throws Exception {
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        Class<?> buildClass = Proxy.newProxyClass(ThrowAopBean.class, t -> true, aopInterceptor);
        ThrowAopBean instance = (ThrowAopBean) ClassUtils.newInstance(buildClass);

        try {
            instance.fooCall("abc");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("abc");
            assert e.getCause().getMessage().equals("abc");
            assert !e.getMessage().equals("abc");
        }

        assert aopInterceptor.getCallInfo().get("fooCall").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall").contains("THROW");
    }

    @Test
    public void aopTest6() throws Exception {
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        Class<?> buildClass = Proxy.newProxyClass(GenericsMethodAopBean.class, t -> true, aopInterceptor);
        GenericsMethodAopBean instance = (GenericsMethodAopBean) ClassUtils.newInstance(buildClass);

        instance.fooCall1("abc1", "abc2", "abc3");
        assert aopInterceptor.getCallInfo().get("fooCall1").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall1").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall1").contains("AFTER");

        instance.fooCall2(new Date(), new ArrayList());
        assert aopInterceptor.getCallInfo().get("fooCall2").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall2").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall2").contains("AFTER");

        instance.fooCall3(Date.class, new ArrayList<>());
        assert aopInterceptor.getCallInfo().get("fooCall3").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall3").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall3").contains("AFTER");
    }

    @Test
    public void aopTest7() throws Exception {
        CommonAopInterceptor aopInterceptor = new CommonAopInterceptor();
        Class<?> buildClass = Proxy.newProxyClass(GenericsMethodAopBean.class, method -> true, aopInterceptor);
        GenericsMethodAopBean instance = (GenericsMethodAopBean) ClassUtils.newInstance(buildClass);

        instance.fooCall1("abc1", "abc2", "abc3");
        assert aopInterceptor.getCallInfo().get("fooCall1").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall1").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall1").contains("AFTER");

        instance.fooCall2(new Date(), new ArrayList());
        assert aopInterceptor.getCallInfo().get("fooCall2").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall2").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall2").contains("AFTER");

        instance.fooCall3(Date.class, new ArrayList<>());
        assert aopInterceptor.getCallInfo().get("fooCall3").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall3").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall3").contains("AFTER");
    }
}
