/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-4-13
 */
public class InvokerImplementInvocation implements MethodInvocation {
    private final boolean           isProxy;
    private final Method            proxyMethod;
    private final Class<?>          implType;
    private final Method            targetMethod;
    private final Object            targetObject;
    private final Object[]          paramObjects;
    private final InvocationHandler proceedingChain;

    public InvokerImplementInvocation(boolean isProxy, final Class<?> implType, final Method targetMethod, final Method proxyMethod, final DynamicConfig dynamicConfig, final Object targetObject, Object[] methodParams) {
        this.isProxy = isProxy;
        this.implType = implType;
        this.proxyMethod = proxyMethod;
        this.targetMethod = targetMethod;
        this.targetObject = targetObject;
        this.paramObjects = methodParams;
        this.proceedingChain = dynamicConfig.getImplementMap().get(implType);
    }

    @Override
    public boolean isProxy() {
        return this.isProxy;
    }

    @Override
    public Method getMethod() {
        return this.proxyMethod;
    }

    @Override
    public Object[] getArguments() {
        return this.paramObjects;
    }

    @Override
    public Object proceed() throws Throwable {
        return this.proceedingChain.invoke(this.targetObject, this.proxyMethod, this.paramObjects);
    }

    @Override
    public Object getThis() {
        return this.targetObject;
    }
}
