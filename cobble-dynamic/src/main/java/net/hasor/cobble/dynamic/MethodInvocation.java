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
import java.lang.reflect.Method;

/**
 * Description of an invocation to a method, given to an interceptor
 * upon method-call.
 * <p>A method invocation is a joinpoint and can be intercepted by a
 * method interceptor.
 * copy from org.aopalliance.intercept.MethodInvocation,and its extends types
 * @author Rod Johnson
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014-7-17
 * @see MethodInterceptor
 */
public interface MethodInvocation {

    boolean isProxy();

    /**
     * Gets the method being called.
     * @return the method being called.
     */
    Method getMethod();

    /**
     * Get the arguments as an array object.
     * It is possible to change element values within this array to change the arguments.
     * @return the argument of the invocation
     */
    Object[] getArguments();

    /**
     * Proceeds to the next interceptor in the chain.
     * <p>The implementation and the semantics of this method depends on the actual joinpoint type (see the children interfaces).
     * @return see the children interfaces' proceed definition.
     * @throws Throwable if the joinpoint throws an exception.
     */
    Object proceed() throws Throwable;

    /**
     * Returns the object that holds the current join point's static part.
     * <p>For instance, the target object for an invocation.
     * @return the object (can be null if the accessible object is
     */
    Object getThis();
}
