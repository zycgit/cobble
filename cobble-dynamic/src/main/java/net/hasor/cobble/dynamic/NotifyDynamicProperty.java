/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
