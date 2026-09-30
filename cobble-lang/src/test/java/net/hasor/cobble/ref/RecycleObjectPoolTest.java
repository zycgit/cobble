/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecycleObjectPoolTest {
    @Test
    public void callbacksRunForFreshAndCachedObjects() {
        StringBuilder events = new StringBuilder();
        RecycleObjectPool<StringBuilder> pool = new RecycleObjectPool<>(() -> {
            events.append("create>");
            return new StringBuilder();
        }, value -> {
            events.append("init>");
            value.append("ready");
        }, value -> {
            events.append("recycle>");
            value.setLength(0);
        });
        StringBuilder value = pool.get();
        assertEquals("ready", value.toString());
        value.append(" payload");
        pool.recycle(value);
        assertEquals("create>init>recycle>", events.toString());
        assertSame(value, pool.get());
        assertEquals("ready", value.toString());
        assertEquals("create>init>recycle>init>", events.toString());
    }

    @Test
    public void separatePoolsOfTheSameTypeHaveNoRegistrationLimit() {
        List<RecycleObjectPool<Object>> pools = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
            }, value -> {
            });
            Object value = pool.get();
            pools.add(pool);
            objects.add(value);
            pool.recycle(value);
        }
        for (int i = 0; i < pools.size(); i++) {
            assertSame(objects.get(i), pools.get(i).get());
        }
    }

    @Test
    public void simultaneousBorrowsNeverReuseAnInUseObject() {
        AtomicInteger created = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(() -> {
            created.incrementAndGet();
            return new Object();
        }, value -> {
        }, value -> {
        });
        Object first = pool.get();
        Object second = pool.get();
        assertNotSame(first, second);
        assertEquals(2, created.get());
        pool.recycle(first);
        assertSame(first, pool.get());
        assertEquals(2, created.get());
    }

    @Test
    public void defaultCountLimitsPreserveLocalLifoAndSharedFifo() {
        AtomicInteger created = new AtomicInteger();
        AtomicInteger cleaned = new AtomicInteger();
        RecycleObjectPool<int[]> pool = new RecycleObjectPool<>(() -> {
            created.incrementAndGet();
            return new int[1];
        }, value -> {
        }, value -> cleaned.incrementAndGet());
        List<int[]> values = new ArrayList<>();
        for (int i = 0; i < 1024 + 4096 + 7; i++) {
            int[] value = new int[] { i };
            values.add(value);
            pool.recycle(value);
        }
        assertEquals(values.size(), cleaned.get());
        for (int i = 1023; i >= 0; i--)
            assertSame(values.get(i), pool.get());
        for (int i = 1024; i < 1024 + 4096; i++)
            assertSame(values.get(i), pool.get());
        assertEquals(0, created.get());
        assertFalse(values.contains(pool.get()));
        assertEquals(1, created.get());
    }

    @Test
    public void configuredCountLimitsDiscardOverflowAfterCleanup() {
        AtomicInteger cleaned = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
        }, value -> cleaned.incrementAndGet(), 2, 1);
        Object first = new Object(), second = new Object(), shared = new Object(), discarded = new Object();
        pool.recycle(first);
        pool.recycle(second);
        pool.recycle(shared);
        pool.recycle(discarded);
        assertEquals(4, cleaned.get());
        assertSame(second, pool.get());
        assertSame(first, pool.get());
        assertSame(shared, pool.get());
        assertNotSame(discarded, pool.get());
    }

    @Test
    public void disabledCachesStillRunLifecycleCallbacks() {
        for (int[] limits : new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 } }) {
            AtomicInteger initialized = new AtomicInteger(), cleaned = new AtomicInteger();
            RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> initialized.incrementAndGet(), value -> cleaned.incrementAndGet(), limits[0], limits[1]);
            Object first = pool.get();
            pool.recycle(first);
            Object next = pool.get();
            if (limits[0] + limits[1] == 0)
                assertNotSame(first, next);
            else
                assertSame(first, next);
            assertEquals(2, initialized.get());
            assertEquals(1, cleaned.get());
        }
    }

    @Test
    public void invalidConfigurationAndNullReturnsAreRejected() {
        assertThrows(NullPointerException.class, () -> new RecycleObjectPool<>(null, value -> {
        }, value -> {
        }));
        assertThrows(NullPointerException.class, () -> new RecycleObjectPool<>(Object::new, null, value -> {
        }));
        assertThrows(NullPointerException.class, () -> new RecycleObjectPool<>(Object::new, value -> {
        }, null));
        assertThrows(IllegalArgumentException.class, () -> new RecycleObjectPool<>(Object::new, value -> {
        }, value -> {
        }, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new RecycleObjectPool<>(Object::new, value -> {
        }, value -> {
        }, 0, -1));
        AtomicInteger cleaned = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
        }, value -> cleaned.incrementAndGet());
        assertThrows(NullPointerException.class, () -> pool.recycle(null));
        assertEquals(0, cleaned.get());
        Object valid = pool.get();
        pool.recycle(valid);
        assertSame(valid, pool.get());
    }

    @Test
    public void nullFactoryResultNeverReachesInitializer() {
        AtomicInteger initialized = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(() -> null, value -> initialized.incrementAndGet(), value -> {
        });
        assertThrows(NullPointerException.class, pool::get);
        assertThrows(NullPointerException.class, pool::get);
        assertEquals(0, initialized.get());
    }

    @Test
    public void factoryFailureLeavesPoolUsable() {
        AtomicInteger created = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(() -> {
            if (created.incrementAndGet() == 1)
                throw new IllegalStateException("create");
            return new Object();
        }, value -> {
        }, value -> {
        });
        assertThrows(IllegalStateException.class, pool::get);
        Object valid = pool.get();
        pool.recycle(valid);
        assertSame(valid, pool.get());
        assertEquals(2, created.get());
    }

    @Test
    public void initializerFailureDiscardsObjectAndReleasesItsBudget() {
        for (int[] limits : new int[][] { { 1, 0 }, { 0, 1 } }) {
            AtomicInteger initialized = new AtomicInteger();
            RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
                if (initialized.incrementAndGet() == 2)
                    throw new IllegalStateException("initialize");
            }, value -> {
            }, limits[0], limits[1]);
            Object first = pool.get();
            pool.recycle(first);
            assertThrows(IllegalStateException.class, pool::get);
            Object next = pool.get();
            assertNotSame(first, next);
            pool.recycle(next);
            assertSame(next, pool.get());
        }
    }

    @Test
    public void recycleFailureDoesNotPublishOrChargeTheObject() {
        AtomicInteger cleaned = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
        }, value -> {
            if (cleaned.incrementAndGet() == 1)
                throw new IllegalStateException("clean");
        }, 0, 1);
        Object first = pool.get();
        assertThrows(IllegalStateException.class, () -> pool.recycle(first));
        Object next = pool.get();
        assertNotSame(first, next);
        pool.recycle(next);
        assertSame(next, pool.get());
    }

    @Test(timeout = 10000)
    public void sharedOverflowTransfersAcrossThreadsWithoutStealingLocalObjects() throws Exception {
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
        }, value -> {
        }, 2, 2);
        Object first = new Object(), second = new Object(), third = new Object(), fourth = new Object();
        pool.recycle(first);
        pool.recycle(second);
        pool.recycle(third);
        pool.recycle(fourth);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread consumer = new Thread(() -> {
            try {
                assertSame(third, pool.get());
                assertSame(fourth, pool.get());
                Object fresh = pool.get();
                assertNotSame(first, fresh);
                assertNotSame(second, fresh);
            } catch (Throwable e) {
                failure.set(e);
            }
        });
        consumer.start();
        consumer.join();
        if (failure.get() != null)
            throw new AssertionError(failure.get());
        assertSame(second, pool.get());
        assertSame(first, pool.get());
    }

    @Test(timeout = 10000)
    public void globalCountLimitIsStrictUnderConcurrentReturns() throws Exception {
        for (int round = 0; round < 8; round++) {
            RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
            }, value -> {
            }, 0, 32);
            Set<Object> returned = Collections.newSetFromMap(new IdentityHashMap<>());
            List<Thread> workers = new ArrayList<>();
            CountDownLatch start = new CountDownLatch(1);
            AtomicReference<Throwable> failure = new AtomicReference<>();
            for (int t = 0; t < 8; t++) {
                List<Object> batch = new ArrayList<>();
                for (int i = 0; i < 32; i++) {
                    Object value = new Object();
                    batch.add(value);
                    returned.add(value);
                }
                Thread worker = new Thread(() -> {
                    try {
                        start.await();
                        for (Object value : batch)
                            pool.recycle(value);
                    } catch (Throwable e) {
                        failure.compareAndSet(null, e);
                    }
                });
                workers.add(worker);
                worker.start();
            }
            start.countDown();
            for (Thread worker : workers)
                worker.join();
            if (failure.get() != null)
                throw new AssertionError(failure.get());
            Set<Object> acquired = Collections.newSetFromMap(new IdentityHashMap<>());
            for (int i = 0; i < 32; i++) {
                Object value = pool.get();
                assertTrue(returned.contains(value));
                assertTrue(acquired.add(value));
            }
            assertFalse(returned.contains(pool.get()));
            for (Object value : acquired)
                pool.recycle(value);
            for (int i = 0; i < 32; i++)
                assertTrue(acquired.contains(pool.get()));
        }
    }
}
