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
package net.hasor.cobble.concurrent.future;
/**
 * Basic implementation of the {@link java.util.concurrent.Future} interface. <tt>BasicFuture<tt>
 * can be put into a completed state by invoking any of the following methods:
 * {@link #cancel()}, {@link #failed(Throwable)}, or {@link #completed(Object)}.
 *
 * @param <T> the future result type of an asynchronous operation.
 * @since 4.2
 */
public interface Future<T> extends java.util.concurrent.Future<T> {

    /** 不阻塞，不抛异常，获取结果如果 completed 则返回 result，如果 failed 或 cancel 则返回空。*/
    T getResult();

    Throwable getCause();

    boolean completed(final T result);

    boolean failed(final Throwable exception);

    @Override
    boolean cancel(final boolean mayInterruptIfRunning);

    boolean cancel();

    Future<T> onCompleted(FutureListener<Future<T>> listener);

    Future<T> onFailed(FutureListener<Future<T>> listener);

    Future<T> onCancel(FutureListener<Future<T>> listener);

    Future<T> onFinal(FutureListener<Future<T>> listener);
}
