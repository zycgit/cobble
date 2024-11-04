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
import net.hasor.cobble.concurrent.future.Future;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2014年5月22日
 */
public interface BusContext {
    BusContext DEFAULT = new EventBus();

    /**
     * pushPhaseEvent方法注册的事件监听器当收到一次事件之后会被自动删除。
     * @param event 事件
     * @param eventListener 事件监听器。
     */
    boolean pushListener(String event, BusListener eventListener);

    /**
     * 添加一种类型事件的事件监听器。
     * @param event 事件
     * @param eventListener 事件监听器。
     */
    boolean addListener(String event, BusListener eventListener);

    /**
     * 删除某个监听器的注册。
     * @param event 事件
     * @param eventListener 事件监听器。
     */
    boolean removeListener(String event, BusListener eventListener);

    /**
     * 删除所有监听器的注册。
     */
    void clearAllListener();

    /**
     * 删除某个监听器的注册。
     * @param event 事件
     */
    void clearListener(String event);

    /**
     * 触发事件。
     * @param event 事件
     * @param args 事件参数
     */
    void fireEvent(String event, Object args) throws Throwable;

    /**
     * 触发事件。
     * @param event 事件
     * @param args 事件参数
     * @param caller 决定怎样处理对 Event 监听器的调用，并且可以处理 event 的返回值。
     */
    Object fireEvent(String event, Object args, BusCaller caller) throws Throwable;

    /**
     * 触发事件，不会被任何异常打断。
     * @param event 事件
     * @param args 事件参数
     */
    void fireEventWithoutThrow(String event, Object args);

    /**
     * 触发事件，不会被任何异常打断。
     * @param event 事件
     * @param args 事件参数
     * @param caller 决定怎样处理对 Event 监听器的调用，并且可以处理 event 的返回值。
     */
    Object fireEventWithoutThrow(String event, Object args, BusCaller caller);

    /**
     * 异步触发事件。
     * @param executor 异步执行的线程
     * @param event 事件
     * @param args 事件参数
     */
    Future<Void> asyncFireEvent(Executor executor, String event, Object args);

    /**
     * 异步触发事件。
     * @param executor 异步执行的线程
     * @param event 事件
     * @param args 事件参数
     */
    Future<Object> asyncFireEvent(Executor executor, String event, Object args, BusCaller caller);

    /**
     * 延迟触发事件。
     * @param executor 异步执行的线程
     * @param event 事件
     * @param args 事件参数
     * @param sleep 延迟时间
     * @param timeUnit 延迟时间单位
     */
    Future<Void> lazyFireEvent(Executor executor, String event, Object args, long sleep, TimeUnit timeUnit);

    /**
     * 延迟触发事件。
     * @param executor 异步执行的线程
     * @param event 事件
     * @param args 事件参数
     * @param sleep 延迟时间
     * @param timeUnit 延迟时间单位
     */
    Future<Object> lazyFireEvent(Executor executor, String event, Object args, BusCaller caller, long sleep, TimeUnit timeUnit);
}
