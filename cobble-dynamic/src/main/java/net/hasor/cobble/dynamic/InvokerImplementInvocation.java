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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
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