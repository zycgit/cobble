/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.lang.reflect.Field;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorArrayListTest {
    @Test
    public void prefixRemovalClearsReferencesAndCompactsAtConfiguredThreshold() throws Exception {
        CursorArrayList<Integer> list = new CursorArrayList<>(3);
        list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        Object[] backing = elements(list);
        assertEquals(Integer.valueOf(0), list.remove(0));
        assertEquals(1, head(list));
        assertNull(backing[0]);
        assertEquals(Integer.valueOf(1), list.remove(0));
        assertEquals(2, head(list));
        assertNull(backing[1]);
        assertEquals(Integer.valueOf(2), list.remove(0));
        assertEquals(0, head(list));
        assertSame(backing, elements(list));
        assertEquals(Arrays.asList(3, 4, 5), list);
        assertCleanSlots(list);
    }

    @Test
    public void tailExhaustionReclaimsPrefixBeforeGrowing() throws Exception {
        CursorArrayList<Integer> list = new CursorArrayList<>(100);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        Object[] backing = elements(list);
        list.subList(0, 3).clear();
        assertEquals(3, head(list));
        list.add(10);
        assertEquals(0, head(list));
        assertSame(backing, elements(list));
        assertEquals(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10), list);
        assertCleanSlots(list);
    }

    @Test
    public void emptyResetAndClearReuseArrayWithoutRetainingRemovedObjects() throws Exception {
        CursorArrayList<Object> list = new CursorArrayList<>(1000);
        for (int i = 0; i < 513; i++) {
            list.add(new Object());
        }
        Object[] backing = elements(list);
        list.subList(0, 512).clear();
        list.remove(0);
        assertEquals(0, head(list));
        assertSame(backing, elements(list));
        assertCleanSlots(list);
        list.add(new Object());
        list.clear();
        assertSame(backing, elements(list));
        assertCleanSlots(list);
    }

    @Test
    public void insertionsReusePrefixAndPreserveMiddleAndTailOrdering() throws Exception {
        CursorArrayList<Integer> list = new CursorArrayList<>(100);
        list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        list.subList(0, 3).clear();
        Object[] backing = elements(list);
        list.add(0, 2);
        list.addAll(0, Arrays.asList(0, 1));
        assertSame(backing, elements(list));
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), list);
        list.add(3, 99);
        list.addAll(4, Arrays.asList(null, 98));
        list.add(list.size(), 6);
        assertEquals(Arrays.asList(0, 1, 2, 99, null, 98, 3, 4, 5, 6), list);
        assertEquals(Integer.valueOf(99), list.set(3, 88));
        list.subList(3, 6).clear();
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6), list);
        assertCleanSlots(list);
    }

    @Test
    public void bulkPrefixRemovalCanCrossThresholdWithoutLosingLiveElements() throws Exception {
        CursorArrayList<Integer> list = new CursorArrayList<>(4);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        list.subList(0, 73).clear();
        assertEquals(0, head(list));
        assertEquals(27, list.size());
        for (int i = 0; i < 27; i++) {
            assertEquals(Integer.valueOf(i + 73), list.get(i));
        }
        assertCleanSlots(list);
    }

    @Test
    public void overlappingAndSelfInsertionUseSnapshotSemantics() throws Exception {
        CursorArrayList<Integer> list = new CursorArrayList<>(100);
        list.addAll(Arrays.asList(0, 1, 2, 3));
        list.remove(0);
        list.addAll(1, list.subList(0, 2));
        assertEquals(Arrays.asList(1, 1, 2, 2, 3), list);
        list.addAll(list);
        assertEquals(Arrays.asList(1, 1, 2, 2, 3, 1, 1, 2, 2, 3), list);
        assertCleanSlots(list);
    }

    @Test
    public void nullsAndTypedArraysFollowListContract() {
        CursorArrayList<String> list = new CursorArrayList<>();
        list.addAll(Arrays.asList("old", null, "a", "b"));
        list.remove(0);
        assertNull(list.remove(0));
        assertArrayEquals(new Object[] { "a", "b" }, list.toArray());
        assertArrayEquals(new String[] { "a", "b" }, list.toArray(new String[0]));
        String[] target = { "x", "x", "x", "sentinel" };
        assertSame(target, list.toArray(target));
        assertArrayEquals(new String[] { "a", "b", null, "sentinel" }, target);
        assertThrows(ArrayStoreException.class, () -> list.toArray(new Integer[0]));
        assertThrows(NullPointerException.class, () -> list.toArray((String[]) null));
    }

    @Test
    public void iteratorsAndNestedSublistsRemainCorrectAcrossCompaction() throws Exception {
        CursorArrayList<Integer> list = new CursorArrayList<>(2);
        list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        Iterator<Integer> iterator = list.iterator();
        assertEquals(Integer.valueOf(0), iterator.next());
        iterator.remove();
        assertEquals(Integer.valueOf(1), iterator.next());
        iterator.remove();
        assertEquals(Integer.valueOf(2), iterator.next());
        List<Integer> view = list.subList(0, 4);
        view.subList(0, 2).clear();
        assertEquals(Arrays.asList(4, 5), view);
        view.add(0, 3);
        ListIterator<Integer> edit = view.listIterator();
        assertEquals(Integer.valueOf(3), edit.next());
        edit.set(30);
        edit.add(31);
        assertEquals(Arrays.asList(30, 31, 4, 5), list);
        assertCleanSlots(list);
    }

    @Test
    public void structuralModificationsInvalidateIteratorsAndViews() {
        CursorArrayList<Integer> list = new CursorArrayList<>(2);
        list.addAll(Arrays.asList(1, 2, 3));
        Iterator<Integer> iterator = list.iterator();
        List<Integer> view = list.subList(0, 2);
        list.remove(0);
        assertThrows(ConcurrentModificationException.class, iterator::next);
        assertThrows(ConcurrentModificationException.class, view::size);
        assertThrows(ConcurrentModificationException.class, () -> list.forEach(value -> list.add(value)));
    }

    @Test
    public void invalidOperationsLeaveContentsUntouched() {
        assertThrows(IllegalArgumentException.class, () -> new CursorArrayList<>(0));
        assertThrows(IllegalArgumentException.class, () -> new CursorArrayList<>(-1));
        CursorArrayList<Integer> list = new CursorArrayList<>(1);
        list.addAll(Arrays.asList(1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(2, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));
        assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertThrows(NullPointerException.class, () -> list.forEach(null));
        assertFalse(list.addAll(Collections.emptyList()));
        assertEquals(Arrays.asList(1, 2), list);
    }

    @Test
    public void mixedOperationsMatchArrayListAcrossThresholds() throws Exception {
        for (int threshold : new int[] { 1, 2, 3, 8, 64, 257 }) {
            Random random = new Random(7319 + threshold);
            CursorArrayList<Integer> actual = new CursorArrayList<>(threshold);
            List<Integer> expected = new ArrayList<>();
            for (int step = 0; step < 20000; step++) {
                Integer value = random.nextInt(7) == 0 ? null : random.nextInt(1000);
                int index = random.nextInt(expected.size() + 1);
                switch (random.nextInt(9)) {
                    case 0:
                        actual.add(value);
                        expected.add(value);
                        break;
                    case 1:
                        actual.add(index, value);
                        expected.add(index, value);
                        break;
                    case 2:
                        List<Integer> extra = Arrays.asList(value, null, step);
                        actual.addAll(index, extra);
                        expected.addAll(index, extra);
                        break;
                    case 3:
                        if (!expected.isEmpty()) {
                            assertEquals(expected.remove(0), actual.remove(0));
                        }
                        break;
                    case 4:
                        if (index < expected.size()) {
                            assertEquals(expected.remove(index), actual.remove(index));
                        }
                        break;
                    case 5:
                        int end = index + random.nextInt(expected.size() - index + 1);
                        expected.subList(index, end).clear();
                        actual.subList(index, end).clear();
                        break;
                    case 6:
                        if (index < expected.size()) {
                            assertEquals(expected.set(index, value), actual.set(index, value));
                        }
                        break;
                    case 7:
                        if (random.nextInt(20) == 0) {
                            expected.clear();
                            actual.clear();
                        }
                        break;
                    default:
                        expected.remove(value);
                        actual.remove(value);
                        break;
                }
                assertEquals(expected, actual);
                assertEquals(expected.hashCode(), actual.hashCode());
                assertArrayEquals(expected.toArray(), actual.toArray());
                if (step % 31 == 0) {
                    assertCleanSlots(actual);
                    assertTrue(head(actual) < threshold);
                }
            }
        }
    }

    private static int head(CursorArrayList<?> list) throws Exception {
        Field field = CursorArrayList.class.getDeclaredField("head");
        field.setAccessible(true);
        return field.getInt(list);
    }

    private static Object[] elements(CursorArrayList<?> list) throws Exception {
        Field field = CursorArrayList.class.getDeclaredField("elements");
        field.setAccessible(true);
        return (Object[]) field.get(list);
    }

    private static void assertCleanSlots(CursorArrayList<?> list) throws Exception {
        Object[] elements = elements(list);
        int head = head(list);
        assertTrue(head >= 0 && head + list.size() <= elements.length);
        for (int i = 0; i < elements.length; i++) {
            if (i < head || i >= head + list.size()) {
                assertNull("retained slot " + i, elements[i]);
            }
        }
    }
}
