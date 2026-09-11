/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic.anno.custom;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.hasor.cobble.dynamic.Aop;

/**
 * 自定义的Aop注解只让它在方法上生效。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2015年11月9日
 */
@Aop(MyAopInterceptor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface MyAop {
}
