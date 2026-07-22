package net.hasor.cobble.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * RecycleObjectPool 单元测试。
 */
public class RecycleObjectPoolTest {

    // ==================== 测试用 Handler ====================

    /** 带计数的 Handler，方便验证 create() 是否被调用 */
    static class CountingHandler implements RecycleObjectPool.ObjHandler<StringBuilder> {
        final AtomicInteger createCount = new AtomicInteger(0);

        @Override
        public StringBuilder create() {
            createCount.incrementAndGet();
            return new StringBuilder();
        }

        @Override
        public void free(StringBuilder tar) {
            tar.setLength(0);
        }
    }

    // ==================== Class API 测试 ====================

    @Test
    public void classApi_getFromEmptyPool_createsNewObject() {
        CountingHandler handler = new CountingHandler();
        StringBuilder obj = RecycleObjectPool.get(StringBuilder.class, handler);
        Assert.assertNotNull(obj);
        Assert.assertEquals(1, handler.createCount.get());
    }

    @Test
    public void classApi_freeAndGet_returnsRecycledObject() {
        CountingHandler handler = new CountingHandler();

        // 先 get 拿到一个对象
        StringBuilder obj1 = RecycleObjectPool.get(StringBuilder.class, handler);
        obj1.append("recycled");

        // 归还
        RecycleObjectPool.free(StringBuilder.class, obj1);

        // 再次 get，应该复用同一个对象（从 L1 ThreadLocal 取出）
        StringBuilder obj2 = RecycleObjectPool.get(StringBuilder.class, handler);
        Assert.assertSame("应复用同一对象实例", obj1, obj2);
        // create 只应被调用 1 次
        Assert.assertEquals(1, handler.createCount.get());
    }

    @Test
    public void classApi_multipleObjects_allRecycled() {
        CountingHandler handler = new CountingHandler();
        int count = 10;
        List<StringBuilder> objs = new ArrayList<>();

        // 取出 10 个
        for (int i = 0; i < count; i++) {
            objs.add(RecycleObjectPool.get(StringBuilder.class, handler));
        }
        Assert.assertEquals(count, handler.createCount.get());

        // 全部归还
        for (StringBuilder o : objs) {
            RecycleObjectPool.free(StringBuilder.class, o);
        }

        // 再取 10 个，全部应命中缓存，create 不再增加
        for (int i = 0; i < count; i++) {
            StringBuilder recycled = RecycleObjectPool.get(StringBuilder.class, handler);
            Assert.assertTrue("复用对象应在原来的列表中", objs.contains(recycled));
        }
        Assert.assertEquals(count, handler.createCount.get());
    }

    @Test
    public void classApi_continuousGetFree_onlyOneCreateCall() {
        CountingHandler handler = new CountingHandler();

        StringBuilder obj = RecycleObjectPool.get(StringBuilder.class, handler);
        RecycleObjectPool.free(StringBuilder.class, obj);

        StringBuilder obj2 = RecycleObjectPool.get(StringBuilder.class, handler);
        RecycleObjectPool.free(StringBuilder.class, obj2);

        StringBuilder obj3 = RecycleObjectPool.get(StringBuilder.class, handler);

        // 整个过程只需 create 一次
        Assert.assertEquals(1, handler.createCount.get());
        Assert.assertSame(obj, obj2);
        Assert.assertSame(obj2, obj3);
    }

    // ==================== 索引 API 测试 ====================

    /** 用于索引 API 测试的专属 Handler，避免与 Class API 共享同一个 Class 键 */
    static class PointHandler implements RecycleObjectPool.ObjHandler<int[]> {
        final AtomicInteger createCount = new AtomicInteger(0);

        @Override
        public int[] create() {
            createCount.incrementAndGet();
            return new int[] { 0, 0 };
        }

        @Override
        public void free(int[] tar) {
            tar[0] = 0;
            tar[1] = 0;
        }
    }

    @Test
    public void indexedApi_getFromEmptyPool_createsNewObject() {
        int idx = RecycleObjectPool.registerType();
        PointHandler handler = new PointHandler();

        int[] obj = RecycleObjectPool.get(idx, handler);
        Assert.assertNotNull(obj);
        Assert.assertEquals(1, handler.createCount.get());
    }

    @Test
    public void indexedApi_freeAndGet_returnsRecycledObject() {
        int idx = RecycleObjectPool.registerType();
        PointHandler handler = new PointHandler();

        int[] obj1 = RecycleObjectPool.get(idx, handler);
        RecycleObjectPool.free(idx, obj1);

        int[] obj2 = RecycleObjectPool.get(idx, handler);
        Assert.assertSame("应复用同一对象实例", obj1, obj2);
        Assert.assertEquals(1, handler.createCount.get());
    }

    @Test
    public void indexedApi_multipleObjects_allRecycled() {
        int idx = RecycleObjectPool.registerType();
        PointHandler handler = new PointHandler();
        int count = 5;
        List<int[]> objs = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            objs.add(RecycleObjectPool.get(idx, handler));
        }
        Assert.assertEquals(count, handler.createCount.get());

        for (int[] o : objs) {
            RecycleObjectPool.free(idx, o);
        }

        for (int i = 0; i < count; i++) {
            int[] recycled = RecycleObjectPool.get(idx, handler);
            Assert.assertTrue("复用对象应在原来的列表中", objs.contains(recycled));
        }
        Assert.assertEquals(count, handler.createCount.get());
    }

    // ==================== 跨线程（L2 全局缓存）测试 ====================

    @Test
    public void classApi_crossThread_l2CacheRecycling() throws InterruptedException {
        RecycleObjectPool.ObjHandler<AtomicInteger> handler = new RecycleObjectPool.ObjHandler<AtomicInteger>() {
            @Override
            public AtomicInteger create() {
                return new AtomicInteger(-1);
            }

            @Override
            public void free(AtomicInteger tar) {
                tar.set(-1);
            }
        };

        AtomicReference<AtomicInteger> freedObj = new AtomicReference<>();
        CountDownLatch freedLatch = new CountDownLatch(1);
        CountDownLatch consumedLatch = new CountDownLatch(1);
        AtomicReference<AtomicInteger> gotObj = new AtomicReference<>();

        // 线程 A：先 get，再填满 L1 使对象落入 L2，或直接在新线程中 get 后 free（进 L2）
        Thread producer = new Thread(() -> {
            AtomicInteger obj = RecycleObjectPool.get(AtomicInteger.class, handler);
            obj.set(42);
            freedObj.set(obj);
            // 填满 L1（1024 容量），让此 obj 进入 L2
            // 实际上只需在新线程 free，L1 是 ThreadLocal，该线程的 L1 为空，所以会进 L1；
            // 为验证跨线程复用，这里先把 L1 填满再 free
            for (int i = 0; i < 1024; i++) {
                RecycleObjectPool.free(AtomicInteger.class, new AtomicInteger(i));
            }
            // 此时 L1 已满，free 会进 L2
            RecycleObjectPool.free(AtomicInteger.class, obj);
            freedLatch.countDown();
        });

        // 线程 B：在 A free 之后从池里 get，若能从 L2 拿到则复用成功
        Thread consumer = new Thread(() -> {
            try {
                freedLatch.await();
                // 线程 B 的 L1 为空，先检查 L2
                AtomicInteger obj = RecycleObjectPool.get(AtomicInteger.class, handler);
                gotObj.set(obj);
                consumedLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join(3000);
        consumer.join(3000);
        consumedLatch.await();

        // 线程 B 拿到的对象要么是从 L2 复用的（value=42），要么是新建的（value=-1）
        // 无论如何，对象不能为 null
        Assert.assertNotNull(gotObj.get());
        // 如果 L2 生效，应该拿到 freedObj（value 42）
        Assert.assertSame("应从 L2 全局缓存复用跨线程归还的对象", freedObj.get(), gotObj.get());
    }

    // ==================== ObjHandler 接口测试 ====================

    @Test
    public void objHandler_create_calledExactlyOnceWhenPoolEmpty() {
        CountingHandler handler = new CountingHandler();
        // 连续 3 次 get，每次 free 后再 get，create 只应调用 1 次
        StringBuilder obj = RecycleObjectPool.get(StringBuilder.class, handler);
        RecycleObjectPool.free(StringBuilder.class, obj);
        RecycleObjectPool.get(StringBuilder.class, handler);
        Assert.assertEquals(1, handler.createCount.get());
    }

    @Test
    public void objHandler_create_calledAgainAfterAllObjectsInUse() {
        RecycleObjectPool.ObjHandler<Object> handler = new RecycleObjectPool.ObjHandler<Object>() {
            final AtomicInteger cnt = new AtomicInteger(0);

            @Override
            public Object create() {
                cnt.incrementAndGet();
                return new Object();
            }

            @Override
            public void free(Object tar) { /* no-op */ }
        };

        // 两个对象同时使用，均不归还
        Object a = RecycleObjectPool.get(Object.class, handler);
        Object b = RecycleObjectPool.get(Object.class, handler);
        Assert.assertNotSame(a, b);
        // 两个对象都是新建的
        Assert.assertEquals(2, ((AtomicInteger) getField(handler, "cnt")).get());
    }

    @Test
    public void recycler_reusesObjectFromItsOwnIndexedSlot() {
        RecycleObjectPool.Recycler<StringBuilder> recycler = RecycleObjectPool.recycler(StringBuilder::new, object -> {
        }, object -> {
        });
        StringBuilder first = recycler.get();
        first.append("reused");
        recycler.recycle(first);

        StringBuilder second = recycler.get();
        Assert.assertSame(first, second);
        Assert.assertEquals("reused", second.toString());
    }

    @Test
    public void recycler_keepsDifferentTypesInDifferentSlots() {
        RecycleObjectPool.Recycler<StringBuilder> strings = RecycleObjectPool.recycler(StringBuilder::new, object -> {
        }, object -> {
        });
        RecycleObjectPool.Recycler<AtomicInteger> integers = RecycleObjectPool.recycler(AtomicInteger::new, object -> {
        }, object -> {
        });

        StringBuilder string = strings.get();
        AtomicInteger integer = integers.get();
        strings.recycle(string);
        integers.recycle(integer);

        Assert.assertSame(string, strings.get());
        Assert.assertSame(integer, integers.get());
    }

    @Test
    public void recycler_invokesLifecycleCallbacksInOrder() {
        StringBuilder events = new StringBuilder();
        RecycleObjectPool.Recycler<Object> recycler = RecycleObjectPool.recycler(() -> {
            events.append("create>");
            return new Object();
        }, object -> events.append("init>"), object -> events.append("recycle>"));

        Object object = recycler.get();
        recycler.recycle(object);
        Assert.assertEquals("create>init>recycle>", events.toString());

        Assert.assertSame(object, recycler.get());
        Assert.assertEquals("create>init>recycle>init>", events.toString());
    }

    @Test
    public void recycler_doesNotCacheObjectWhenRecycleCallbackFails() {
        AtomicInteger created = new AtomicInteger();
        RecycleObjectPool.Recycler<Object> recycler = RecycleObjectPool.recycler(() -> {
            created.incrementAndGet();
            return new Object();
        }, object -> {
        }, object -> {
            throw new IllegalStateException("recycle failed");
        });

        Object first = recycler.get();
        try {
            recycler.recycle(first);
            Assert.fail("Expected recycle callback failure.");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("recycle failed", expected.getMessage());
        }

        Assert.assertNotSame(first, recycler.get());
        Assert.assertEquals(2, created.get());
    }

    /** 反射获取匿名类字段，仅用于测试断言 */
    private static AtomicInteger getField(Object obj, String name) {
        try {
            for (java.lang.reflect.Field f : obj.getClass().getDeclaredFields()) {
                if (f.getType() == AtomicInteger.class) {
                    f.setAccessible(true);
                    return (AtomicInteger) f.get(obj);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Field not found: " + name);
    }
}
