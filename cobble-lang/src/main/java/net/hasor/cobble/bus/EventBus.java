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
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.concurrent.future.BasicFuture;
import net.hasor.cobble.concurrent.future.Future;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 标准事件处理器接口的实现类
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-5-6
 */
public class EventBus implements BusContext {
    private final ConcurrentMap<String, ListenerPool> listenerMap = new ConcurrentHashMap<>();

    private ListenerPool getOrCreateListenerPool(String event) {
        ListenerPool pool = listenerMap.get(event);
        if (pool == null) {
            ListenerPool newPool = new ListenerPool();
            pool = listenerMap.putIfAbsent(event, newPool);
            if (pool == null) {
                pool = newPool;
            }
        }
        return pool;
    }

    private ListenerPool getListenerPool(String event) {
        return listenerMap.get(event);
    }

    @Override
    public boolean pushListener(final String event, final BusListener eventListener) {
        if (StringUtils.isBlank(event) || eventListener == null) {
            return false;
        }
        return this.getOrCreateListenerPool(event).pushOnceListener(eventListener);
    }

    @Override
    public boolean addListener(final String event, final BusListener eventListener) {
        if (StringUtils.isBlank(event) || eventListener == null) {
            return false;
        }
        return this.getOrCreateListenerPool(event).addListener(eventListener);
    }

    @Override
    public boolean removeListener(final String event, final BusListener eventListener) {
        if (StringUtils.isBlank(event) || eventListener == null) {
            return false;
        }
        ListenerPool listenerPool = this.getListenerPool(event);
        if (listenerPool != null) {
            return listenerPool.removeListener(eventListener);
        } else {
            return false;
        }
    }

    @Override
    public void clearAllListener() {
        this.listenerMap.clear();
    }

    @Override
    public void clearListener(String event) {
        if (StringUtils.isBlank(event)) {
            return;
        }
        ListenerPool listenerPool = this.getListenerPool(event);
        if (listenerPool != null) {
            this.getListenerPool(event).clearListener();
        }
    }

    @Override
    public void fireEvent(String event, Object data) throws Throwable {
        fireEvent(event, data, (listener, lastResult) -> {
            listener.onEvent(event, data);
            return null;
        });
    }

    @Override
    public Object fireEvent(String event, Object data, BusCaller caller) throws Throwable {
        ListenerPool listenerPool = this.getListenerPool(event);
        if (listenerPool == null) {
            return null;
        }

        Object lastResult = null;

        List<BusListener> snapshot = listenerPool.getListenerSnapshot();
        for (BusListener item : snapshot) {
            lastResult = caller.doEvent(item, lastResult);
        }

        List<BusListener> onceList = listenerPool.popOnceListener();
        if (onceList != null) {
            for (BusListener item : onceList) {
                lastResult = caller.doEvent(item, lastResult);
            }
        }
        return lastResult;
    }

    public void fireEventWithoutThrow(String event, Object data) {
        fireEventWithoutThrow(event, data, (listener, lastResult) -> {
            listener.onEvent(event, data);
            return null;
        });
    }

    @Override
    public Object fireEventWithoutThrow(String event, Object data, BusCaller caller) {
        ListenerPool listenerPool = this.getListenerPool(event);
        if (listenerPool == null) {
            return null;
        }

        Object lastResult = null;

        List<BusListener> snapshot = listenerPool.getListenerSnapshot();
        for (BusListener item : snapshot) {
            try {
                lastResult = caller.doEvent(item, lastResult);
            } catch (Throwable ignore) {

            }
        }

        List<BusListener> onceList = listenerPool.popOnceListener();
        if (onceList != null) {
            for (BusListener item : onceList) {
                try {
                    lastResult = caller.doEvent(item, lastResult);
                } catch (Throwable ignore) {

                }
            }
        }
        return lastResult;
    }

    @Override
    public Future<Void> asyncFireEvent(Executor executor, String event, Object args) {
        BasicFuture<Void> future = new BasicFuture<>();
        executor.execute(() -> {
            try {
                if (!future.isCancelled()) {
                    fireEvent(event, args);
                    future.completed(null);
                }
            } catch (Throwable e) {
                future.failed(e);
            }
        });
        return future;
    }

    @Override
    public Future<Object> asyncFireEvent(Executor executor, String event, Object args, BusCaller caller) {
        BasicFuture<Object> future = new BasicFuture<>();
        executor.execute(() -> {
            try {
                if (!future.isCancelled()) {
                    Object result = fireEvent(event, args, caller);
                    future.completed(result);
                }
            } catch (Throwable e) {
                future.failed(e);
            }
        });
        return future;
    }

    @Override
    public Future<Void> lazyFireEvent(Executor executor, String event, Object args, long sleep, TimeUnit timeUnit) {
        BasicFuture<Void> future = new BasicFuture<>();
        executor.execute(() -> {
            try {
                timeUnit.sleep(sleep);

                if (!future.isCancelled()) {
                    fireEvent(event, args);
                    future.completed(null);
                }
            } catch (Throwable e) {
                future.failed(e);
            }
        });
        return future;
    }

    @Override
    public Future<Object> lazyFireEvent(Executor executor, String event, Object args, BusCaller caller, long sleep, TimeUnit timeUnit) {
        BasicFuture<Object> future = new BasicFuture<>();
        executor.execute(() -> {
            try {
                timeUnit.sleep(sleep);

                if (!future.isCancelled()) {
                    Object result = fireEvent(event, args, caller);
                    future.completed(result);
                }
            } catch (Throwable e) {
                future.failed(e);
            }
        });
        return future;
    }
}
