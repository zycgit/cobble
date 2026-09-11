/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-4-13
 */
public class InvokerMethodInvocation implements MethodInvocation {
    private final MethodInterceptor[] interceptorDefinitions;
    private final MethodInvocation    proceedingChain;
    private       int                 index = -1;

    public InvokerMethodInvocation(boolean isProxy, String targetMethodDesc, final Method targetMethod, final Method proxyMethod, final DynamicConfig dynamicConfig, final Object targetObject, Object[] methodParams) {
        MethodInterceptor[] interceptors = dynamicConfig.findInterceptor(targetMethodDesc);
        this.interceptorDefinitions = interceptors == null ? new MethodInterceptor[0] : interceptors;
        this.proceedingChain = new InnerChainMethodInvocation(isProxy, proxyMethod, targetMethod, targetObject, methodParams);
    }

    @Override
    public boolean isProxy() {
        return this.proceedingChain.isProxy();
    }

    public Method getMethod() {
        return this.proceedingChain.getMethod();
    }

    public Object[] getArguments() {
        return this.proceedingChain.getArguments();
    }

    public Object proceed() throws Throwable {
        this.index++;
        if (this.index < this.interceptorDefinitions.length) {
            return this.interceptorDefinitions[this.index].invoke(this);
        } else {
            return this.proceedingChain.proceed();
        }
    }

    public Object getThis() {
        return this.proceedingChain.getThis();
    }

    private record InnerChainMethodInvocation(boolean isProxy, Method proxyMethod, Method targetMethod, Object targetObject, Object[] paramObjects) implements MethodInvocation {
            private InnerChainMethodInvocation(boolean isProxy, Method proxyMethod, Method targetMethod, Object targetObject, Object[] paramObjects) {
                this.isProxy = isProxy;
                if (isProxy) {
                    this.proxyMethod = targetMethod;
                    this.targetObject = ((DynamicObject) targetObject).getOriginalObject();
                } else {
                    this.proxyMethod = proxyMethod;
                    this.targetObject = targetObject;
                }
                this.targetMethod = targetMethod;
                this.paramObjects = paramObjects;
            }

            public Method getMethod() {
                return this.targetMethod;
            }

            public Object[] getArguments() {
                return this.paramObjects;
            }

            public Object proceed() throws Throwable {
                try {
                    return proxyMethod.invoke(this.targetObject, this.paramObjects);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }

            public Object getThis() {
                return this.targetObject;
            }
        }
}
