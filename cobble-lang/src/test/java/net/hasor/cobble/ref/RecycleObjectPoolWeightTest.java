/*
 * Copyright 2015-2022 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble.ref;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecycleObjectPoolWeightTest {
    private RecycleObjectPool<int[]> pool(int local, int global, AtomicInteger cleaned) {
        return new RecycleObjectPool<>(() -> new int[1], value -> value[0] = 1, value -> {
            value[0] = 0;
            cleaned.incrementAndGet();
        }, value -> value.length, local, global);
    }

    @Test
    public void budgetsUseCapacityAndReuseRemovesPreviousCharge() {
        AtomicInteger cleaned = new AtomicInteger();
        RecycleObjectPool<int[]> pool = pool(6, 4, cleaned);
        int[] a = new int[4], b = new int[2], c = new int[4], rejected = new int[3];
        pool.recycle(a);
        pool.recycle(b);
        pool.recycle(c);
        pool.recycle(rejected);
        assertEquals(4, cleaned.get());
        assertSame(b, pool.get());
        assertSame(a, pool.get());
        assertSame(c, pool.get());
        assertNotSame(rejected, pool.get());
        pool.recycle(a);
        assertSame(a, pool.get());
        assertEquals(1, a[0]);
    }

    @Test
    public void oversizedAndDisabledCachesStillCleanObjects() {
        AtomicInteger cleaned = new AtomicInteger();
        for (int limit : new int[] { 0, 4 }) {
            RecycleObjectPool<int[]> pool = pool(limit, limit, cleaned);
            int[] large = new int[5];
            large[0] = 9;
            pool.recycle(large);
            assertEquals(0, large[0]);
            assertNotSame(large, pool.get());
        }
        assertEquals(2, cleaned.get());
    }

    @Test
    public void rejectsInvalidConfigurationAndWeights() {
        assertThrows(NullPointerException.class, () -> new RecycleObjectPool<>(null, value -> {
        }, value -> {
        }, value -> 1, 1, 1));
        assertThrows(NullPointerException.class, () -> new RecycleObjectPool<>(Object::new, value -> {
        }, value -> {
        }, null, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> pool(-1, 1, new AtomicInteger()));
        assertThrows(IllegalArgumentException.class, () -> pool(1, -1, new AtomicInteger()));
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
        }, value -> {
        }, value -> 0, 1, 1);
        assertThrows(NullPointerException.class, () -> pool.recycle(null));
        assertThrows(IllegalArgumentException.class, () -> pool.recycle(new Object()));
    }

    @Test
    public void cleanupWeightIsChargedBeforeInitializationCanChangeIt() {
        for (int[] limits : new int[][] { { 4, 0 }, { 0, 4 } }) {
            RecycleObjectPool<int[]> pool = new RecycleObjectPool<>(() -> new int[] { 2 }, value -> value[0] = 9, value -> value[0] = 2, value -> value[0], limits[0], limits[1]);
            int[] first = new int[] { 20 }, second = new int[] { 30 };
            pool.recycle(first);
            pool.recycle(second);
            int[] borrowed = pool.get();
            assertEquals(9, borrowed[0]);
            assertTrue(borrowed == first || borrowed == second);
            pool.recycle(borrowed);
            Set<int[]> recovered = Collections.newSetFromMap(new IdentityHashMap<>());
            recovered.add(pool.get());
            recovered.add(pool.get());
            assertEquals(2, recovered.size());
            assertTrue(recovered.contains(first));
            assertTrue(recovered.contains(second));
        }
    }

    @Test
    public void acquiredCapacityGrowthUsesItsNewWeightOnReturn() {
        RecycleObjectPool<int[]> pool = new RecycleObjectPool<>(() -> new int[] { 1 }, value -> {
        }, value -> {
        }, value -> value[0], 5, 0);
        int[] large = new int[] { 1 }, small = new int[] { 2 };
        pool.recycle(large);
        assertSame(large, pool.get());
        large[0] = 4;
        pool.recycle(large);
        pool.recycle(small);
        assertSame(large, pool.get());
        assertNotSame(small, pool.get());
    }

    @Test
    public void maximumIntegerWeightsCannotOverflowBudgets() {
        for (int[] limits : new int[][] { { Integer.MAX_VALUE, 0 }, { 0, Integer.MAX_VALUE } }) {
            RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
            }, value -> {
            }, value -> Integer.MAX_VALUE, limits[0], limits[1]);
            Object first = new Object(), discarded = new Object();
            pool.recycle(first);
            pool.recycle(discarded);
            assertSame(first, pool.get());
            assertNotSame(discarded, pool.get());
            pool.recycle(first);
            assertSame(first, pool.get());
        }
    }

    @Test
    public void callbackFailuresDoNotCacheOrStrandBudget() {
        AtomicInteger init = new AtomicInteger();
        AtomicInteger reset = new AtomicInteger();
        RecycleObjectPool<Object> pool = new RecycleObjectPool<>(Object::new, value -> {
            if (init.incrementAndGet() == 2)
                throw new IllegalStateException("initialize");
        }, value -> {
            if (reset.incrementAndGet() == 2)
                throw new IllegalStateException("reset");
        }, value -> 1, 1, 0);
        Object a = pool.get();
        pool.recycle(a);
        assertThrows(IllegalStateException.class, pool::get);
        Object b = pool.get();
        assertNotSame(a, b);
        assertThrows(IllegalStateException.class, () -> pool.recycle(b));
        Object c = pool.get();
        assertNotSame(b, c);
        pool.recycle(c);
        assertSame(c, pool.get());
    }

    @Test(timeout = 10000)
    public void globalOverflowIsBoundedUnderConcurrentReturns() throws Exception {
        RecycleObjectPool<int[]> pool = pool(0, 128, new AtomicInteger());
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        List<Thread> workers = new ArrayList<>();
        Set<int[]> returned = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int t = 0; t < 8; t++) {
            List<int[]> batch = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                int[] value = new int[4];
                batch.add(value);
                returned.add(value);
            }
            Thread worker = new Thread(() -> {
                try {
                    start.await();
                    for (int[] value : batch)
                        pool.recycle(value);
                } catch (Throwable e) {
                    failure.set(e);
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
        Set<int[]> acquired = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int i = 0; i < 32; i++) {
            int[] value = pool.get();
            assertTrue(returned.contains(value));
            assertTrue(acquired.add(value));
        }
        assertFalse(returned.contains(pool.get()));
        for (int[] value : acquired)
            pool.recycle(value);
        for (int i = 0; i < 32; i++)
            assertTrue(acquired.contains(pool.get()));
    }

    @Test(timeout = 10000)
    public void concurrentBorrowAndReturnDoesNotDuplicateOwnership() throws Exception {
        RecycleObjectPool<AtomicInteger> pool = new RecycleObjectPool<>(AtomicInteger::new, value -> {
            if (!value.compareAndSet(0, 1))
                throw new AssertionError("duplicate owner");
        }, value -> {
            if (!value.compareAndSet(1, 0))
                throw new AssertionError("invalid return");
        }, value -> 4, 0, 32);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        List<Thread> workers = new ArrayList<>();
        for (int t = 0; t < 8; t++) {
            Thread worker = new Thread(() -> {
                try {
                    for (int i = 0; i < 10000; i++)
                        pool.recycle(pool.get());
                } catch (Throwable e) {
                    failure.compareAndSet(null, e);
                }
            });
            workers.add(worker);
            worker.start();
        }
        for (Thread worker : workers)
            worker.join();
        if (failure.get() != null)
            throw new AssertionError(failure.get());
    }
}
