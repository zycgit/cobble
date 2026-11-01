/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {

    @Test
    public void toMapTest() {
        Assertions.assertNull(ArrayUtils.toMap(null));

        String[][] mapData = new String[][] { { "RED", "#FF0000" }, { "GREEN", "#00FF00" }, { "BLUE", "#0000FF" } };
        Map<Object, Object> colorMap = ArrayUtils.toMap(mapData);
        Assertions.assertEquals(3, colorMap.size());
        Assertions.assertEquals("#FF0000", colorMap.get("RED"));
        Assertions.assertEquals("#00FF00", colorMap.get("GREEN"));
        Assertions.assertEquals("#0000FF", colorMap.get("BLUE"));

        try {
            ArrayUtils.toMap(new String[][] { { "RED" } });
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ArrayUtils.toMap(new Object[] { "RED" });
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void cloneTest() {
        Assertions.assertNull(ArrayUtils.clone((Object[]) null));
        Assertions.assertNull(ArrayUtils.clone((long[]) null));
        Assertions.assertNull(ArrayUtils.clone((int[]) null));
        Assertions.assertNull(ArrayUtils.clone((short[]) null));
        Assertions.assertNull(ArrayUtils.clone((char[]) null));
        Assertions.assertNull(ArrayUtils.clone((byte[]) null));
        Assertions.assertNull(ArrayUtils.clone((double[]) null));
        Assertions.assertNull(ArrayUtils.clone((float[]) null));
        Assertions.assertNull(ArrayUtils.clone((boolean[]) null));

        String[] strArray = new String[] { "a", "b" };
        String[] strArrayClone = ArrayUtils.clone(strArray);
        Assertions.assertNotSame(strArray, strArrayClone);
        Assertions.assertEquals(strArray.length, strArrayClone.length);
        Assertions.assertEquals(strArray[0], strArrayClone[0]);

        int[] intArray = new int[] { 1, 2 };
        int[] intArrayClone = ArrayUtils.clone(intArray);
        Assertions.assertNotSame(intArray, intArrayClone);
        Assertions.assertEquals(intArray.length, intArrayClone.length);
        Assertions.assertEquals(intArray[0], intArrayClone[0]);
    }

    @Test
    public void nullToEmptyTest() {
        // Object
        Assertions.assertSame(ArrayUtils.nullToEmpty((Object[]) null), ArrayUtils.EMPTY_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new Object[0]), ArrayUtils.EMPTY_OBJECT_ARRAY);
        Object[] objArray = new Object[] { new Object() };
        Assertions.assertSame(ArrayUtils.nullToEmpty(objArray), objArray);

        // String
        Assertions.assertSame(ArrayUtils.nullToEmpty((String[]) null), ArrayUtils.EMPTY_STRING_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new String[0]), ArrayUtils.EMPTY_STRING_ARRAY);
        String[] strArray = new String[] { "a" };
        Assertions.assertSame(ArrayUtils.nullToEmpty(strArray), strArray);

        // long
        Assertions.assertSame(ArrayUtils.nullToEmpty((long[]) null), ArrayUtils.EMPTY_LONG_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new long[0]), ArrayUtils.EMPTY_LONG_ARRAY);
        long[] longArray = new long[] { 1L };
        Assertions.assertSame(ArrayUtils.nullToEmpty(longArray), longArray);

        // int
        Assertions.assertSame(ArrayUtils.nullToEmpty((int[]) null), ArrayUtils.EMPTY_INT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new int[0]), ArrayUtils.EMPTY_INT_ARRAY);
        int[] intArray = new int[] { 1 };
        Assertions.assertSame(ArrayUtils.nullToEmpty(intArray), intArray);

        // short
        Assertions.assertSame(ArrayUtils.nullToEmpty((short[]) null), ArrayUtils.EMPTY_SHORT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new short[0]), ArrayUtils.EMPTY_SHORT_ARRAY);
        short[] shortArray = new short[] { 1 };
        Assertions.assertSame(ArrayUtils.nullToEmpty(shortArray), shortArray);

        // char
        Assertions.assertSame(ArrayUtils.nullToEmpty((char[]) null), ArrayUtils.EMPTY_CHAR_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new char[0]), ArrayUtils.EMPTY_CHAR_ARRAY);
        char[] charArray = new char[] { 'a' };
        Assertions.assertSame(ArrayUtils.nullToEmpty(charArray), charArray);

        // byte
        Assertions.assertSame(ArrayUtils.nullToEmpty((byte[]) null), ArrayUtils.EMPTY_BYTE_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new byte[0]), ArrayUtils.EMPTY_BYTE_ARRAY);
        byte[] byteArray = new byte[] { 1 };
        Assertions.assertSame(ArrayUtils.nullToEmpty(byteArray), byteArray);

        // double
        Assertions.assertSame(ArrayUtils.nullToEmpty((double[]) null), ArrayUtils.EMPTY_DOUBLE_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new double[0]), ArrayUtils.EMPTY_DOUBLE_ARRAY);
        double[] doubleArray = new double[] { 1.0 };
        Assertions.assertSame(ArrayUtils.nullToEmpty(doubleArray), doubleArray);

        // float
        Assertions.assertSame(ArrayUtils.nullToEmpty((float[]) null), ArrayUtils.EMPTY_FLOAT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new float[0]), ArrayUtils.EMPTY_FLOAT_ARRAY);
        float[] floatArray = new float[] { 1.0f };
        Assertions.assertSame(ArrayUtils.nullToEmpty(floatArray), floatArray);

        // boolean
        Assertions.assertSame(ArrayUtils.nullToEmpty((boolean[]) null), ArrayUtils.EMPTY_BOOLEAN_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty(new boolean[0]), ArrayUtils.EMPTY_BOOLEAN_ARRAY);
        boolean[] booleanArray = new boolean[] { true };
        Assertions.assertSame(ArrayUtils.nullToEmpty(booleanArray), booleanArray);

        // Wrappers
        Assertions.assertSame(ArrayUtils.nullToEmpty((Long[]) null), ArrayUtils.EMPTY_LONG_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Integer[]) null), ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Short[]) null), ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Character[]) null), ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Byte[]) null), ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Double[]) null), ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Float[]) null), ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY);
        Assertions.assertSame(ArrayUtils.nullToEmpty((Boolean[]) null), ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY);
    }

    @Test
    public void subarrayTest() {
        // Object
        Object[] objArray = new Object[] { "a", "b", "c" };
        Assertions.assertNull(ArrayUtils.subarray((Object[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(objArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(objArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(objArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(objArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(objArray, 1, 2).length);
        Assertions.assertEquals("b", ArrayUtils.subarray(objArray, 1, 2)[0]);

        // long
        long[] longArray = new long[] { 1L, 2L, 3L };
        Assertions.assertNull(ArrayUtils.subarray((long[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(longArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(longArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(longArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(longArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(longArray, 1, 2).length);
        Assertions.assertEquals(2L, ArrayUtils.subarray(longArray, 1, 2)[0]);

        // int
        int[] intArray = new int[] { 1, 2, 3 };
        Assertions.assertNull(ArrayUtils.subarray((int[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(intArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(intArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(intArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(intArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(intArray, 1, 2).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(intArray, 1, 2)[0]);

        // short
        short[] shortArray = new short[] { 1, 2, 3 };
        Assertions.assertNull(ArrayUtils.subarray((short[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(shortArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(shortArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(shortArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(shortArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(shortArray, 1, 2).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(shortArray, 1, 2)[0]);

        // char
        char[] charArray = new char[] { 'a', 'b', 'c' };
        Assertions.assertNull(ArrayUtils.subarray((char[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(charArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(charArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(charArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(charArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(charArray, 1, 2).length);
        Assertions.assertEquals('b', ArrayUtils.subarray(charArray, 1, 2)[0]);

        // byte
        byte[] byteArray = new byte[] { 1, 2, 3 };
        Assertions.assertNull(ArrayUtils.subarray((byte[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(byteArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(byteArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(byteArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(byteArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(byteArray, 1, 2).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(byteArray, 1, 2)[0]);

        // double
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        Assertions.assertNull(ArrayUtils.subarray((double[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(doubleArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(doubleArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(doubleArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(doubleArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(doubleArray, 1, 2).length);
        Assertions.assertEquals(2.0, ArrayUtils.subarray(doubleArray, 1, 2)[0], 0.0);

        // float
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        Assertions.assertNull(ArrayUtils.subarray((float[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(floatArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(floatArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(floatArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(floatArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(floatArray, 1, 2).length);
        Assertions.assertEquals(2.0f, ArrayUtils.subarray(floatArray, 1, 2)[0], 0.0);

        // boolean
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assertions.assertNull(ArrayUtils.subarray((boolean[]) null, 0, 0));
        Assertions.assertEquals(0, ArrayUtils.subarray(booleanArray, 0, 0).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(booleanArray, 0, 3).length);
        Assertions.assertEquals(3, ArrayUtils.subarray(booleanArray, 0, 4).length);
        Assertions.assertEquals(2, ArrayUtils.subarray(booleanArray, -1, 2).length);
        Assertions.assertEquals(1, ArrayUtils.subarray(booleanArray, 1, 2).length);
        Assertions.assertFalse(ArrayUtils.subarray(booleanArray, 1, 2)[0]);
    }

    @Test
    public void isSameLengthTest() {
        // Object
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (Object[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new Object[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new Object[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new Object[0], new Object[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new Object[] { "a" }, new Object[] { "b" }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new Object[] { "a" }, new Object[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new Object[0], new Object[] { "a" }));
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new Object[] { "a" }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new Object[] { "a" }, null));

        // long
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (long[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new long[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new long[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new long[0], new long[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new long[] { 1 }, new long[] { 2 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new long[] { 1 }, new long[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new long[0], new long[] { 1 }));

        // int
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (int[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new int[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new int[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new int[0], new int[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new int[] { 1 }, new int[] { 2 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new int[] { 1 }, new int[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new int[0], new int[] { 1 }));

        // short
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (short[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new short[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new short[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new short[0], new short[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new short[] { 1 }, new short[] { 2 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new short[] { 1 }, new short[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new short[0], new short[] { 1 }));

        // char
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (char[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new char[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new char[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new char[0], new char[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new char[] { 'a' }, new char[] { 'b' }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new char[] { 'a' }, new char[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new char[0], new char[] { 'a' }));

        // byte
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (byte[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new byte[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new byte[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new byte[0], new byte[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new byte[] { 1 }, new byte[] { 2 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new byte[] { 1 }, new byte[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new byte[0], new byte[] { 1 }));

        // double
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (double[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new double[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new double[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new double[0], new double[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new double[] { 1.0 }, new double[] { 2.0 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new double[] { 1.0 }, new double[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new double[0], new double[] { 1.0 }));

        // float
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (float[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new float[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new float[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new float[0], new float[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new float[] { 1.0f }, new float[] { 2.0f }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new float[] { 1.0f }, new float[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new float[0], new float[] { 1.0f }));

        // boolean
        Assertions.assertTrue(ArrayUtils.isSameLength(null, (boolean[]) null));
        Assertions.assertTrue(ArrayUtils.isSameLength(new boolean[0], null));
        Assertions.assertTrue(ArrayUtils.isSameLength(null, new boolean[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new boolean[0], new boolean[0]));
        Assertions.assertTrue(ArrayUtils.isSameLength(new boolean[] { true }, new boolean[] { false }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new boolean[] { true }, new boolean[0]));
        Assertions.assertFalse(ArrayUtils.isSameLength(new boolean[0], new boolean[] { true }));
    }

    @Test
    public void getLengthTest() {
        Assertions.assertEquals(0, ArrayUtils.getLength(null));
        Assertions.assertEquals(0, ArrayUtils.getLength(new Object[0]));
        Assertions.assertEquals(1, ArrayUtils.getLength(new Object[] { "a" }));
        Assertions.assertEquals(2, ArrayUtils.getLength(new int[] { 1, 2 }));
        Assertions.assertEquals(1, ArrayUtils.getLength(new boolean[] { true }));

        try {
            ArrayUtils.getLength("not an array");
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void isSameTypeTest() {
        Assertions.assertTrue(ArrayUtils.isSameType(new Object[0], new Object[0]));
        Assertions.assertTrue(ArrayUtils.isSameType(new String[0], new String[0]));
        Assertions.assertFalse(ArrayUtils.isSameType(new Object[0], new String[0]));
        Assertions.assertFalse(ArrayUtils.isSameType(new int[0], new long[0]));

        try {
            ArrayUtils.isSameType(null, new Object[0]);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ArrayUtils.isSameType(new Object[0], null);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void reverseTest() {
        // Object
        Object[] objArray = new Object[] { "a", "b", "c" };
        ArrayUtils.reverse(objArray);
        Assertions.assertEquals("c", objArray[0]);
        Assertions.assertEquals("b", objArray[1]);
        Assertions.assertEquals("a", objArray[2]);
        ArrayUtils.reverse((Object[]) null); // should not throw exception

        // long
        long[] longArray = new long[] { 1L, 2L, 3L };
        ArrayUtils.reverse(longArray);
        Assertions.assertEquals(3L, longArray[0]);
        Assertions.assertEquals(2L, longArray[1]);
        Assertions.assertEquals(1L, longArray[2]);
        ArrayUtils.reverse((long[]) null);

        // int
        int[] intArray = new int[] { 1, 2, 3 };
        ArrayUtils.reverse(intArray);
        Assertions.assertEquals(3, intArray[0]);
        Assertions.assertEquals(2, intArray[1]);
        Assertions.assertEquals(1, intArray[2]);
        ArrayUtils.reverse((int[]) null);

        // short
        short[] shortArray = new short[] { 1, 2, 3 };
        ArrayUtils.reverse(shortArray);
        Assertions.assertEquals(3, shortArray[0]);
        Assertions.assertEquals(2, shortArray[1]);
        Assertions.assertEquals(1, shortArray[2]);
        ArrayUtils.reverse((short[]) null);

        // char
        char[] charArray = new char[] { 'a', 'b', 'c' };
        ArrayUtils.reverse(charArray);
        Assertions.assertEquals('c', charArray[0]);
        Assertions.assertEquals('b', charArray[1]);
        Assertions.assertEquals('a', charArray[2]);
        ArrayUtils.reverse((char[]) null);

        // byte
        byte[] byteArray = new byte[] { 1, 2, 3 };
        ArrayUtils.reverse(byteArray);
        Assertions.assertEquals(3, byteArray[0]);
        Assertions.assertEquals(2, byteArray[1]);
        Assertions.assertEquals(1, byteArray[2]);
        ArrayUtils.reverse((byte[]) null);

        // double
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        ArrayUtils.reverse(doubleArray);
        Assertions.assertEquals(3.0, doubleArray[0], 0.0);
        Assertions.assertEquals(2.0, doubleArray[1], 0.0);
        Assertions.assertEquals(1.0, doubleArray[2], 0.0);
        ArrayUtils.reverse((double[]) null);

        // float
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        ArrayUtils.reverse(floatArray);
        Assertions.assertEquals(3.0f, floatArray[0], 0.0);
        Assertions.assertEquals(2.0f, floatArray[1], 0.0);
        Assertions.assertEquals(1.0f, floatArray[2], 0.0);
        ArrayUtils.reverse((float[]) null);

        // boolean
        boolean[] booleanArray = new boolean[] { true, false, true };
        ArrayUtils.reverse(booleanArray);
        Assertions.assertTrue(booleanArray[0]);
        Assertions.assertFalse(booleanArray[1]);
        Assertions.assertTrue(booleanArray[2]);
        ArrayUtils.reverse((boolean[]) null);
    }

    @Test
    public void indexOfTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assertions.assertEquals(-1, ArrayUtils.indexOf(null, null));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(null, "0"));
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, "0"));
        Assertions.assertEquals(1, ArrayUtils.indexOf(array, "1"));
        Assertions.assertEquals(2, ArrayUtils.indexOf(array, "2"));
        Assertions.assertEquals(3, ArrayUtils.indexOf(array, "3"));
        Assertions.assertEquals(4, ArrayUtils.indexOf(array, null));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(array, "not_found"));
        Assertions.assertEquals(5, ArrayUtils.indexOf(array, "0", 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(array, "0", -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(array, "0", 10));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(longArray, 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(longArray, 1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(longArray, 99));
        Assertions.assertEquals(4, ArrayUtils.indexOf(longArray, 0, 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(longArray, 0, -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf((long[]) null, 0));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(intArray, 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(intArray, 1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(intArray, 99));
        Assertions.assertEquals(4, ArrayUtils.indexOf(intArray, 0, 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(intArray, 0, -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf((int[]) null, 0));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(shortArray, (short) 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(shortArray, (short) 1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(shortArray, (short) 99));
        Assertions.assertEquals(4, ArrayUtils.indexOf(shortArray, (short) 0, 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(shortArray, (short) 0, -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf((short[]) null, (short) 0));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertEquals(0, ArrayUtils.indexOf(charArray, 'a'));
        Assertions.assertEquals(1, ArrayUtils.indexOf(charArray, 'b'));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(charArray, 'z'));
        Assertions.assertEquals(4, ArrayUtils.indexOf(charArray, 'a', 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(charArray, 'a', -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(byteArray, (byte) 0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(byteArray, (byte) 1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(byteArray, (byte) 99));
        Assertions.assertEquals(4, ArrayUtils.indexOf(byteArray, (byte) 0, 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(byteArray, (byte) 0, -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf((byte[]) null, (byte) 0));

        // double[]
        double[] doubleArray = new double[] { 0.0, 1.1, 2.2, 3.3, 0.0 };
        Assertions.assertEquals(0, ArrayUtils.indexOf(doubleArray, 0.0));
        Assertions.assertEquals(1, ArrayUtils.indexOf(doubleArray, 1.1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(doubleArray, 9.9));
        Assertions.assertEquals(4, ArrayUtils.indexOf(doubleArray, 0.0, 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(doubleArray, 0.0, -1));
        assert ArrayUtils.indexOf(doubleArray, 1.1, 0.1) == 1; // tolerance
        assert ArrayUtils.indexOf(doubleArray, 1.1, 2, 0.1) == -1; // tolerance with start index
        Assertions.assertEquals(-1, ArrayUtils.indexOf(null, 0.0));

        // float[]
        float[] floatArray = new float[] { 0.0f, 1.1f, 2.2f, 3.3f, 0.0f };
        Assertions.assertEquals(0, ArrayUtils.indexOf(floatArray, 0.0f));
        Assertions.assertEquals(1, ArrayUtils.indexOf(floatArray, 1.1f));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(floatArray, 9.9f));
        Assertions.assertEquals(4, ArrayUtils.indexOf(floatArray, 0.0f, 2));
        Assertions.assertEquals(0, ArrayUtils.indexOf(floatArray, 0.0f, -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf((float[]) null, 0.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assertions.assertEquals(0, ArrayUtils.indexOf(booleanArray, true));
        Assertions.assertEquals(1, ArrayUtils.indexOf(booleanArray, false));
        Assertions.assertEquals(2, ArrayUtils.indexOf(booleanArray, true, 1));
        Assertions.assertEquals(0, ArrayUtils.indexOf(booleanArray, true, -1));
        Assertions.assertEquals(-1, ArrayUtils.indexOf(null, true));
    }

    @Test
    public void lastIndexOfTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(null, null));
        Assertions.assertEquals(5, ArrayUtils.lastIndexOf(array, "0"));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, "1"));
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(array, null));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(array, "not_found"));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(array, "0", 4));
        Assertions.assertEquals(5, ArrayUtils.lastIndexOf(array, "0", 10));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(array, "0", -1));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(longArray, 0));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(longArray, 0, 3));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf((long[]) null, 0));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(intArray, 0));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(intArray, 0, 3));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf((int[]) null, 0));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(shortArray, (short) 0));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(shortArray, (short) 0, 3));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf((short[]) null, (short) 0));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(charArray, 'a'));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(charArray, 'a', 3));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(byteArray, (byte) 0));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(byteArray, (byte) 0, 3));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf((byte[]) null, (byte) 0));

        // double[]
        double[] doubleArray = new double[] { 0.0, 1.1, 2.2, 3.3, 0.0 };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(doubleArray, 0.0));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(doubleArray, 0.0, 3));
        assert ArrayUtils.lastIndexOf(doubleArray, 1.1, 0.1) == 1; // tolerance
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(null, 0.0));

        // float[]
        float[] floatArray = new float[] { 0.0f, 1.1f, 2.2f, 3.3f, 0.0f };
        Assertions.assertEquals(4, ArrayUtils.lastIndexOf(floatArray, 0.0f));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(floatArray, 0.0f, 3));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf((float[]) null, 0.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assertions.assertEquals(2, ArrayUtils.lastIndexOf(booleanArray, true));
        Assertions.assertEquals(0, ArrayUtils.lastIndexOf(booleanArray, true, 1));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(null, true));
    }

    @Test
    public void containsTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assertions.assertTrue(ArrayUtils.contains(array, "0"));
        Assertions.assertFalse(ArrayUtils.contains(array, "not_found"));
        Assertions.assertTrue(ArrayUtils.contains(array, null));
        Assertions.assertFalse(ArrayUtils.contains(null, "0"));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(longArray, 1));
        Assertions.assertFalse(ArrayUtils.contains(longArray, 99));
        Assertions.assertFalse(ArrayUtils.contains((long[]) null, 1));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(intArray, 1));
        Assertions.assertFalse(ArrayUtils.contains(intArray, 99));
        Assertions.assertFalse(ArrayUtils.contains((int[]) null, 1));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(shortArray, (short) 1));
        Assertions.assertFalse(ArrayUtils.contains(shortArray, (short) 99));
        Assertions.assertFalse(ArrayUtils.contains((short[]) null, (short) 1));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assertions.assertTrue(ArrayUtils.contains(charArray, 'b'));
        Assertions.assertFalse(ArrayUtils.contains(charArray, 'z'));
        Assertions.assertFalse(ArrayUtils.contains((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assertions.assertTrue(ArrayUtils.contains(byteArray, (byte) 1));
        Assertions.assertFalse(ArrayUtils.contains(byteArray, (byte) 99));
        Assertions.assertFalse(ArrayUtils.contains((byte[]) null, (byte) 1));

        // double[]
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        Assertions.assertTrue(ArrayUtils.contains(doubleArray, 1.0));
        Assertions.assertFalse(ArrayUtils.contains(doubleArray, 9.9));
        Assertions.assertTrue(ArrayUtils.contains(doubleArray, 1.1, 0.2));
        Assertions.assertFalse(ArrayUtils.contains(doubleArray, 1.5, 0.1));
        Assertions.assertFalse(ArrayUtils.contains(null, 1.0));

        // float[]
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        Assertions.assertTrue(ArrayUtils.contains(floatArray, 1.0f));
        Assertions.assertFalse(ArrayUtils.contains(floatArray, 9.9f));
        Assertions.assertFalse(ArrayUtils.contains((float[]) null, 1.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false };
        Assertions.assertTrue(ArrayUtils.contains(booleanArray, true));
        Assertions.assertTrue(ArrayUtils.contains(booleanArray, false));
        Assertions.assertFalse(ArrayUtils.contains(new boolean[] { true }, false));
        Assertions.assertFalse(ArrayUtils.contains(null, true));
    }

    @Test
    public void toPrimitiveTest() {
        // Integer
        Assertions.assertNull(ArrayUtils.toPrimitive((Integer[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Integer[0]).length);
        Assertions.assertEquals(2, ArrayUtils.toPrimitive(new Integer[] { 1, 2 }).length);
        Assertions.assertEquals(1, ArrayUtils.toPrimitive(new Integer[] { 1, 2 })[0]);
        try {
            ArrayUtils.toPrimitive(new Integer[] { null });
            Assertions.fail();
        } catch (NullPointerException e) {
            Assertions.assertTrue(true);
        }
        Assertions.assertEquals(99, ArrayUtils.toPrimitive(new Integer[] { null }, 99)[0]);

        // Long
        Assertions.assertNull(ArrayUtils.toPrimitive((Long[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Long[0]).length);
        Assertions.assertEquals(1L, ArrayUtils.toPrimitive(new Long[] { 1L, 2L })[0]);
        Assertions.assertEquals(99L, ArrayUtils.toPrimitive(new Long[] { null }, 99L)[0]);

        // Short
        Assertions.assertNull(ArrayUtils.toPrimitive((Short[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Short[0]).length);
        Assertions.assertEquals(ArrayUtils.toPrimitive(new Short[] { (short) 1, (short) 2 })[0], (short) 1);
        Assertions.assertEquals(ArrayUtils.toPrimitive(new Short[] { null }, (short) 99)[0], (short) 99);

        // Byte
        Assertions.assertNull(ArrayUtils.toPrimitive((Byte[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Byte[0]).length);
        Assertions.assertEquals(ArrayUtils.toPrimitive(new Byte[] { (byte) 1, (byte) 2 })[0], (byte) 1);
        Assertions.assertEquals(ArrayUtils.toPrimitive(new Byte[] { null }, (byte) 99)[0], (byte) 99);

        // Double
        Assertions.assertNull(ArrayUtils.toPrimitive((Double[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Double[0]).length);
        Assertions.assertEquals(1.1, ArrayUtils.toPrimitive(new Double[] { 1.1, 2.2 })[0], 0.0);
        Assertions.assertEquals(99.9, ArrayUtils.toPrimitive(new Double[] { null }, 99.9)[0], 0.0);

        // Float
        Assertions.assertNull(ArrayUtils.toPrimitive((Float[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Float[0]).length);
        Assertions.assertEquals(1.1f, ArrayUtils.toPrimitive(new Float[] { 1.1f, 2.2f })[0], 0.0);
        Assertions.assertEquals(99.9f, ArrayUtils.toPrimitive(new Float[] { null }, 99.9f)[0], 0.0);

        // Boolean
        Assertions.assertNull(ArrayUtils.toPrimitive((Boolean[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Boolean[0]).length);
        Assertions.assertTrue(ArrayUtils.toPrimitive(new Boolean[] { true, false })[0]);
        Assertions.assertFalse(ArrayUtils.toPrimitive(new Boolean[] { null }, false)[0]);

        // Character
        Assertions.assertNull(ArrayUtils.toPrimitive((Character[]) null));
        Assertions.assertEquals(0, ArrayUtils.toPrimitive(new Character[0]).length);
        Assertions.assertEquals('a', ArrayUtils.toPrimitive(new Character[] { 'a', 'b' })[0]);
        Assertions.assertEquals('z', ArrayUtils.toPrimitive(new Character[] { null }, 'z')[0]);
    }

    @Test
    public void toObjectTest() {
        // int
        Assertions.assertNull(ArrayUtils.toObject((int[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new int[0]).length);
        Assertions.assertEquals(2, ArrayUtils.toObject(new int[] { 1, 2 }).length);
        Assertions.assertEquals(1, (int) ArrayUtils.toObject(new int[] { 1, 2 })[0]);

        // long
        Assertions.assertNull(ArrayUtils.toObject((long[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new long[0]).length);
        Assertions.assertEquals(1L, (long) ArrayUtils.toObject(new long[] { 1L, 2L })[0]);

        // short
        Assertions.assertNull(ArrayUtils.toObject((short[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new short[0]).length);
        Assertions.assertEquals((short) ArrayUtils.toObject(new short[] { (short) 1, (short) 2 })[0], (short) 1);

        // byte
        Assertions.assertNull(ArrayUtils.toObject((byte[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new byte[0]).length);
        Assertions.assertEquals((byte) ArrayUtils.toObject(new byte[] { (byte) 1, (byte) 2 })[0], (byte) 1);

        // double
        Assertions.assertNull(ArrayUtils.toObject((double[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new double[0]).length);
        Assertions.assertEquals(1.1, ArrayUtils.toObject(new double[] { 1.1, 2.2 })[0], 0.0);

        // float
        Assertions.assertNull(ArrayUtils.toObject((float[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new float[0]).length);
        Assertions.assertEquals(1.1f, ArrayUtils.toObject(new float[] { 1.1f, 2.2f })[0], 0.0);

        // boolean
        Assertions.assertNull(ArrayUtils.toObject((boolean[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new boolean[0]).length);
        Assertions.assertEquals(true, (boolean) ArrayUtils.toObject(new boolean[] { true, false })[0]);

        // char
        Assertions.assertNull(ArrayUtils.toObject((char[]) null));
        Assertions.assertEquals(0, ArrayUtils.toObject(new char[0]).length);
        Assertions.assertEquals('a', (char) ArrayUtils.toObject(new char[] { 'a', 'b' })[0]);
    }

    @Test
    public void safeToObjectTest() {
        Assertions.assertNull(ArrayUtils.safeToObject(null));
        Assertions.assertEquals(1, ArrayUtils.safeToObject("string").length);
        Assertions.assertEquals("string", ArrayUtils.safeToObject("string")[0]);

        // arrays
        Assertions.assertEquals(2, ArrayUtils.safeToObject(new int[] { 1, 2 }).length);
        Assertions.assertEquals(2, ArrayUtils.safeToObject(new Integer[] { 1, 2 }).length);
        Assertions.assertEquals(1, ArrayUtils.safeToObject(new char[] { 'a' }).length);

        // iterable
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("a");
        list.add("b");
        Assertions.assertEquals(2, ArrayUtils.safeToObject(list).length);
    }

    @Test
    public void isEmptyTest() {
        Assertions.assertTrue(ArrayUtils.isEmpty((Object[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new Object[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new Object[] { "" }));
        Assertions.assertTrue(ArrayUtils.isEmpty((long[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new long[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new long[] { 1 }));
        Assertions.assertTrue(ArrayUtils.isEmpty((int[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new int[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new int[] { 1 }));
        Assertions.assertTrue(ArrayUtils.isEmpty((short[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new short[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new short[] { 1 }));
        Assertions.assertTrue(ArrayUtils.isEmpty((char[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new char[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new char[] { 'a' }));
        Assertions.assertTrue(ArrayUtils.isEmpty((byte[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new byte[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new byte[] { 1 }));
        Assertions.assertTrue(ArrayUtils.isEmpty((double[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new double[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new double[] { 1.1 }));
        Assertions.assertTrue(ArrayUtils.isEmpty((float[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new float[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new float[] { 1.1f }));
        Assertions.assertTrue(ArrayUtils.isEmpty((boolean[]) null));
        Assertions.assertTrue(ArrayUtils.isEmpty(new boolean[0]));
        Assertions.assertFalse(ArrayUtils.isEmpty(new boolean[] { true }));
    }

    @Test
    public void isNotEmptyTest() {
        Assertions.assertFalse(ArrayUtils.isNotEmpty((Object[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new Object[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new Object[] { "" }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((long[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new long[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new long[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((int[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new int[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new int[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((short[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new short[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new short[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((char[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new char[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new char[] { 'a' }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((byte[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new byte[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new byte[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((double[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new double[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new double[] { 1.1 }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((float[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new float[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new float[] { 1.1f }));
        Assertions.assertFalse(ArrayUtils.isNotEmpty((boolean[]) null));
        Assertions.assertFalse(ArrayUtils.isNotEmpty(new boolean[0]));
        Assertions.assertTrue(ArrayUtils.isNotEmpty(new boolean[] { true }));
    }

    @Test
    public void addAllTest() {
        Assertions.assertNull(ArrayUtils.addAll(null, (Object[]) null));
        Assertions.assertEquals(1, ArrayUtils.addAll(null, new String[] { "a" }).length);
        Assertions.assertEquals(1, ArrayUtils.addAll(new String[] { "a" }, null).length);
        Assertions.assertEquals(2, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b" }).length);
        Assertions.assertEquals(3, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b", "c" }).length);

        // primitives
        Assertions.assertNull(ArrayUtils.addAll(null, (int[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new int[] { 1 }, new int[] { 2 }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (boolean[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new boolean[] { true }, new boolean[] { false }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (char[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new char[] { 'a' }, new char[] { 'b' }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (byte[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new byte[] { 1 }, new byte[] { 2 }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (short[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new short[] { 1 }, new short[] { 2 }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (long[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new long[] { 1 }, new long[] { 2 }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (float[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new float[] { 1 }, new float[] { 2 }).length);
        Assertions.assertNull(ArrayUtils.addAll(null, (double[]) null));
        Assertions.assertEquals(2, ArrayUtils.addAll(new double[] { 1 }, new double[] { 2 }).length);
    }

    @Test
    public void addTest() {
        // Object
        String[] newArray = ArrayUtils.add(null, "a");
        Assertions.assertEquals(1, newArray.length);
        Assertions.assertEquals("a", newArray[0]);
        newArray = ArrayUtils.add(newArray, "b");
        Assertions.assertEquals(2, newArray.length);
        Assertions.assertEquals("b", newArray[1]);

        // int
        int[] newIntArray = ArrayUtils.add((int[]) null, 1);
        Assertions.assertEquals(1, newIntArray.length);
        Assertions.assertEquals(1, newIntArray[0]);
        newIntArray = ArrayUtils.add(newIntArray, 2);
        Assertions.assertEquals(2, newIntArray.length);
        Assertions.assertEquals(2, newIntArray[1]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.add(null, true);
        Assertions.assertEquals(1, newBooleanArray.length);
        Assertions.assertTrue(newBooleanArray[0]);
        newBooleanArray = ArrayUtils.add(newBooleanArray, false);
        Assertions.assertEquals(2, newBooleanArray.length);
        Assertions.assertFalse(newBooleanArray[1]);

        // char
        char[] newCharArray = ArrayUtils.add((char[]) null, 'a');
        Assertions.assertEquals(1, newCharArray.length);
        Assertions.assertEquals('a', newCharArray[0]);
        newCharArray = ArrayUtils.add(newCharArray, 'b');
        Assertions.assertEquals(2, newCharArray.length);
        Assertions.assertEquals('b', newCharArray[1]);

        // byte
        byte[] newByteArray = ArrayUtils.add((byte[]) null, (byte) 1);
        Assertions.assertEquals(1, newByteArray.length);
        Assertions.assertEquals(1, newByteArray[0]);
        newByteArray = ArrayUtils.add(newByteArray, (byte) 2);
        Assertions.assertEquals(2, newByteArray.length);
        Assertions.assertEquals(2, newByteArray[1]);

        // short
        short[] newShortArray = ArrayUtils.add((short[]) null, (short) 1);
        Assertions.assertEquals(1, newShortArray.length);
        Assertions.assertEquals(1, newShortArray[0]);
        newShortArray = ArrayUtils.add(newShortArray, (short) 2);
        Assertions.assertEquals(2, newShortArray.length);
        Assertions.assertEquals(2, newShortArray[1]);

        // long
        long[] newLongArray = ArrayUtils.add((long[]) null, 1L);
        Assertions.assertEquals(1, newLongArray.length);
        Assertions.assertEquals(1L, newLongArray[0]);
        newLongArray = ArrayUtils.add(newLongArray, 2L);
        Assertions.assertEquals(2, newLongArray.length);
        Assertions.assertEquals(2L, newLongArray[1]);

        // float
        float[] newFloatArray = ArrayUtils.add((float[]) null, 1.1f);
        Assertions.assertEquals(1, newFloatArray.length);
        Assertions.assertEquals(1.1f, newFloatArray[0], 0.0);
        newFloatArray = ArrayUtils.add(newFloatArray, 2.2f);
        Assertions.assertEquals(2, newFloatArray.length);
        Assertions.assertEquals(2.2f, newFloatArray[1], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.add(null, 1.1);
        Assertions.assertEquals(1, newDoubleArray.length);
        Assertions.assertEquals(1.1, newDoubleArray[0], 0.0);
        newDoubleArray = ArrayUtils.add(newDoubleArray, 2.2);
        Assertions.assertEquals(2, newDoubleArray.length);
        Assertions.assertEquals(2.2, newDoubleArray[1], 0.0);
    }

    @Test
    public void insertTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.add(null, 0, "a");
        Assertions.assertEquals(1, newArray.length);
        Assertions.assertEquals("a", newArray[0]);
        newArray = (String[]) ArrayUtils.add(newArray, 0, "b");
        Assertions.assertEquals(2, newArray.length);
        Assertions.assertEquals("b", newArray[0]);
        Assertions.assertEquals("a", newArray[1]);

        // int
        int[] newIntArray = ArrayUtils.add((int[]) null, 0, 1);
        Assertions.assertEquals(1, newIntArray.length);
        Assertions.assertEquals(1, newIntArray[0]);
        newIntArray = ArrayUtils.add(newIntArray, 0, 2);
        Assertions.assertEquals(2, newIntArray.length);
        Assertions.assertEquals(2, newIntArray[0]);
        Assertions.assertEquals(1, newIntArray[1]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.add(null, 0, true);
        Assertions.assertEquals(1, newBooleanArray.length);
        Assertions.assertTrue(newBooleanArray[0]);
        newBooleanArray = ArrayUtils.add(newBooleanArray, 0, false);
        Assertions.assertEquals(2, newBooleanArray.length);
        Assertions.assertFalse(newBooleanArray[0]);
        Assertions.assertTrue(newBooleanArray[1]);

        // char
        char[] newCharArray = ArrayUtils.add((char[]) null, 0, 'a');
        Assertions.assertEquals(1, newCharArray.length);
        Assertions.assertEquals('a', newCharArray[0]);
        newCharArray = ArrayUtils.add(newCharArray, 0, 'b');
        Assertions.assertEquals(2, newCharArray.length);
        Assertions.assertEquals('b', newCharArray[0]);
        Assertions.assertEquals('a', newCharArray[1]);

        // byte
        byte[] newByteArray = ArrayUtils.add((byte[]) null, 0, (byte) 1);
        Assertions.assertEquals(1, newByteArray.length);
        Assertions.assertEquals(1, newByteArray[0]);
        newByteArray = ArrayUtils.add(newByteArray, 0, (byte) 2);
        Assertions.assertEquals(2, newByteArray.length);
        Assertions.assertEquals(2, newByteArray[0]);
        Assertions.assertEquals(1, newByteArray[1]);

        // short
        short[] newShortArray = ArrayUtils.add((short[]) null, 0, (short) 1);
        Assertions.assertEquals(1, newShortArray.length);
        Assertions.assertEquals(1, newShortArray[0]);
        newShortArray = ArrayUtils.add(newShortArray, 0, (short) 2);
        Assertions.assertEquals(2, newShortArray.length);
        Assertions.assertEquals(2, newShortArray[0]);
        Assertions.assertEquals(1, newShortArray[1]);

        // long
        long[] newLongArray = ArrayUtils.add((long[]) null, 0, 1L);
        Assertions.assertEquals(1, newLongArray.length);
        Assertions.assertEquals(1L, newLongArray[0]);
        newLongArray = ArrayUtils.add(newLongArray, 0, 2L);
        Assertions.assertEquals(2, newLongArray.length);
        Assertions.assertEquals(2L, newLongArray[0]);
        Assertions.assertEquals(1L, newLongArray[1]);

        // float
        float[] newFloatArray = ArrayUtils.add((float[]) null, 0, 1.1f);
        Assertions.assertEquals(1, newFloatArray.length);
        Assertions.assertEquals(1.1f, newFloatArray[0], 0.0);
        newFloatArray = ArrayUtils.add(newFloatArray, 0, 2.2f);
        Assertions.assertEquals(2, newFloatArray.length);
        Assertions.assertEquals(2.2f, newFloatArray[0], 0.0);
        Assertions.assertEquals(1.1f, newFloatArray[1], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.add(null, 0, 1.1);
        Assertions.assertEquals(1, newDoubleArray.length);
        Assertions.assertEquals(1.1, newDoubleArray[0], 0.0);
        newDoubleArray = ArrayUtils.add(newDoubleArray, 0, 2.2);
        Assertions.assertEquals(2, newDoubleArray.length);
        Assertions.assertEquals(2.2, newDoubleArray[0], 0.0);
        Assertions.assertEquals(1.1, newDoubleArray[1], 0.0);
    }

    @Test
    public void removeTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.remove(new String[] { "a", "b" }, 0);
        Assertions.assertEquals(1, newArray.length);
        Assertions.assertEquals("b", newArray[0]);

        // int
        int[] newIntArray = ArrayUtils.remove(new int[] { 1, 2 }, 0);
        Assertions.assertEquals(1, newIntArray.length);
        Assertions.assertEquals(2, newIntArray[0]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.remove(new boolean[] { true, false }, 0);
        Assertions.assertEquals(1, newBooleanArray.length);
        Assertions.assertFalse(newBooleanArray[0]);

        // char
        char[] newCharArray = ArrayUtils.remove(new char[] { 'a', 'b' }, 0);
        Assertions.assertEquals(1, newCharArray.length);
        Assertions.assertEquals('b', newCharArray[0]);

        // byte
        byte[] newByteArray = ArrayUtils.remove(new byte[] { 1, 2 }, 0);
        Assertions.assertEquals(1, newByteArray.length);
        Assertions.assertEquals(2, newByteArray[0]);

        // short
        short[] newShortArray = ArrayUtils.remove(new short[] { 1, 2 }, 0);
        Assertions.assertEquals(1, newShortArray.length);
        Assertions.assertEquals(2, newShortArray[0]);

        // long
        long[] newLongArray = ArrayUtils.remove(new long[] { 1L, 2L }, 0);
        Assertions.assertEquals(1, newLongArray.length);
        Assertions.assertEquals(2L, newLongArray[0]);

        // float
        float[] newFloatArray = ArrayUtils.remove(new float[] { 1.1f, 2.2f }, 0);
        Assertions.assertEquals(1, newFloatArray.length);
        Assertions.assertEquals(2.2f, newFloatArray[0], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.remove(new double[] { 1.1, 2.2 }, 0);
        Assertions.assertEquals(1, newDoubleArray.length);
        Assertions.assertEquals(2.2, newDoubleArray[0], 0.0);
    }

    @Test
    public void removeElementTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.removeElement(new String[] { "a", "b" }, "a");
        Assertions.assertEquals(1, newArray.length);
        Assertions.assertEquals("b", newArray[0]);

        // int
        int[] newIntArray = ArrayUtils.removeElement(new int[] { 1, 2 }, 1);
        Assertions.assertEquals(1, newIntArray.length);
        Assertions.assertEquals(2, newIntArray[0]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.removeElement(new boolean[] { true, false }, true);
        Assertions.assertEquals(1, newBooleanArray.length);
        Assertions.assertFalse(newBooleanArray[0]);

        // char
        char[] newCharArray = ArrayUtils.removeElement(new char[] { 'a', 'b' }, 'a');
        Assertions.assertEquals(1, newCharArray.length);
        Assertions.assertEquals('b', newCharArray[0]);

        // byte
        byte[] newByteArray = ArrayUtils.removeElement(new byte[] { 1, 2 }, (byte) 1);
        Assertions.assertEquals(1, newByteArray.length);
        Assertions.assertEquals(2, newByteArray[0]);

        // short
        short[] newShortArray = ArrayUtils.removeElement(new short[] { 1, 2 }, (short) 1);
        Assertions.assertEquals(1, newShortArray.length);
        Assertions.assertEquals(2, newShortArray[0]);

        // long
        long[] newLongArray = ArrayUtils.removeElement(new long[] { 1L, 2L }, 1L);
        Assertions.assertEquals(1, newLongArray.length);
        Assertions.assertEquals(2L, newLongArray[0]);

        // float
        float[] newFloatArray = ArrayUtils.removeElement(new float[] { 1.1f, 2.2f }, 1.1f);
        Assertions.assertEquals(1, newFloatArray.length);
        Assertions.assertEquals(2.2f, newFloatArray[0], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.removeElement(new double[] { 1.1, 2.2 }, 1.1);
        Assertions.assertEquals(1, newDoubleArray.length);
        Assertions.assertEquals(2.2, newDoubleArray[0], 0.0);
    }

    @Test
    public void clearNullTest() {
        Assertions.assertEquals(0, ArrayUtils.clearNull(null).length);
        Assertions.assertEquals(0, ArrayUtils.clearNull(new Object[0]).length);
        Assertions.assertEquals(0, ArrayUtils.clearNull(new Object[] { null }).length);
        Object[] result = ArrayUtils.clearNull(new Object[] { "a", null, "b" });
        Assertions.assertEquals(2, result.length);
        Assertions.assertEquals("a", result[0]);
        Assertions.assertEquals("b", result[1]);
    }

    @Test
    public void toMapCoverageTest() {
        // Test with Map.Entry
        Map.Entry<String, String> entry1 = new AbstractMap.SimpleEntry<String, String>("key1", "value1");
        Map.Entry<String, String> entry2 = new AbstractMap.SimpleEntry<String, String>("key2", "value2");
        Object[] array = new Object[] { entry1, entry2 };
        Map map = ArrayUtils.toMap(array);
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("value1", map.get("key1"));
        Assertions.assertEquals("value2", map.get("key2"));

        // Test with Object[] of length >= 2
        Object[] item1 = new Object[] { "k1", "v1" };
        Object[] item2 = new Object[] { "k2", "v2", "extra" }; // extra should be ignored
        Object[] array2 = new Object[] { item1, item2 };
        Map map2 = ArrayUtils.toMap(array2);
        Assertions.assertEquals(2, map2.size());
        Assertions.assertEquals("v1", map2.get("k1"));
        Assertions.assertEquals("v2", map2.get("k2"));
    }

    @Test
    public void addAllIncompatibleTypesTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.addAll(new String[] { "a" }, new Integer[] { 1 }));
    }

    @Test
    public void addNullArrayInvalidIndexTest() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.add(null, 1, "a"));
    }

    @Test
    public void removeInvalidIndexTest() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.remove(new String[] { "a" }, 1));
    }

    @Test
    public void removeNegativeIndexTest() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.remove(new String[] { "a" }, -1));
    }

    @Test
    public void isSameLengthCoverageTest() {
        // long
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new long[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new long[] { 1 }, null));

        // int
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new int[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new int[] { 1 }, null));

        // short
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new short[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new short[] { 1 }, null));

        // char
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new char[] { 'a' }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new char[] { 'a' }, null));

        // byte
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new byte[] { 1 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new byte[] { 1 }, null));

        // double
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new double[] { 1.0 }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new double[] { 1.0 }, null));

        // float
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new float[] { 1.0f }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new float[] { 1.0f }, null));

        // boolean
        Assertions.assertFalse(ArrayUtils.isSameLength(null, new boolean[] { true }));
        Assertions.assertFalse(ArrayUtils.isSameLength(new boolean[] { true }, null));
    }

    @Test
    public void subarrayCoverageTest() {
        // Object[]
        Assertions.assertNull(ArrayUtils.subarray((Object[]) null, 0, 0));
        Assertions.assertArrayEquals(new Object[] {}, ArrayUtils.subarray(new Object[] { "a", "b" }, 0, 0));
        Assertions.assertArrayEquals(new Object[] { "a" }, ArrayUtils.subarray(new Object[] { "a", "b" }, 0, 1));
        Assertions.assertArrayEquals(new Object[] { "b" }, ArrayUtils.subarray(new Object[] { "a", "b" }, 1, 2));
        Assertions.assertArrayEquals(new Object[] { "a", "b" }, ArrayUtils.subarray(new Object[] { "a", "b" }, 0, 2));
        Assertions.assertArrayEquals(new Object[] { "a", "b" }, ArrayUtils.subarray(new Object[] { "a", "b" }, -1, 3));
        Assertions.assertArrayEquals(new Object[] {}, ArrayUtils.subarray(new Object[] { "a", "b" }, 2, 1));

        // long[]
        Assertions.assertNull(ArrayUtils.subarray((long[]) null, 0, 0));
        Assertions.assertArrayEquals(new long[] {}, ArrayUtils.subarray(new long[] { 1, 2 }, 0, 0));
        Assertions.assertArrayEquals(new long[] { 1 }, ArrayUtils.subarray(new long[] { 1, 2 }, 0, 1));
        Assertions.assertArrayEquals(new long[] { 2 }, ArrayUtils.subarray(new long[] { 1, 2 }, 1, 2));
        Assertions.assertArrayEquals(new long[] { 1, 2 }, ArrayUtils.subarray(new long[] { 1, 2 }, 0, 2));
        Assertions.assertArrayEquals(new long[] { 1, 2 }, ArrayUtils.subarray(new long[] { 1, 2 }, -1, 3));
        Assertions.assertArrayEquals(new long[] {}, ArrayUtils.subarray(new long[] { 1, 2 }, 2, 1));

        // int[]
        Assertions.assertNull(ArrayUtils.subarray((int[]) null, 0, 0));
        Assertions.assertArrayEquals(new int[] {}, ArrayUtils.subarray(new int[] { 1, 2 }, 0, 0));
        Assertions.assertArrayEquals(new int[] { 1 }, ArrayUtils.subarray(new int[] { 1, 2 }, 0, 1));
        Assertions.assertArrayEquals(new int[] { 2 }, ArrayUtils.subarray(new int[] { 1, 2 }, 1, 2));
        Assertions.assertArrayEquals(new int[] { 1, 2 }, ArrayUtils.subarray(new int[] { 1, 2 }, 0, 2));
        Assertions.assertArrayEquals(new int[] { 1, 2 }, ArrayUtils.subarray(new int[] { 1, 2 }, -1, 3));
        Assertions.assertArrayEquals(new int[] {}, ArrayUtils.subarray(new int[] { 1, 2 }, 2, 1));

        // short[]
        Assertions.assertNull(ArrayUtils.subarray((short[]) null, 0, 0));
        Assertions.assertArrayEquals(new short[] {}, ArrayUtils.subarray(new short[] { 1, 2 }, 0, 0));
        Assertions.assertArrayEquals(new short[] { 1 }, ArrayUtils.subarray(new short[] { 1, 2 }, 0, 1));
        Assertions.assertArrayEquals(new short[] { 2 }, ArrayUtils.subarray(new short[] { 1, 2 }, 1, 2));
        Assertions.assertArrayEquals(new short[] { 1, 2 }, ArrayUtils.subarray(new short[] { 1, 2 }, 0, 2));
        Assertions.assertArrayEquals(new short[] { 1, 2 }, ArrayUtils.subarray(new short[] { 1, 2 }, -1, 3));
        Assertions.assertArrayEquals(new short[] {}, ArrayUtils.subarray(new short[] { 1, 2 }, 2, 1));

        // char[]
        Assertions.assertNull(ArrayUtils.subarray((char[]) null, 0, 0));
        Assertions.assertArrayEquals(new char[] {}, ArrayUtils.subarray(new char[] { 'a', 'b' }, 0, 0));
        Assertions.assertArrayEquals(new char[] { 'a' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, 0, 1));
        Assertions.assertArrayEquals(new char[] { 'b' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, 1, 2));
        Assertions.assertArrayEquals(new char[] { 'a', 'b' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, 0, 2));
        Assertions.assertArrayEquals(new char[] { 'a', 'b' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, -1, 3));
        Assertions.assertArrayEquals(new char[] {}, ArrayUtils.subarray(new char[] { 'a', 'b' }, 2, 1));

        // byte[]
        Assertions.assertNull(ArrayUtils.subarray((byte[]) null, 0, 0));
        Assertions.assertArrayEquals(new byte[] {}, ArrayUtils.subarray(new byte[] { 1, 2 }, 0, 0));
        Assertions.assertArrayEquals(new byte[] { 1 }, ArrayUtils.subarray(new byte[] { 1, 2 }, 0, 1));
        Assertions.assertArrayEquals(new byte[] { 2 }, ArrayUtils.subarray(new byte[] { 1, 2 }, 1, 2));
        Assertions.assertArrayEquals(new byte[] { 1, 2 }, ArrayUtils.subarray(new byte[] { 1, 2 }, 0, 2));
        Assertions.assertArrayEquals(new byte[] { 1, 2 }, ArrayUtils.subarray(new byte[] { 1, 2 }, -1, 3));
        Assertions.assertArrayEquals(new byte[] {}, ArrayUtils.subarray(new byte[] { 1, 2 }, 2, 1));

        // double[]
        Assertions.assertNull(ArrayUtils.subarray((double[]) null, 0, 0));
        Assertions.assertArrayEquals(new double[] {}, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 0, 0), 0.0);
        Assertions.assertArrayEquals(new double[] { 1.1 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 0, 1), 0.0);
        Assertions.assertArrayEquals(new double[] { 2.2 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 1, 2), 0.0);
        Assertions.assertArrayEquals(new double[] { 1.1, 2.2 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 0, 2), 0.0);
        Assertions.assertArrayEquals(new double[] { 1.1, 2.2 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, -1, 3), 0.0);
        Assertions.assertArrayEquals(new double[] {}, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 2, 1), 0.0);

        // float[]
        Assertions.assertNull(ArrayUtils.subarray((float[]) null, 0, 0));
        Assertions.assertArrayEquals(new float[] {}, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 0, 0), 0.0f);
        Assertions.assertArrayEquals(new float[] { 1.1f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 0, 1), 0.0f);
        Assertions.assertArrayEquals(new float[] { 2.2f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 1, 2), 0.0f);
        Assertions.assertArrayEquals(new float[] { 1.1f, 2.2f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 0, 2), 0.0f);
        Assertions.assertArrayEquals(new float[] { 1.1f, 2.2f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, -1, 3), 0.0f);
        Assertions.assertArrayEquals(new float[] {}, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 2, 1), 0.0f);

        // boolean[]
        Assertions.assertNull(ArrayUtils.subarray((boolean[]) null, 0, 0));
        Assertions.assertArrayEquals(new boolean[] {}, ArrayUtils.subarray(new boolean[] { true, false }, 0, 0));
        Assertions.assertArrayEquals(new boolean[] { true }, ArrayUtils.subarray(new boolean[] { true, false }, 0, 1));
        Assertions.assertArrayEquals(new boolean[] { false }, ArrayUtils.subarray(new boolean[] { true, false }, 1, 2));
        Assertions.assertArrayEquals(new boolean[] { true, false }, ArrayUtils.subarray(new boolean[] { true, false }, 0, 2));
        Assertions.assertArrayEquals(new boolean[] { true, false }, ArrayUtils.subarray(new boolean[] { true, false }, -1, 3));
        Assertions.assertArrayEquals(new boolean[] {}, ArrayUtils.subarray(new boolean[] { true, false }, 2, 1));
    }

    @Test
    public void removeElementMissingTest() {
        // Object
        String[] strArray = new String[] { "a" };
        String[] newStrArray = (String[]) ArrayUtils.removeElement(strArray, "b");
        Assertions.assertArrayEquals(strArray, newStrArray);
        Assertions.assertNotSame(strArray, newStrArray); // Should be a clone

        // long
        long[] longArray = new long[] { 1L };
        long[] newLongArray = ArrayUtils.removeElement(longArray, 2L);
        Assertions.assertArrayEquals(longArray, newLongArray);
        Assertions.assertNotSame(longArray, newLongArray);

        // int
        int[] intArray = new int[] { 1 };
        int[] newIntArray = ArrayUtils.removeElement(intArray, 2);
        Assertions.assertArrayEquals(intArray, newIntArray);
        Assertions.assertNotSame(intArray, newIntArray);

        // short
        short[] shortArray = new short[] { 1 };
        short[] newShortArray = ArrayUtils.removeElement(shortArray, (short) 2);
        Assertions.assertArrayEquals(shortArray, newShortArray);
        Assertions.assertNotSame(shortArray, newShortArray);

        // char
        char[] charArray = new char[] { 'a' };
        char[] newCharArray = ArrayUtils.removeElement(charArray, 'b');
        Assertions.assertArrayEquals(charArray, newCharArray);
        Assertions.assertNotSame(charArray, newCharArray);

        // byte
        byte[] byteArray = new byte[] { 1 };
        byte[] newByteArray = ArrayUtils.removeElement(byteArray, (byte) 2);
        Assertions.assertArrayEquals(byteArray, newByteArray);
        Assertions.assertNotSame(byteArray, newByteArray);

        // double
        double[] doubleArray = new double[] { 1.0 };
        double[] newDoubleArray = ArrayUtils.removeElement(doubleArray, 2.0);
        Assertions.assertArrayEquals(doubleArray, newDoubleArray, 0.0);
        Assertions.assertNotSame(doubleArray, newDoubleArray);

        // float
        float[] floatArray = new float[] { 1.0f };
        float[] newFloatArray = ArrayUtils.removeElement(floatArray, 2.0f);
        Assertions.assertArrayEquals(floatArray, newFloatArray, 0.0f);
        Assertions.assertNotSame(floatArray, newFloatArray);

        // boolean
        boolean[] booleanArray = new boolean[] { true };
        boolean[] newBooleanArray = ArrayUtils.removeElement(booleanArray, false);
        Assertions.assertArrayEquals(booleanArray, newBooleanArray);
        Assertions.assertNotSame(booleanArray, newBooleanArray);
    }

    @Test
    public void toMapEntryTest() {
        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>("key", "value");
        Map<Object, Object> map = ArrayUtils.toMap(new Object[] { entry });
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("value", map.get("key"));
    }

    @Test
    public void nullToEmptyWrapperTest() {
        Long[] longArray = new Long[] { 1L };
        Assertions.assertSame(longArray, ArrayUtils.nullToEmpty(longArray));
        Assertions.assertSame(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Long[0]));

        Integer[] integerArray = new Integer[] { 1 };
        Assertions.assertSame(integerArray, ArrayUtils.nullToEmpty(integerArray));
        Assertions.assertSame(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Integer[0]));

        Short[] shortArray = new Short[] { 1 };
        Assertions.assertSame(shortArray, ArrayUtils.nullToEmpty(shortArray));
        Assertions.assertSame(ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Short[0]));

        Character[] charArray = new Character[] { 'a' };
        Assertions.assertSame(charArray, ArrayUtils.nullToEmpty(charArray));
        Assertions.assertSame(ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Character[0]));

        Byte[] byteArray = new Byte[] { 1 };
        Assertions.assertSame(byteArray, ArrayUtils.nullToEmpty(byteArray));
        Assertions.assertSame(ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Byte[0]));

        Double[] doubleArray = new Double[] { 1.0 };
        Assertions.assertSame(doubleArray, ArrayUtils.nullToEmpty(doubleArray));
        Assertions.assertSame(ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Double[0]));

        Float[] floatArray = new Float[] { 1.0f };
        Assertions.assertSame(floatArray, ArrayUtils.nullToEmpty(floatArray));
        Assertions.assertSame(ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Float[0]));

        Boolean[] booleanArray = new Boolean[] { true };
        Assertions.assertSame(booleanArray, ArrayUtils.nullToEmpty(booleanArray));
        Assertions.assertSame(ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Boolean[0]));
    }

    @Test
    public void subarrayTypeTest() {
        String[] stringArray = new String[] { "a", "b" };
        Object[] subarray = ArrayUtils.subarray(stringArray, 0, 1);
        Assertions.assertEquals(String[].class, subarray.getClass());

        // Test empty subarray type preservation
        Object[] emptySubarray = ArrayUtils.subarray(stringArray, 0, 0);
        Assertions.assertEquals(String[].class, emptySubarray.getClass());
    }

    @Test
    public void indexOfTypeTest() {
        Integer[] intArray = new Integer[] { 1, 2 };
        Assertions.assertEquals(-1, ArrayUtils.indexOf(intArray, "string"));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(intArray, "string"));
    }

    @Test
    public void addAllTypeMismatchTest() {
        try {
            ArrayUtils.addAll(new Long[] { 1L }, new Double[] { 1.0 });
            Assertions.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void constructorTest() {
        new ArrayUtils();
    }

    @Test
    public void safeToObjectCoverageTest() {
        Assertions.assertNull(ArrayUtils.safeToObject(null));

        // Primitives
        Assertions.assertArrayEquals(new Character[] { 'a' }, ArrayUtils.safeToObject(new char[] { 'a' }));
        Assertions.assertArrayEquals(new Byte[] { 1 }, ArrayUtils.safeToObject(new byte[] { 1 }));
        Assertions.assertArrayEquals(new Short[] { 1 }, ArrayUtils.safeToObject(new short[] { 1 }));
        Assertions.assertArrayEquals(new Integer[] { 1 }, ArrayUtils.safeToObject(new int[] { 1 }));
        Assertions.assertArrayEquals(new Long[] { 1L }, ArrayUtils.safeToObject(new long[] { 1L }));
        Assertions.assertArrayEquals(new Float[] { 1.0f }, ArrayUtils.safeToObject(new float[] { 1.0f }));
        Assertions.assertArrayEquals(new Double[] { 1.0 }, ArrayUtils.safeToObject(new double[] { 1.0 }));
        Assertions.assertArrayEquals(new Boolean[] { true }, ArrayUtils.safeToObject(new boolean[] { true }));

        // Wrappers
        Assertions.assertArrayEquals(new Character[] { 'a' }, ArrayUtils.safeToObject(new Character[] { 'a' }));
        Assertions.assertArrayEquals(new Byte[] { 1 }, ArrayUtils.safeToObject(new Byte[] { 1 }));
        Assertions.assertArrayEquals(new Short[] { 1 }, ArrayUtils.safeToObject(new Short[] { 1 }));
        Assertions.assertArrayEquals(new Integer[] { 1 }, ArrayUtils.safeToObject(new Integer[] { 1 }));
        Assertions.assertArrayEquals(new Long[] { 1L }, ArrayUtils.safeToObject(new Long[] { 1L }));
        Assertions.assertArrayEquals(new Float[] { 1.0f }, ArrayUtils.safeToObject(new Float[] { 1.0f }));
        Assertions.assertArrayEquals(new Double[] { 1.0 }, ArrayUtils.safeToObject(new Double[] { 1.0 }));
        Assertions.assertArrayEquals(new Boolean[] { true }, ArrayUtils.safeToObject(new Boolean[] { true }));

        // Object array
        Assertions.assertArrayEquals(new String[] { "a" }, ArrayUtils.safeToObject(new String[] { "a" }));

        // Iterable
        Assertions.assertArrayEquals(new Object[] { "a", "b" }, ArrayUtils.safeToObject(java.util.Arrays.asList("a", "b")));

        // Single Object
        Assertions.assertArrayEquals(new Object[] { "a" }, ArrayUtils.safeToObject("a"));
    }

    @Test
    public void addAllCoverageTest() {
        Assertions.assertNull(ArrayUtils.addAll(null, (Object[]) null));
        Assertions.assertArrayEquals(new String[] { "a" }, ArrayUtils.addAll(new String[] { "a" }, null));
        Assertions.assertArrayEquals(new String[] { "a" }, ArrayUtils.addAll(null, new String[] { "a" }));
        Assertions.assertArrayEquals(new String[] { "a", "b" }, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b" }));
    }

    @Test
    public void addCoverageTest() {
        // add(Object[] array, int index, Object element)
        Assertions.assertArrayEquals(new Object[] { null }, ArrayUtils.add(null, 0, null));
        Assertions.assertArrayEquals(new String[] { "a" }, ArrayUtils.add(null, 0, "a"));
        Assertions.assertArrayEquals(new String[] { "a", "b" }, ArrayUtils.add(new String[] { "a" }, 1, "b"));
        Assertions.assertArrayEquals(new String[] { "b", "a" }, ArrayUtils.add(new String[] { "a" }, 0, "b"));

        try {
            ArrayUtils.add(new String[] { "a" }, 2, "b");
            Assertions.fail();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            ArrayUtils.add(new String[] { "a" }, -1, "b");
            Assertions.fail();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            ArrayUtils.add(null, 1, "a");
            Assertions.fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void lastIndexOfDoubleCoverageTest() {
        double[] array = new double[] { 1.1, 2.2, 3.3, 2.2 };

        // lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance)
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(null, 2.2, 0, 0.1));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(new double[] {}, 2.2, 0, 0.1));
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(array, 2.2, -1, 0.1));

        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 2.2, 10, 0.1)); // startIndex >= length
        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 2.2, 3, 0.1));
        Assertions.assertEquals(1, ArrayUtils.lastIndexOf(array, 2.2, 2, 0.1));

        Assertions.assertEquals(3, ArrayUtils.lastIndexOf(array, 2.25, 3, 0.1)); // Tolerance match
        Assertions.assertEquals(-1, ArrayUtils.lastIndexOf(array, 4.4, 3, 0.1));
    }
}
