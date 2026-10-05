/*
 * Copyright 2008-2009 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble.concurrent.future;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

public class BasicFutureTimeoutTest {
    @Test
    public void incompleteNonpositiveWaitsExpireAndNullUnitIsRejected() {
        BasicFuture<String> future = new BasicFuture<>();
        for (TimeUnit unit : TimeUnit.values()) {
            assertThrows(TimeoutException.class, () -> future.get(0, unit));
            assertThrows(TimeoutException.class, () -> future.get(-1, unit));
            assertThrows(TimeoutException.class, () -> future.get(Long.MIN_VALUE, unit));
        }
        assertThrows(NullPointerException.class, () -> future.get(1, null));
        assertFalse(future.isDone());
    }

    @Test
    public void completedOutcomesKeepTheirExistingContract() throws Exception {
        BasicFuture<String> successful = new BasicFuture<>("value");
        assertEquals("value", successful.get(Long.MIN_VALUE, TimeUnit.DAYS));
        assertThrows(NullPointerException.class, () -> successful.get(0, null));
        BasicFuture<String> failed = new BasicFuture<>();
        IOException cause = new IOException("expected");
        failed.failed(cause);
        assertSame(cause, assertThrows(ExecutionException.class, () -> failed.get(0, TimeUnit.SECONDS)).getCause());
        BasicFuture<String> cancelled = new BasicFuture<>();
        cancelled.cancel();
        assertTrue(cancelled.isCancelled());
        assertNull(cancelled.get(0, TimeUnit.SECONDS));
    }

    @Test(timeout = 5000)
    public void positiveSubmillisecondWaitsDoNotBecomeImmediateTimeouts() {
        BasicFuture<String> future = new BasicFuture<>();
        for (int i = 0; i < 20; i++) {
            long start = System.nanoTime();
            assertThrows(TimeoutException.class, () -> future.get(500000, TimeUnit.NANOSECONDS));
            assertTrue("positive timeout was truncated", System.nanoTime() - start >= 500000);
        }
        assertFalse(future.isDone());
    }

    @Test(timeout = 5000)
    public void saturatedConversionStillAllowsCompletion() throws Exception {
        BasicFuture<String> future = new BasicFuture<>();
        FutureTask<String> result = new FutureTask<>(() -> future.get(Long.MAX_VALUE, TimeUnit.DAYS));
        Thread waiter = start(result);
        try {
            awaitWaiting(waiter);
            assertTrue(future.completed("value"));
            assertEquals("value", result.get(2, TimeUnit.SECONDS));
        } finally {
            stop(waiter);
        }
    }

    @Test(timeout = 5000)
    public void expirationDoesNotCompleteOrCancelTheFuture() throws Exception {
        BasicFuture<String> future = new BasicFuture<>();
        assertThrows(TimeoutException.class, () -> future.get(20, TimeUnit.MILLISECONDS));
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());
        assertTrue(future.completed("later"));
        assertEquals("later", future.get(0, TimeUnit.NANOSECONDS));
    }

    @Test(timeout = 5000)
    public void failureWakesTimedWaitersWithTheOriginalCause() throws Exception {
        BasicFuture<String> future = new BasicFuture<>();
        IOException cause = new IOException("expected");
        FutureTask<Throwable> result = new FutureTask<>(() -> {
            try {
                future.get(1, TimeUnit.DAYS);
                throw new AssertionError("failure was lost");
            } catch (ExecutionException failure) {
                return failure.getCause();
            }
        });
        Thread waiter = start(result);
        try {
            awaitWaiting(waiter);
            future.failed(cause);
            assertSame(cause, result.get(2, TimeUnit.SECONDS));
        } finally {
            stop(waiter);
        }
    }

    @Test(timeout = 5000)
    public void cancellationWakesTimedWaitersWithoutChangingTheResultContract() throws Exception {
        BasicFuture<String> future = new BasicFuture<>();
        FutureTask<String> result = new FutureTask<>(() -> future.get(1, TimeUnit.DAYS));
        Thread waiter = start(result);
        try {
            awaitWaiting(waiter);
            future.cancel();
            assertNull(result.get(2, TimeUnit.SECONDS));
            assertTrue(future.isCancelled());
        } finally {
            stop(waiter);
        }
    }

    @Test(timeout = 5000)
    public void interruptionEscapesWithoutCompletingTheFuture() throws Exception {
        BasicFuture<String> future = new BasicFuture<>();
        FutureTask<String> result = new FutureTask<>(() -> future.get(1, TimeUnit.DAYS));
        Thread waiter = start(result);
        try {
            awaitWaiting(waiter);
            waiter.interrupt();
            assertTrue(assertThrows(ExecutionException.class, () -> result.get(2, TimeUnit.SECONDS)).getCause() instanceof InterruptedException);
            assertFalse(future.isDone());
        } finally {
            stop(waiter);
        }
    }

    @Test(timeout = 5000)
    public void notificationsDoNotRestartTheTimeoutBudget() throws Exception {
        BasicFuture<String> future = new BasicFuture<>();
        AtomicBoolean running = new AtomicBoolean(true);
        Thread notifier = new Thread(() -> {
            while (running.get()) {
                synchronized (future) {
                    future.notifyAll();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "future-notifier");
        FutureTask<Long> result = new FutureTask<>(() -> {
            long start = System.nanoTime();
            assertThrows(TimeoutException.class, () -> future.get(100, TimeUnit.MILLISECONDS));
            return System.nanoTime() - start;
        });
        Thread waiter = start(result);
        notifier.start();
        try {
            assertTrue(result.get(2, TimeUnit.SECONDS) >= TimeUnit.MILLISECONDS.toNanos(100));
        } finally {
            running.set(false);
            stop(notifier);
            stop(waiter);
        }
    }

    private static Thread start(FutureTask<?> result) {
        Thread thread = new Thread(result, "basic-future-waiter");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private static void awaitWaiting(Thread thread) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (thread.getState() != Thread.State.TIMED_WAITING && thread.isAlive() && System.nanoTime() < deadline) {
            Thread.sleep(1);
        }
        assertEquals(Thread.State.TIMED_WAITING, thread.getState());
    }

    private static void stop(Thread thread) throws InterruptedException {
        thread.interrupt();
        thread.join(2000);
        assertFalse("waiter did not stop", thread.isAlive());
    }
}
