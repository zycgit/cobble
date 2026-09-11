/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记在类或方法上，为类或方法指定拦截器。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-3-20
 */
@Repeatable(AopSet.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aop {
    Class<? extends MethodInterceptor>[] value();
}
