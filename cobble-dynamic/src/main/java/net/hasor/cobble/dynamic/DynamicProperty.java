/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
/**
 * 委托属性，aop 组建提供一种新型的属性形态，传统的bean的get/set方法是针对一个私有字段。
 * 而委托属性则是导出这个get/set方法到一个接口中。从而被添加的属性可以通过接口的相应方法来负责处理。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-29
 */
public interface DynamicProperty {
    /** 该委托属性的get方法，参数是属性所处的对象 */
    Object get(Object target) throws Throwable;

    /** 该委托属性的set方法，第一个参数是属性所处的对象，第二个参数代表设置的新值 */
    void set(Object target, Object newValue) throws Throwable;
}
