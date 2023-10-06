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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Basic implementation of the {@link Future} interface. <tt>BasicFuture<tt>
 * can be put into a completed state by invoking any of the following methods:
 * {@link #cancel()}, {@link #failed(Throwable)}, or {@link #completed(Object)}.
 *
 * @param <T> the future result type of an asynchronous operation.
 * @since 4.2
 */
public class BasicFuture<T> implements Future<T> {
    private final    FutureCallback<T>               callback;
    private volatile List<FutureListener<Future<T>>> completedListener;
    private volatile List<FutureListener<Future<T>>> failedListener;
    private volatile List<FutureListener<Future<T>>> cancelListener;
    private volatile List<FutureListener<Future<T>>> finalListener;
    private volatile boolean                         completed;
    private volatile boolean                         cancelled;
    private volatile T                               result;
    private volatile Throwable                       ex;

    public BasicFuture() {
        super();
        this.callback = null;
    }

    public BasicFuture(T value) {
        this();
        this.completed = true;
        this.result = value;
    }

    public BasicFuture(final FutureCallback<T> callback) {
        super();
        this.callback = callback;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public boolean isDone() {
        return this.completed;
    }

    @Override
    public Throwable getCause() {
        return this.ex;
    }

    @Override
    public T getResult() {
        return (this.ex != null) ? null : this.result;
    }

    private T result() throws ExecutionException {
        if (this.ex != null) {
            throw new ExecutionException(this.ex.getMessage(), this.ex);
        }
        return this.result;
    }

    @Override
    public synchronized T get() throws InterruptedException, ExecutionException {
        while (!this.completed) {
            wait();
        }
        return result();
    }

    @Override
    public synchronized T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null) {
            throw new NullPointerException("Time unit");
        }

        final long msecs = unit.toMillis(timeout);
        final long startTime = (msecs <= 0) ? 0 : System.currentTimeMillis();
        long waitTime = msecs;
        if (this.completed) {
            return result();
        } else if (waitTime <= 0) {
            throw new TimeoutException();
        } else {
            for (; ; ) {
                wait(waitTime);
                if (this.completed) {
                    return result();
                } else {
                    waitTime = msecs - (System.currentTimeMillis() - startTime);
                    if (waitTime <= 0) {
                        throw new TimeoutException();
                    }
                }
            }
        }
    }

    @Override
    public boolean completed(final T result) {
        synchronized (this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.result = result;
            notifyAll();
        }

        if (this.callback != null) {
            this.callback.completed(result);
        }
        this.triggerListener(this.completedListener);
        this.triggerListener(this.finalListener);
        return true;
    }

    @Override
    public boolean failed(final Throwable exception) {
        synchronized (this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.ex = exception;
            notifyAll();
        }

        if (this.callback != null) {
            this.callback.failed(exception);
        }
        this.triggerListener(this.failedListener);
        this.triggerListener(this.finalListener);
        return true;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.cancelled = true;
            notifyAll();
        }
        if (this.callback != null) {
            if (this.callback instanceof CancelFutureCallback) {
                ((CancelFutureCallback) this.callback).cancelled();
            } else {
                this.failed(new CancellationException());
            }
        }

        this.triggerListener(this.cancelListener);
        this.triggerListener(this.finalListener);
        return true;
    }

    @Override
    public boolean cancel() {
        return cancel(true);
    }

    private void triggerListener(List<FutureListener<Future<T>>> listenerList) {
        if (listenerList != null) {
            for (FutureListener<Future<T>> callback : listenerList) {
                callback.operationComplete(this);
            }
        }
    }

    @Override
    public Future<T> onCompleted(FutureListener<Future<T>> listener) {
        if (listener == null) {
            return this;
        }

        if (this.completed && !this.cancelled && this.ex == null) {
            listener.operationComplete(this);
            return this;
        }

        if (this.completedListener == null) {
            synchronized (this) {
                if (this.completedListener == null) {
                    this.completedListener = new ArrayList<>();
                }
            }
        }

        this.completedListener.add(listener);
        return this;
    }

    @Override
    public Future<T> onFailed(FutureListener<Future<T>> listener) {
        if (listener == null) {
            return this;
        }

        if (this.completed && !this.cancelled && this.ex != null) {
            listener.operationComplete(this);
            return this;
        }

        if (this.failedListener == null) {
            synchronized (this) {
                if (this.failedListener == null) {
                    this.failedListener = new ArrayList<>();
                }
            }
        }

        this.failedListener.add(listener);
        return this;
    }

    @Override
    public Future<T> onCancel(FutureListener<Future<T>> listener) {
        if (listener == null) {
            return this;
        }

        if (this.completed && this.cancelled) {
            listener.operationComplete(this);
            return this;
        }

        if (this.cancelListener == null) {
            synchronized (this) {
                if (this.cancelListener == null) {
                    this.cancelListener = new ArrayList<>();
                }
            }
        }

        this.cancelListener.add(listener);
        return this;
    }

    @Override
    public Future<T> onFinal(FutureListener<Future<T>> listener) {
        if (listener == null) {
            return this;
        }

        if (this.completed) {
            listener.operationComplete(this);
            return this;
        }

        if (this.finalListener == null) {
            synchronized (this) {
                if (this.finalListener == null) {
                    this.finalListener = new ArrayList<>();
                }
            }
        }

        this.finalListener.add(listener);
        return this;
    }
}
