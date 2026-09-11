/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.reflect.annotations;
import java.lang.annotation.*;

/**
 * 基于注解的 Mapper
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-06-19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AnnoConfig {
    int intConfig() default 0;

    String strConfig() default "";

}
