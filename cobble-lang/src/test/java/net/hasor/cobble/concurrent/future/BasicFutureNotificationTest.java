/*
 * Copyright 2008-2009 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble.concurrent.future;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BasicFutureNotificationTest {
    @Parameterized.Parameters(name = "{0}/{1}/timed={2}")
    public static Collection<Object[]> cases() {
        Collection<Object[]> cases = new ArrayList<>();
        for (String outcome : new String[] { "completed", "failed", "cancelled" }) {
            for (String observer : new String[] { "callback", "listener", "final", "plain-callback" }) {
                for (boolean timed : new boolean[] { false, true }) {
                    cases.add(new Object[] { outcome, observer, timed });
                }
            }
        }
        return cases;
    }

    private final String  outcome;
    private final String  observer;
    private final boolean timed;

    public BasicFutureNotificationTest(String outcome, String observer, boolean timed) {
        this.outcome = outcome;
        this.observer = observer;
        this.timed = timed;
    }

    @Test(timeout = 10000)
    public void terminalTransitionAlwaysWakesAllWaiters() throws Exception {
        AssertionError observerError = new AssertionError("observer error");
        IOException cause = new IOException("task failure");
        BasicFuture<String> future;
        if ("callback".equals(observer)) {
            future = new BasicFuture<>(new CancelFutureCallback<String>() {
                @Override
                public void completed(String value) {
                    throw observerError;
                }

                @Override
                public void failed(Throwable error) {
                    throw observerError;
                }

                @Override
                public void cancelled() {
                    throw observerError;
                }
            });
        } else if ("plain-callback".equals(observer)) {
            future = new BasicFuture<>(new FutureCallback<String>() {
                @Override
                public void completed(String value) {
                }

                @Override
                public void failed(Throwable error) {
                }
            });
        } else {
            future = new BasicFuture<>();
            FutureListener<Future<String>> listener = result -> {
                throw observerError;
            };
            if ("final".equals(observer)) {
                future.onFinal(listener);
            } else if ("completed".equals(outcome)) {
                future.onCompleted(listener);
            } else if ("failed".equals(outcome)) {
                future.onFailed(listener);
            } else {
                future.onCancel(listener);
            }
        }

        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<FutureTask<Object>> results = new ArrayList<>();
        try {
            for (int i = 0; i < 2; i++) {
                FutureTask<Object> result = new FutureTask<>(() -> {
                    try {
                        return timed ? future.get(1, TimeUnit.DAYS) : future.get();
                    } catch (ExecutionException error) {
                        return error.getCause();
                    }
                });
                Thread thread = new Thread(result, "future-notification-waiter");
                thread.setDaemon(true);
                threads.add(thread);
                results.add(result);
                thread.start();
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
                Thread.State expected = timed ? Thread.State.TIMED_WAITING : Thread.State.WAITING;
                while (thread.getState() != expected && thread.isAlive() && System.nanoTime() < deadline) {
                    Thread.sleep(1);
                }
                assertEquals(expected, thread.getState());
            }

            if ("plain-callback".equals(observer)) {
                transition(future, cause);
            } else {
                assertSame(observerError, assertThrows(AssertionError.class, () -> transition(future, cause)));
            }
            assertTrue(future.isDone());
            assertEquals("cancelled".equals(outcome), future.isCancelled());
            Object expected = "completed".equals(outcome) ? "value" : "failed".equals(outcome) ? cause : null;
            for (FutureTask<Object> result : results) {
                assertSame(expected, result.get(1, TimeUnit.SECONDS));
            }
            assertFalse(future.completed("again"));
            assertFalse(future.failed(new IOException("again")));
            assertFalse(future.cancel());
        } finally {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            for (Thread thread : threads) {
                thread.join(2000);
                assertFalse("waiter did not stop", thread.isAlive());
            }
        }
    }

    private void transition(BasicFuture<String> future, Throwable cause) {
        if ("completed".equals(outcome)) {
            assertTrue(future.completed("value"));
        } else if ("failed".equals(outcome)) {
            assertTrue(future.failed(cause));
        } else {
            assertTrue(future.cancel());
        }
    }
}
