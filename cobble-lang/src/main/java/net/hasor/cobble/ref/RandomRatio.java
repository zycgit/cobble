/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.hasor.cobble.RandomUtils;

/**
 * 控制操作生成比率
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2022-07-25
 */
public class RandomRatio<T> {
    private final    List<Boundary<T>> opsBoundary = new CopyOnWriteArrayList<>();
    private volatile long              maxBoundary = 0;

    private long maxBoundary(List<Boundary<T>> opsBoundary) {
        Boundary<?> boundary = opsBoundary.stream().max(Comparator.comparingLong(Boundary::getMaximum)).orElse(null);
        return (boundary != null) ? boundary.getMaximum() : 0L;
    }

    public synchronized void clearRatio() {
        this.opsBoundary.clear();
        this.maxBoundary = 0;
    }

    public synchronized void addRatio(int ratio, T value) {
        this.opsBoundary.add(new Boundary<>(value, this.maxBoundary, this.maxBoundary + ratio));
        this.maxBoundary = maxBoundary(this.opsBoundary);
    }

    private long getMaxBoundary() {
        return this.maxBoundary;
    }

    public T getByBoundary(long boundaryNumber) {
        for (Boundary<T> entry : this.opsBoundary) {
            if (entry.getMinimum() <= boundaryNumber && boundaryNumber <= entry.getMaximum()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public T getByIndex(int index) {
        return this.opsBoundary.get(index).getValue();
    }

    public T getLast() {
        if (this.opsBoundary.isEmpty()) {
            return null;
        } else {
            return this.opsBoundary.get(this.opsBoundary.size() - 1).getValue();
        }
    }

    public T getFirst() {
        if (this.opsBoundary.isEmpty()) {
            return null;
        } else {
            return this.opsBoundary.get(0).getValue();
        }
    }

    public T getByRandom() {
        return getByBoundary(RandomUtils.nextLong(0, this.maxBoundary));
    }

    public boolean isEmpty() {
        return this.opsBoundary.isEmpty();
    }

    public int getBoundaryCount() {
        return this.opsBoundary.size();
    }

    public void forEach(Consumer<T> action) {
        Objects.requireNonNull(action);
        for (Boundary<T> t : this.opsBoundary) {
            action.accept(t.value);
        }
    }

    /** 拥有上下界的一个范围 */
    protected static class Boundary<V> extends Range<Long> {
        private final V value;

        public Boundary(V value, long lower, long upper) {
            super(lower, upper, Long::compare);
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Boundary boundary = (Boundary) o;
            return boundary.value == this.value && super.equals(boundary);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value, getMinimum(), getMaximum());
        }
    }
}
