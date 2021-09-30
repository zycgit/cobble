/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.spi;
import net.hasor.cobble.ExceptionUtils;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * SPI 管理器。
 * @version : 2019年06月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultSpiCaller implements SpiTrigger, Closeable {
    private       AtomicBoolean                                    inited      = null;
    private final ConcurrentHashMap<Class<?>, List<EventListener>> spiListener = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, SpiJudge>            spiSpiJudge = new ConcurrentHashMap<>();

    public DefaultSpiCaller() {
        this.inited = new AtomicBoolean(false);
    }

    /** 执行初始化 */
    public final boolean isInit() {
        return this.inited.get();
    }

    /** 执行容器初始化，当 {@link #isInit} 方法返回 false 的时候会触发 {@link #doInitialize()} 调用。 */
    public final void init() {
        if (!this.inited.compareAndSet(false, true)) {
            throw new IllegalStateException("the Container has been inited.");
        }
        this.doInitialize();
    }

    /** 执行容器关闭，当 {@link #isInit} 方法返回 true 的时候会触发 {@link #doClose()} 调用。 */
    @Override
    public final void close() {
        if (!this.inited.compareAndSet(true, false)) {
            throw new IllegalStateException("the Container has been closed.");
        }
        this.doClose();
    }

    @Override
    public <R, T extends EventListener> R notifySpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        return spiCommonCall(spiType, spiCaller, defaultResult, true, false);
    }

    @Override
    public <R, T extends EventListener> R notifyWithoutJudge(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        return spiCommonCall(spiType, spiCaller, defaultResult, true, true);
    }

    @Override
    public <R, T extends EventListener> R chainSpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        return spiCommonCall(spiType, spiCaller, defaultResult, false, false);
    }

    @Override
    public boolean hasSpi(Class<? extends EventListener> spiType) {
        return this.spiListener.containsKey(spiType);
    }

    @Override
    public boolean hasJudge(Class<? extends EventListener> spiJudge) {
        return this.spiSpiJudge.containsKey(spiJudge);
    }

    private <R, T extends EventListener> R spiCommonCall(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult, boolean isNotify, boolean ignoreJudge) {
        List<EventListener> listeners = this.spiListener.get(spiType);
        // .没有 SPI 监听器，那么返回默认值
        if (listeners == null || listeners.isEmpty()) {
            return defaultResult;
        }
        // .只有一个 SPI 监听器，那么选用监听器的值
        if (listeners.size() == 1) {
            try {
                T listener = (T) listeners.get(0);
                return spiCaller.doResultSpi(listener, defaultResult);
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntime(e);
            }
        }
        //
        // .多个 SPI 监听器情况下，通过仲裁决定哪些监听器有效
        SpiJudge spiJudge = SpiJudge.DEFAULT;
        if (!ignoreJudge) {
            if (this.spiSpiJudge.containsKey(spiType)) {
                // 有仲裁，但是仲裁不能为空
                spiJudge = this.spiSpiJudge.get(spiType);
                Objects.requireNonNull(spiJudge, "spi '" + spiType.getName() + "' SpiJudge is null.");
            } else if (isNotify) {
                // 必须要设置仲裁
                throw new UnsupportedOperationException("spi '" + spiType.getName() + "' encounters Multiple, require SpiJudge.");
            }
        }
        List<EventListener> collect = spiJudge.judgeSpi(listeners);
        //
        // .执行监听器
        try {
            if (isNotify) {
                List<R> list = new ArrayList<>();
                for (EventListener listener : collect) {
                    list.add(spiCaller.doResultSpi((T) listener, defaultResult));
                }
                return spiJudge.judgeResult(list, defaultResult);// .仲裁SPI返回结果
            } else {
                R lastResult = defaultResult;
                for (EventListener listener : collect) {
                    lastResult = spiCaller.doResultSpi((T) listener, lastResult);
                }
                return lastResult;
            }
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    /** 获取某个类型 SPI 下面的所有监听器。 */
    public List<EventListener> getEventListenerList(Class<?> spiType) {
        return this.spiListener.get(spiType);
    }

    /** 获取已经注册的 SPI 类型总数。 */
    public int getListenerTypeSize() {
        return this.spiListener.size();
    }

    /** 获取所有SPI总共注册的监听器数。 */
    public long getListenerSize() {
        return this.spiListener.entrySet().stream()//
                .flatMap((Function<Map.Entry<Class<?>, List<EventListener>>, Stream<?>>) classListEntry -> {
                    return classListEntry.getValue().stream();
                }).count();
    }

    /** 注册一个 SPI 监听器 */
    public synchronized <T extends EventListener> void addListener(Class<T> spiType, T spiListener) {
        Objects.requireNonNull(spiType, "spiType is null.");
        Objects.requireNonNull(spiListener, "spiListener is null.");
        //
        List<EventListener> listenerList = this.spiListener.computeIfAbsent(spiType, k -> {
            return new ArrayList<>(5);
        });
        listenerList.add(spiListener);
    }

    /** 注册一个 SPI 仲裁 */
    public synchronized <T extends EventListener> void bindSpiJudge(Class<T> spiType, SpiJudge spiSpiJudgeSupplier) {
        Objects.requireNonNull(spiType, "spiType is null.");
        Objects.requireNonNull(spiSpiJudgeSupplier, "spiSpiJudgeSupplier is null.");
        this.spiSpiJudge.put(spiType, spiSpiJudgeSupplier);
    }

    /** 遍历所有 Listener */
    public void forEachListener(Consumer<Map.Entry<Class<?>, EventListener>> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<Class<?>, List<EventListener>> entryList : this.spiListener.entrySet()) {
            Class<?> listenerKey = entryList.getKey();
            for (EventListener entry : entryList.getValue()) {
                action.accept(new MapEntry(listenerKey, entry));
            }
        }
    }

    /** A single entry in the map. */
    private static final class MapEntry implements Map.Entry<Class<?>, EventListener> {
        private final Class<?>      listenerKey;
        private final EventListener listenerEntry;

        public MapEntry(Class<?> listenerKey, EventListener listenerEntry) {
            this.listenerKey = listenerKey;
            this.listenerEntry = listenerEntry;
        }

        @Override
        public Class<?> getKey() {
            return this.listenerKey;
        }

        @Override
        public EventListener getValue() {
            return this.listenerEntry;
        }

        @Override
        public EventListener setValue(EventListener value) {
            throw new UnsupportedOperationException("this entry no support.");
        }
    }

    /** 初始化过程 */
    protected void doInitialize() {
    }

    /** 销毁过程，清理掉所有已经注册的 SPI 监听器 */
    protected void doClose() {
        this.spiListener.clear();
    }
}
