/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
/**
 * Intercepts calls on an interface on its way to the target. These are nested "on top" of the target.
 * <p>
 * The user should implement the {@link #invoke(MethodInvocation)} method to
 * modify the original behavior. E.g. the following class implements a tracing
 * interceptor (traces all the calls on the intercepted method(s)):
 * <pre class=code>
 * class TracingInterceptor implements MethodInterceptor {
 * 	Object invoke(MethodInvocation i) throws Throwable {
 * 		System.out.println(&quot;method &quot; + i.getMethod() + &quot; is called on &quot;
 * 				+ i.getThis() + &quot; with args &quot; + i.getArguments());
 * 		Object ret = i.proceed();
 * 		System.out.println(&quot;method &quot; + i.getMethod() + &quot; returns &quot; + ret);
 * 		return ret;
 *    }
 * }
 * </pre>
 * copy from org.aopalliance.intercept.MethodInterceptor
 * @author Rod Johnson
 */
@FunctionalInterface
public interface MethodInterceptor {
    /**
     * implement this method to perform extra treatments before and after the invocation.
     * @param invocation 拦截器链
     * @return 返回拦截器执行结果
     * @throws Throwable 抛出的异常
     */
    Object invoke(MethodInvocation invocation) throws Throwable;
}
