/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * PropertyDelegate 默认简单实现
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-29
 */
public class NotifyDynamicProperty extends SimpleDynamicProperty {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public NotifyDynamicProperty() {
        super(null);
    }

    public NotifyDynamicProperty(Object defaultValue) {
        super(defaultValue);
    }

    public void addListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void set(Object target, Object newValue) {
        if (this.getValue() == newValue) {
            return;
        }

        Object oldValue = this.getValue();
        this.setValue(newValue);
        this.pcs.firePropertyChange("value", oldValue, newValue);
    }
}
