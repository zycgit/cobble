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
package net.hasor.cobble.bus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用于封装事件对象。
 * @version : 2014-3-13
 * @author 赵永春 (zyc@hasor.net)
 */
class ListenerPool {
    private final Object                            ONCE_LOCK = new Object();
    private       CopyOnWriteArrayList<BusListener> onceListener;
    private final CopyOnWriteArrayList<BusListener> listenerList;

    public ListenerPool() {
        onceListener = new CopyOnWriteArrayList<>();
        listenerList = new CopyOnWriteArrayList<>();
    }

    public boolean pushOnceListener(BusListener eventListener) {
        synchronized (ONCE_LOCK) {
            return onceListener.add(eventListener);
        }
    }

    public boolean addListener(BusListener eventListener) {
        return listenerList.add(eventListener);
    }

    public List<BusListener> popOnceListener() {
        List<BusListener> onceList = null;
        synchronized (ONCE_LOCK) {
            onceList = this.onceListener;
            this.onceListener = new CopyOnWriteArrayList<>();
        }
        return onceList;
    }

    public List<BusListener> getListenerSnapshot() {
        return new ArrayList<>(this.listenerList);
    }

    public boolean removeListener(BusListener eventListener) {
        return listenerList.remove(eventListener);
    }

    public boolean clearListener() {
        onceListener.clear();
        listenerList.clear();
        return true;
    }
}