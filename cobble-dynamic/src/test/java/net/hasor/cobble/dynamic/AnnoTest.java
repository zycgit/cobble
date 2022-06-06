package net.hasor.cobble.dynamic;
import net.hasor.cobble.dynamic.anno.aop.AopBean;
import net.hasor.cobble.dynamic.anno.aop.ClassAnnoInterceptor;
import net.hasor.cobble.dynamic.anno.aop.MethodAnnoInterceptor;
import net.hasor.cobble.dynamic.anno.custom.MethodMyAopBean;
import net.hasor.cobble.dynamic.anno.custom.MyAopInterceptor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AnnoTest {
    @Test
    public void aopTest1() throws Exception {
        ClassAnnoInterceptor.reset();
        MethodAnnoInterceptor.reset();

        Class<?> aClass = Proxy.newEnhanceClass(Thread.currentThread().getContextClassLoader(), AopBean.class);
        AopBean instance = (AopBean) aClass.newInstance();
        assert instance instanceof DynamicClass;

        instance.doInit(new ArrayList<>());
        assert MethodAnnoInterceptor.getCallInfo().get("doInit") == null;
        assert ClassAnnoInterceptor.getCallInfo().get("doInit").size() == 2;

        List<?> objects = instance.checkBaseType(true, (byte) 1, (short) 2, 3, 4, 5.0f, 6.0d, 'c');
        assert objects.size() == 8;
        assert (Boolean) objects.get(0);
        assert (Byte) objects.get(1) == 1;
        assert (Short) objects.get(2) == 2;
        assert (Integer) objects.get(3) == 3;
        assert (Long) objects.get(4) == 4;
        assert (Float) objects.get(5) == 5.0f;
        assert (Double) objects.get(6) == 6.0d;
        assert (Character) objects.get(7) == 'c';

        assert instance.aBooleanValue(true) &&//
                ClassAnnoInterceptor.getCallInfo().get("aBooleanValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aBooleanValue").size() == 2;

        assert instance.aByteValue((byte) 1) == 1 &&//
                ClassAnnoInterceptor.getCallInfo().get("aByteValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aByteValue").size() == 2;

        assert instance.aShort((short) 2) == 2 &&//
                ClassAnnoInterceptor.getCallInfo().get("aShort").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aShort").size() == 2;

        assert instance.aIntValue(3) == 3 &&//
                ClassAnnoInterceptor.getCallInfo().get("aIntValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aIntValue").size() == 2;

        assert instance.aLongValue(4) == 4 &&//
                ClassAnnoInterceptor.getCallInfo().get("aLongValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aLongValue").size() == 2;

        assert instance.aFloatValue(5.5f) == 5.5f &&//
                ClassAnnoInterceptor.getCallInfo().get("aFloatValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aFloatValue").size() == 2;

        assert instance.aDoubleValue(6.0d) == 6.0d &&//
                ClassAnnoInterceptor.getCallInfo().get("aLongValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aLongValue").size() == 2;

        assert instance.aCharValue('a') == 'a' &&//
                ClassAnnoInterceptor.getCallInfo().get("aCharValue").size() == 2 &&//
                MethodAnnoInterceptor.getCallInfo().get("aCharValue").size() == 2;
    }

    @Test
    public void aopTest2() throws Exception {
        MyAopInterceptor.resetInit();
        assert !MyAopInterceptor.isCalled();
        Class<?> withMethodType = Proxy.newEnhanceClass(Thread.currentThread().getContextClassLoader(), MethodMyAopBean.class);
        MethodMyAopBean withMethodInstance = (MethodMyAopBean) withMethodType.newInstance();
        assert withMethodInstance.fooCall("abc").equals("call back : abc");
        assert MyAopInterceptor.isCalled();
        //
        //
        //
        MyAopInterceptor.resetInit();
        assert !MyAopInterceptor.isCalled();
        Class<?> withClassType = Proxy.newEnhanceClass(Thread.currentThread().getContextClassLoader(), MethodMyAopBean.class);
        MethodMyAopBean withClassInstance = (MethodMyAopBean) withClassType.newInstance();
        assert withClassInstance.fooCall("abc").equals("call back : abc");
        assert MyAopInterceptor.isCalled();
    }

}
