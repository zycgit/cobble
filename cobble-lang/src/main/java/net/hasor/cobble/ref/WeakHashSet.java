/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.*;

/**
 * 参照{@link java.util.HashSet}实现的 WeakHashSet.
 * 详细介绍参看{@link java.util.Set}和{@link java.util.WeakHashMap}功能
 * @author (网络收集)
 * @version : 2013-11-8
 */
public class WeakHashSet<E> extends AbstractSet<E> implements Set<E> {
    private final transient WeakHashMap<E, Object> map;
    private static final    Object                 PRESENT = new Object();

    public WeakHashSet() {
        this.map = new WeakHashMap<>();
    }

    public WeakHashSet(final Collection<? extends E> c) {
        this.map = new WeakHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        this.addAll(c);
    }

    public WeakHashSet(final int initialCapacity, final float loadFactor) {
        this.map = new WeakHashMap<>(initialCapacity, loadFactor);
    }

    public WeakHashSet(final int initialCapacity) {
        this.map = new WeakHashMap<>(initialCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean add(final E o) {
        return this.map.put(o, WeakHashSet.PRESENT) == null;
    }

    @Override
    public boolean remove(final Object o) {
        return this.map.remove(o) == WeakHashSet.PRESENT;
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
