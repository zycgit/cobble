/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * An independent, type-safe object pool with bounded idle caches.
 * <p>
 * Acquisition uses the current thread's LIFO cache, then the pool's shared FIFO
 * cache, then the factory. Objects are returned to the recycling thread's cache;
 * local overflow is offered to the shared cache. No type registration is needed,
 * and separate pools never share cached objects, even for the same Java type.
 * <p>
 * Limits account for idle objects only: the local limit applies to each thread
 * using this pool, and the shared limit applies to this pool as a whole. They do
 * not bound in-use objects, total heap usage, or all threads' combined caches.
 * <p>
 * Each acquired object has one owner. Callers must recycle it at most once and
 * must not access it after recycling. Duplicate returns and returns to the wrong
 * pool are caller errors; the pool does not attach ownership wrappers to objects.
 * @param <T> pooled object type
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2022-11-01
 */
public final class RecycleObjectPool<T> {
    private static final int                        DEFAULT_LOCAL_LIMIT  = 1024;
    private static final int                        DEFAULT_GLOBAL_LIMIT = 4096;
    private static final ToIntFunction<Object>      UNIT_WEIGHT          = object -> 1;
    private final        Supplier<? extends T>      factory;
    private final        Consumer<? super T>        initializer;
    private final        Consumer<? super T>        recycler;
    private final        ToIntFunction<? super T>   weigher;
    private final        int                        localLimit;
    private final        int                        globalLimit;
    private final        ThreadLocal<LocalCache<T>> local                = ThreadLocal.withInitial(LocalCache::new);
    private final        Queue<T>                   global               = new ConcurrentLinkedQueue<>();
    private final        AtomicLong                 globalWeight         = new AtomicLong();

    /** Creates a count-bounded pool with 1024 local and 4096 shared idle slots. */
    public RecycleObjectPool(Supplier<? extends T> factory, Consumer<? super T> initializer, Consumer<? super T> recycler) {
        this(factory, initializer, recycler, DEFAULT_LOCAL_LIMIT, DEFAULT_GLOBAL_LIMIT);
    }

    /**
     * Creates a count-bounded pool. A zero limit disables that cache.
     * @param factory creates a non-null object when both caches are empty
     * @param initializer runs on every acquisition, including newly created objects
     * @param recycler cleans the object before caching or discarding it
     * @param localLimit maximum idle count per thread
     * @param globalLimit maximum shared idle count
     */
    public RecycleObjectPool(Supplier<? extends T> factory, Consumer<? super T> initializer, Consumer<? super T> recycler, int localLimit, int globalLimit) {
        this(factory, initializer, recycler, UNIT_WEIGHT, localLimit, globalLimit);
    }

    /**
     * Creates a weight-bounded pool, for example using retained capacity in bytes.
     * Weight is measured after cleanup and before initialization on reuse. The
     * weigher must be non-throwing, return a positive value and remain stable while
     * an object is cached. Cleanup still runs for oversized or discarded objects.
     * @param factory creates a non-null object when both caches are empty
     * @param initializer runs on every acquisition, including newly created objects
     * @param recycler cleans the object before caching or discarding it
     * @param weigher measures retained weight in caller-defined units
     * @param localLimit maximum idle weight per thread; zero disables the local cache
     * @param globalLimit maximum shared idle weight; zero disables the shared cache
     */
    public RecycleObjectPool(Supplier<? extends T> factory, Consumer<? super T> initializer, Consumer<? super T> recycler, ToIntFunction<? super T> weigher, int localLimit, int globalLimit) {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.initializer = Objects.requireNonNull(initializer, "initializer");
        this.recycler = Objects.requireNonNull(recycler, "recycler");
        this.weigher = Objects.requireNonNull(weigher, "weigher");
        if (localLimit < 0 || globalLimit < 0) {
            throw new IllegalArgumentException("Cache limits must not be negative");
        }
        this.localLimit = localLimit;
        this.globalLimit = globalLimit;
    }

    /**
     * Returns an initialized cached or newly created object. If initialization
     * fails, that object is not put back into either cache.
     */
    public T get() {
        T object = null;
        if (this.localLimit > 0) {
            LocalCache<T> cache = this.local.get();
            object = cache.objects.pollLast();
            if (object != null) {
                cache.weight -= this.weigher.applyAsInt(object);
            }
        }
        if (object == null && this.globalLimit > 0) {
            object = this.global.poll();
            if (object != null) {
                this.globalWeight.addAndGet(-this.weigher.applyAsInt(object));
            }
        }
        if (object == null) {
            object = Objects.requireNonNull(this.factory.get(), "factory returned null");
        }
        this.initializer.accept(object);
        return object;
    }

    /**
     * Cleans and returns an object. Full or disabled caches discard it after
     * cleanup. If cleanup fails, the object is not cached and no budget is charged.
     */
    public void recycle(T object) {
        Objects.requireNonNull(object, "object");
        this.recycler.accept(object);
        int weight = this.weigher.applyAsInt(object);
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        if (weight <= this.localLimit) {
            LocalCache<T> cache = this.local.get();
            if (weight <= this.localLimit - cache.weight) {
                cache.objects.addLast(object);
                cache.weight += weight;
                return;
            }
        }
        if (weight <= this.globalLimit) {
            for (; ; ) {
                long current = this.globalWeight.get();
                if (weight > this.globalLimit - current) {
                    return;
                }
                // Reserve before publishing so concurrent returns cannot exceed the limit.
                if (this.globalWeight.compareAndSet(current, current + weight)) {
                    boolean offered = false;
                    try {
                        offered = this.global.offer(object);
                    } finally {
                        if (!offered) {
                            this.globalWeight.addAndGet(-weight);
                        }
                    }
                    return;
                }
            }
        }
    }

    private static final class LocalCache<T> {
        private final ArrayDeque<T> objects = new ArrayDeque<>(4);
        private       int           weight;
    }
}
