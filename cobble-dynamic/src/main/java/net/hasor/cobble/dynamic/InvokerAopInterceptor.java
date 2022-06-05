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
import net.hasor.cobble.supplier.TypeSupplier;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aop拦截器
 * @version : 2013-11-8
 * @author 赵永春 (zyc@hasor.net)
 */
class InvokerAopInterceptor implements MethodInterceptor {
    private final WeakHashMap<Method, List<Class<? extends MethodInterceptor>>> methodInterceptorMap = new WeakHashMap<>();
    private       TypeSupplier                                                  typeSupplier         = null;

    public InvokerAopInterceptor(TypeSupplier typeSupplier) {
        this.typeSupplier = typeSupplier;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        List<Class<? extends MethodInterceptor>> list = this.methodInterceptorMap.get(targetMethod);
        //1.取得拦截器链
        if (list == null) {
            list = new ArrayList<>();
            //b.类级拦截器
            list.addAll(collectAnno(targetMethod.getDeclaringClass()));
            //c.方法级拦截器
            list.addAll(collectAnno(targetMethod));
            //d.缓存结果
            this.methodInterceptorMap.put(targetMethod, list);
        }
        //2.创建对象
        return new AopChainInvocation(this.typeSupplier, list, invocation).invoke(invocation);
    }

    private List<Class<? extends MethodInterceptor>> collectAnno(AnnotatedElement element) {
        Aop[] annoSet = element.getAnnotationsByType(Aop.class);
        if (annoSet == null) {
            annoSet = new Aop[0];
        }
        return Arrays.stream(annoSet).flatMap(//
                (Function<Aop, Stream<Class<? extends MethodInterceptor>>>) aop -> Arrays.stream(aop.value())//
        ).collect(Collectors.toList());
    }

    private static class AopChainInvocation implements MethodInvocation {
        private final MethodInterceptor[] beforeInterceptor;
        private final MethodInvocation    invocation;
        private       int                 index = -1;

        public AopChainInvocation(TypeSupplier appContext, List<Class<? extends MethodInterceptor>> interTypeList, MethodInvocation invocation) {
            List<MethodInterceptor> beforeList = new ArrayList<>();
            for (Class<? extends MethodInterceptor> interType : interTypeList) {
                if (interType != null) {
                    beforeList.add(appContext.get(interType));
                }
            }
            this.beforeInterceptor = beforeList.toArray(new MethodInterceptor[0]);
            this.invocation = invocation;
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {
            index++;
            if (index < beforeInterceptor.length) {
                return beforeInterceptor[index].invoke(this);
            } else {
                return invocation.proceed();
            }
        }

        public Object[] getArguments() {
            return invocation.getArguments();
        }

        public Object proceed() throws Throwable {
            return this.invoke(this.invocation);
        }

        public Object getThis() {
            return invocation.getThis();
        }

        @Override
        public boolean isProxy() {
            return invocation.isProxy();
        }

        public Method getMethod() {
            return invocation.getMethod();
        }
    }
}