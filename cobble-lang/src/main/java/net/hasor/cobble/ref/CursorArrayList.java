/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.*;
import java.util.function.Consumer;

/**
 * An array-backed list optimized for FIFO consumption through {@code remove(0)}
 * and {@code subList(0, count).clear()}.
 * <p>
 * Removed references are cleared immediately. Removing a prefix advances a
 * cursor instead of shifting every remaining element. The live range is moved
 * to the beginning when the consumed prefix reaches the configured threshold,
 * or when appending needs to reclaim prefix space. An empty list resets its
 * cursor without copying. Compaction never discards live elements.
 * <p>
 * The threshold counts consumed array slots, not live elements or retained
 * capacity. This list grows as needed and retains its backing array after a
 * clear. It permits null elements and is not thread-safe. Iterators and sublists
 * have the usual {@link AbstractList} fail-fast behavior.
 * @param <E> element type
 */
public final class CursorArrayList<E> extends AbstractList<E> implements RandomAccess {
    private static final Object[] EMPTY    = new Object[0];
    private final        int      compactThreshold;
    private              Object[] elements = EMPTY;
    private              int      head;
    private              int      size;

    /** Creates a list that compacts after consuming 64 prefix slots. */
    public CursorArrayList() {
        this(64);
    }

    /**
     * @param compactThreshold positive consumed-prefix threshold; 1 compacts on every prefix removal
     */
    public CursorArrayList(int compactThreshold) {
        if (compactThreshold <= 0) {
            throw new IllegalArgumentException("compactThreshold must be positive");
        }
        this.compactThreshold = compactThreshold;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        Objects.checkIndex(index, this.size);
        return (E) this.elements[this.head + index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        Objects.checkIndex(index, this.size);
        int position = this.head + index;
        E previous = (E) this.elements[position];
        this.elements[position] = element;
        return previous;
    }

    @Override
    public boolean add(E element) {
        this.ensureRoom(1);
        this.elements[this.head + this.size++] = element;
        this.modCount++;
        return true;
    }

    @Override
    public void add(int index, E element) {
        this.checkPosition(index);
        if (index == 0 && this.head > 0) {
            this.elements[--this.head] = element;
        } else {
            this.ensureRoom(1);
            int position = this.head + index;
            System.arraycopy(this.elements, position, this.elements, position + 1, this.size - index);
            this.elements[position] = element;
        }
        this.size++;
        this.modCount++;
    }

    @Override
    public boolean addAll(Collection<? extends E> source) {
        return this.addAll(this.size, source);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> source) {
        this.checkPosition(index);
        Object[] incoming = source.toArray();
        int count = incoming.length;
        if (count == 0) {
            return false;
        }
        if (index == 0 && this.head >= count) {
            this.head -= count;
        } else {
            this.ensureRoom(count);
            int position = this.head + index;
            System.arraycopy(this.elements, position, this.elements, position + count, this.size - index);
        }
        System.arraycopy(incoming, 0, this.elements, this.head + index, count);
        this.size += count;
        this.modCount++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        Objects.checkIndex(index, this.size);
        int position = this.head + index;
        E previous = (E) this.elements[position];
        if (index == 0) {
            this.elements[this.head++] = null;
            this.size--;
            this.compactConsumedPrefix();
        } else {
            int moving = this.size - index - 1;
            System.arraycopy(this.elements, position + 1, this.elements, position, moving);
            this.elements[this.head + --this.size] = null;
        }
        this.modCount++;
        return previous;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, this.size);
        int count = toIndex - fromIndex;
        if (count == 0) {
            return;
        }
        if (fromIndex == 0) {
            Arrays.fill(this.elements, this.head, this.head + count, null);
            this.head += count;
            this.size -= count;
            this.compactConsumedPrefix();
        } else {
            int oldEnd = this.head + this.size;
            System.arraycopy(this.elements, this.head + toIndex, this.elements, this.head + fromIndex, this.size - toIndex);
            this.size -= count;
            Arrays.fill(this.elements, this.head + this.size, oldEnd, null);
        }
        this.modCount++;
    }

    @Override
    public void clear() {
        Arrays.fill(this.elements, this.head, this.head + this.size, null);
        this.head = 0;
        this.size = 0;
        this.modCount++;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOfRange(this.elements, this.head, this.head + this.size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] target) {
        if (target.length < this.size) {
            return (T[]) Arrays.copyOfRange(this.elements, this.head, this.head + this.size, target.getClass());
        }

        System.arraycopy(this.elements, this.head, target, 0, this.size);
        if (target.length > this.size) {
            target[this.size] = null;
        }
        return target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action, "action");
        int expected = this.modCount;
        int end = this.head + this.size;
        for (int i = this.head; i < end && this.modCount == expected; i++) {
            action.accept((E) this.elements[i]);
        }
        if (this.modCount != expected) {
            throw new ConcurrentModificationException();
        }
    }

    private void checkPosition(int index) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size);
        }
    }

    private void ensureRoom(int additional) {
        int required = this.size + additional;
        if (required < 0) {
            throw new OutOfMemoryError("Required array size too large");
        }
        if (additional <= this.elements.length - this.head - this.size) {
            return;
        }
        if (required <= this.elements.length) {
            this.compact();
            return;
        }
        long grown = Math.max(10L, this.elements.length + (this.elements.length >> 1) + 1L);
        int capacity = (int) Math.min(Integer.MAX_VALUE, Math.max(grown, required));
        Object[] replacement = new Object[capacity];
        System.arraycopy(this.elements, this.head, replacement, 0, this.size);
        this.elements = replacement;
        this.head = 0;
    }

    private void compactConsumedPrefix() {
        if (this.size == 0) {
            this.head = 0;
        } else if (this.head >= this.compactThreshold) {
            this.compact();
        }
    }

    private void compact() {
        int previousEnd = this.head + this.size;
        System.arraycopy(this.elements, this.head, this.elements, 0, this.size);
        Arrays.fill(this.elements, this.size, previousEnd, null);
        this.head = 0;
    }
}
