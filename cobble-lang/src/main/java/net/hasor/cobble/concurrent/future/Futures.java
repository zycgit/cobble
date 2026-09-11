/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.concurrent.future;
import java.util.concurrent.TimeUnit;

public class Futures {
    private static final NoopFuture NOOP_FUTURE = new NoopFuture();

    public static <T> Future<T> buildFailed(Throwable e) {
        Future<T> f = new BasicFuture<>();
        f.failed(e);
        return f;
    }

    public static <T> Future<T> buildCompleted(T result) {
        Future<T> f = new BasicFuture<>();
        f.completed(result);
        return f;
    }

    public static <T> Future<T> buildCancel() {
        Future<T> f = new BasicFuture<>();
        f.cancel();
        return f;
    }

    public static <T> Future<T> buildNoop() {
        return (Future<T>) NOOP_FUTURE;
    }

    /** A pre-completed no-op Future that avoids BasicFuture allocation overhead for internal sends where the caller never reads the result. */
    private static final class NoopFuture implements Future<Object> {
        private NoopFuture() {
        }

        @Override
        public boolean completed(Object result) {
            return false;
        }

        @Override
        public boolean failed(Throwable exception) {
            return false;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean cancel() {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public Throwable getCause() {
            return null;
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object get(long timeout, TimeUnit unit) {
            return null;
        }

        @Override
        public Future<Object> onCompleted(FutureListener<Future<Object>> listener) {
            return this;
        }

        @Override
        public Future<Object> onFailed(FutureListener<Future<Object>> listener) {
            return this;
        }

        @Override
        public Future<Object> onCancel(FutureListener<Future<Object>> listener) {
            return this;
        }

        @Override
        public Future<Object> onFinal(FutureListener<Future<Object>> listener) {
            return this;
        }
    }
}
