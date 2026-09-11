/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
/**
 * PropertyDelegate 默认简单实现
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-29
 */
public class SimpleDynamicProperty implements DynamicProperty {
    private Object value;

    public SimpleDynamicProperty() {
        this(null);
    }

    public SimpleDynamicProperty(Object defaultValue) {
        this.value = defaultValue;
    }

    public Object getValue() {
        return get(this);
    }

    public void setValue(Object value) {
        set(this, value);
    }

    public Object get(Object target) {
        return this.value;
    }

    public void set(Object target, Object newValue) {
        if (this.value == newValue) {
            return;
        }
        this.value = newValue;
    }
}
