/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.i18n;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记国际化资源文件位置
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2023-08-17
 */
@Repeatable(I18nResourceSet.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nResource {
    String[] value() default {};
}
