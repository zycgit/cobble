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
 * delete 语句
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-05-19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface AnnoCfg {

    boolean xml() default false;

    AnnoConfig intConfig();

    AnnoConfig[] intConfigArrays();
}
