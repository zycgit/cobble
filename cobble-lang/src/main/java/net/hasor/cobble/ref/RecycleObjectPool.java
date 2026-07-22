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
package net.hasor.cobble.ref;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通用对象池工具。
 * <p>
 * 采用两级缓存策略：
 * <ol>
 *   <li><b>L1 缓存（ThreadLocal）</b>：无锁、高速，优先使用。</li>
 *   <li><b>L2 缓存（全局共享队列）</b>：线程安全、无锁（ConcurrentLinkedQueue），
 *       用于跨线程回收场景的兜底。</li>
 * </ol>
 * <p>
 * 提供三套 API：
 * <ul>
 *   <li><b>Recycler API（推荐）</b>：{@link #recycler(Supplier, Consumer, Consumer)}。
 *       将类型索引和对象工厂封装为类型安全的复用句柄。</li>
 *   <li><b>索引 API（快速路径）</b>：{@link #get(int, ObjHandler)} / {@link #free(int, Object)}。
 *       通过类型索引直接定位缓存槽，无需 ConcurrentHashMap 查找，性能最优。
 *       建议在静态初始化阶段通过 {@link #registerType()} 预先注册类型索引。</li>
 *   <li><b>Class API（兼容路径）</b>：{@link #get(Class, ObjHandler)} / {@link #free(Class, Object)}。
 *       按 Class 动态查找对应队列，适合类型数量不固定或测试场景。</li>
 * </ul>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2022-11-01
 */
@SuppressWarnings("unchecked")
public class RecycleObjectPool {
    private static final int                                                          LOCAL_CAPACITY       = 1024;
    private static final int                                                          GLOBAL_CAPACITY      = 4096;
    /** 索引快速路径支持的最大类型数量。 */
    private static final int                                                          MAX_INDEXED_TYPES    = 32;
    private static final AtomicInteger                                                TYPE_COUNTER         = new AtomicInteger(0);
    private static final RecyclerQueue[]                                              GLOBAL_INDEXED       = new RecyclerQueue[MAX_INDEXED_TYPES];
    private static final ThreadLocal<ArrayDeque<Object>[]>                            LOCAL_INDEXED_CACHES = ThreadLocal.withInitial(RecycleObjectPool::createIndexedCaches);
    // Per-type ThreadLocal（消除每次 get/free 的 HashMap 查找开销）
    private static final ConcurrentHashMap<Class<?>, ThreadLocal<ArrayDeque<Object>>> PER_TYPE_LOCAL       = new ConcurrentHashMap<>();
    // L2：全局共享缓存
    private static final Map<Class<?>, RecyclerQueue>                                 GLOBAL_CACHE         = new ConcurrentHashMap<>();

    private static ArrayDeque<Object>[] createIndexedCaches() {
        ArrayDeque<Object>[] arr = new ArrayDeque[MAX_INDEXED_TYPES];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new ArrayDeque<>();
        }
        return arr;
    }

    /**
     * 注册新类型以获取索引快速路径所需的类型索引。
     * 应在静态初始化阶段调用一次。
     * @return 用于 {@link #get(int, ObjHandler)} 和 {@link #free(int, Object)} 的类型索引。
     */
    public static int registerType() {
        int idx = TYPE_COUNTER.getAndIncrement();
        if (idx >= MAX_INDEXED_TYPES) {
            throw new IllegalStateException("Too many recycled types registered: " + idx + " (max " + MAX_INDEXED_TYPES + ")");
        }
        GLOBAL_INDEXED[idx] = new RecyclerQueue();
        return idx;
    }

    /**
     * Creates a type-safe handle with acquire and recycle lifecycle callbacks.
     * <p>
     * The initializer runs every time an object is acquired, including immediately
     * after creation. The recycle callback runs before the object is cached. When
     * that callback fails, the object is deliberately not returned to the pool.
     * @param factory factory used when the pool is empty
     * @param initializer callback invoked before an acquired object is returned
     * @param recycler callback invoked before an object is cached
     * @param <T> pooled object type
     * @return recycler handle for the object type
     */
    public static <T> Recycler<T> recycler(Supplier<? extends T> factory, Consumer<? super T> initializer, Consumer<? super T> recycler) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (initializer == null) {
            throw new NullPointerException("initializer");
        }
        if (recycler == null) {
            throw new NullPointerException("recycler");
        }
        return new Recycler<>(registerType(), factory, initializer, recycler);
    }

    // ==================== Class API（兼容路径） ====================

    /**
     * 索引快速路径：从池中取出可复用对象，池为空则创建新对象。
     * 通过数组索引定位 ThreadLocal，无 ConcurrentHashMap 查找开销。
     */
    public static <T> T get(int typeIndex, ObjHandler<?> handler) {
        return getIndexed(typeIndex, handler::create);
    }

    private static <T> T getIndexed(int typeIndex, Supplier<?> factory) {
        // 1. 尝试 L1（ThreadLocal）
        ArrayDeque<Object> localDeque = LOCAL_INDEXED_CACHES.get()[typeIndex];
        if (!localDeque.isEmpty()) {
            return (T) localDeque.pop();
        }

        // 2. 尝试 L2（全局共享）
        RecyclerQueue rq = GLOBAL_INDEXED[typeIndex];
        Object obj = rq.queue.poll();
        if (obj != null) {
            rq.size.decrementAndGet();
            return (T) obj;
        }

        // 3. 创建新对象
        return (T) factory.get();
    }

    /**
     * 索引快速路径：将对象归还到池中以供复用。
     * 通过数组索引定位 ThreadLocal，无 ConcurrentHashMap 查找开销。
     */
    public static void free(int typeIndex, Object obj) {
        // 1. 尝试 L1（ThreadLocal）
        ArrayDeque<Object> localDeque = LOCAL_INDEXED_CACHES.get()[typeIndex];
        if (localDeque.size() < LOCAL_CAPACITY) {
            localDeque.push(obj);
            return;
        }

        // 2. 尝试 L2（全局共享）
        RecyclerQueue rq = GLOBAL_INDEXED[typeIndex];
        if (rq.size.get() < GLOBAL_CAPACITY) {
            if (rq.queue.offer(obj)) {
                rq.size.incrementAndGet();
            }
        }
        // 3. 两级均满时丢弃，由 GC 负责回收
    }

    private static ArrayDeque<Object> localQueue(Class<?> objType) {
        ThreadLocal<ArrayDeque<Object>> tl = PER_TYPE_LOCAL.get(objType);
        if (tl == null) {
            tl = PER_TYPE_LOCAL.computeIfAbsent(objType, k -> ThreadLocal.withInitial(ArrayDeque::new));
        }
        return tl.get();
    }

    private static RecyclerQueue globalQueue(Class<?> objType) {
        return GLOBAL_CACHE.computeIfAbsent(objType, k -> new RecyclerQueue());
    }

    /** Class 兼容路径：从池中取出可复用对象，池为空则创建新对象。 */
    public static <T> T get(Class<T> objType, ObjHandler<?> handler) {
        // 1. 尝试 L1（ThreadLocal）
        ArrayDeque<Object> localDeque = localQueue(objType);
        if (!localDeque.isEmpty()) {
            return (T) localDeque.pop();
        }

        // 2. 尝试 L2（全局共享）—— 本地为空时从全局获取
        RecyclerQueue recyclerQueue = globalQueue(objType);
        Object obj = recyclerQueue.queue.poll();
        if (obj != null) {
            recyclerQueue.size.decrementAndGet();
            return (T) obj;
        }

        // 3. 创建新对象
        return (T) handler.create();
    }

    /** Class 兼容路径：将对象归还到池中以供复用。 */
    public static void free(Class<?> objType, Object obj) {
        assert objType.isInstance(obj) : "Type mismatch: expected " + objType.getName() + ", got " + obj.getClass().getName();
        // 1. 尝试 L1（ThreadLocal）
        ArrayDeque<Object> localDeque = localQueue(objType);
        if (localDeque.size() < LOCAL_CAPACITY) {
            localDeque.push(obj);
            return;
        }

        // 2. 尝试 L2（全局共享）—— 本地满时向全局提交
        RecyclerQueue recyclerQueue = globalQueue(objType);
        if (recyclerQueue.size.get() < GLOBAL_CAPACITY) {
            if (recyclerQueue.queue.offer(obj)) {
                recyclerQueue.size.incrementAndGet();
            }
        }

        // 3. 两级均满时丢弃，由 GC 负责回收
    }

    // ==================== 内部结构 ====================

    private static class RecyclerQueue {
        final Queue<Object> queue = new ConcurrentLinkedQueue<>();
        final AtomicInteger size  = new AtomicInteger(0);
    }

    /**
     * Type-safe handle for acquiring and recycling instances through the indexed
     * object-pool fast path.
     * @param <T> pooled object type
     */
    public static final class Recycler<T> {
        private final int                   typeIndex;
        private final Supplier<? extends T> factory;
        private final Consumer<? super T>   initializer;
        private final Consumer<? super T>   recycler;

        private Recycler(int typeIndex, Supplier<? extends T> factory, Consumer<? super T> initializer, Consumer<? super T> recycler) {
            this.typeIndex = typeIndex;
            this.factory = factory;
            this.initializer = initializer;
            this.recycler = recycler;
        }

        /** Returns a cached instance, or creates one when the pool is empty. */
        public T get() {
            T object = RecycleObjectPool.getIndexed(this.typeIndex, this.factory);
            this.initializer.accept(object);
            return object;
        }

        /** Returns an instance to this recycler. */
        public void recycle(T object) {
            if (object == null) {
                throw new NullPointerException("object");
            }
            this.recycler.accept(object);
            RecycleObjectPool.free(this.typeIndex, object);
        }
    }

    /**
     * 对象池回收处理接口，用于定义对象的创建和重置逻辑。
     * @author 赵永春 (zyc@hasor.net)
     * @version : 2022-11-01
     */
    public static interface ObjHandler<T> {

        /** 创建一个新对象。 */
        T create();

        /** 对象归还到池前的重置/清理操作。 */
        void free(T tar);
    }
}
