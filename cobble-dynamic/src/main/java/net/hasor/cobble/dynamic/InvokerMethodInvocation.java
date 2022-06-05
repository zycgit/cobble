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
package net.hasor.cobble.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
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

    private static class InnerChainMethodInvocation implements MethodInvocation {
        private final boolean  isProxy;
        private final Method   proxyMethod;
        private final Method   targetMethod;
        private final Object   targetObject;
        private final Object[] paramObjects;

        InnerChainMethodInvocation(boolean isProxy, Method proxyMethod, Method targetMethod, Object targetObject, Object[] paramObjects) {
            this.isProxy = isProxy;
            if (proxyMethod == null) {
                // proxy
                this.proxyMethod = targetMethod;
                this.targetObject = ((DynamicObject) targetObject).getOriginalObject();
            } else {
                // build
                this.proxyMethod = proxyMethod;
                this.targetObject = targetObject;
            }
            this.targetMethod = targetMethod;
            this.paramObjects = paramObjects;
        }

        @Override
        public boolean isProxy() {
            return this.isProxy;
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