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
import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilsTest {

    @Test
    public void toMapTest() {
        Assert.assertNull(ArrayUtils.toMap(null));

        String[][] mapData = new String[][] { { "RED", "#FF0000" }, { "GREEN", "#00FF00" }, { "BLUE", "#0000FF" } };
        Map<Object, Object> colorMap = ArrayUtils.toMap(mapData);
        Assert.assertEquals(3, colorMap.size());
        Assert.assertEquals("#FF0000", colorMap.get("RED"));
        Assert.assertEquals("#00FF00", colorMap.get("GREEN"));
        Assert.assertEquals("#0000FF", colorMap.get("BLUE"));

        try {
            ArrayUtils.toMap(new String[][] { { "RED" } });
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            ArrayUtils.toMap(new Object[] { "RED" });
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void cloneTest() {
        Assert.assertNull(ArrayUtils.clone((Object[]) null));
        Assert.assertNull(ArrayUtils.clone((long[]) null));
        Assert.assertNull(ArrayUtils.clone((int[]) null));
        Assert.assertNull(ArrayUtils.clone((short[]) null));
        Assert.assertNull(ArrayUtils.clone((char[]) null));
        Assert.assertNull(ArrayUtils.clone((byte[]) null));
        Assert.assertNull(ArrayUtils.clone((double[]) null));
        Assert.assertNull(ArrayUtils.clone((float[]) null));
        Assert.assertNull(ArrayUtils.clone((boolean[]) null));

        String[] strArray = new String[] { "a", "b" };
        String[] strArrayClone = ArrayUtils.clone(strArray);
        Assert.assertNotSame(strArray, strArrayClone);
        Assert.assertEquals(strArray.length, strArrayClone.length);
        Assert.assertEquals(strArray[0], strArrayClone[0]);

        int[] intArray = new int[] { 1, 2 };
        int[] intArrayClone = ArrayUtils.clone(intArray);
        Assert.assertNotSame(intArray, intArrayClone);
        Assert.assertEquals(intArray.length, intArrayClone.length);
        Assert.assertEquals(intArray[0], intArrayClone[0]);
    }

    @Test
    public void nullToEmptyTest() {
        // Object
        Assert.assertSame(ArrayUtils.nullToEmpty((Object[]) null), ArrayUtils.EMPTY_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new Object[0]), ArrayUtils.EMPTY_OBJECT_ARRAY);
        Object[] objArray = new Object[] { new Object() };
        Assert.assertSame(ArrayUtils.nullToEmpty(objArray), objArray);

        // String
        Assert.assertSame(ArrayUtils.nullToEmpty((String[]) null), ArrayUtils.EMPTY_STRING_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new String[0]), ArrayUtils.EMPTY_STRING_ARRAY);
        String[] strArray = new String[] { "a" };
        Assert.assertSame(ArrayUtils.nullToEmpty(strArray), strArray);

        // long
        Assert.assertSame(ArrayUtils.nullToEmpty((long[]) null), ArrayUtils.EMPTY_LONG_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new long[0]), ArrayUtils.EMPTY_LONG_ARRAY);
        long[] longArray = new long[] { 1L };
        Assert.assertSame(ArrayUtils.nullToEmpty(longArray), longArray);

        // int
        Assert.assertSame(ArrayUtils.nullToEmpty((int[]) null), ArrayUtils.EMPTY_INT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new int[0]), ArrayUtils.EMPTY_INT_ARRAY);
        int[] intArray = new int[] { 1 };
        Assert.assertSame(ArrayUtils.nullToEmpty(intArray), intArray);

        // short
        Assert.assertSame(ArrayUtils.nullToEmpty((short[]) null), ArrayUtils.EMPTY_SHORT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new short[0]), ArrayUtils.EMPTY_SHORT_ARRAY);
        short[] shortArray = new short[] { 1 };
        Assert.assertSame(ArrayUtils.nullToEmpty(shortArray), shortArray);

        // char
        Assert.assertSame(ArrayUtils.nullToEmpty((char[]) null), ArrayUtils.EMPTY_CHAR_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new char[0]), ArrayUtils.EMPTY_CHAR_ARRAY);
        char[] charArray = new char[] { 'a' };
        Assert.assertSame(ArrayUtils.nullToEmpty(charArray), charArray);

        // byte
        Assert.assertSame(ArrayUtils.nullToEmpty((byte[]) null), ArrayUtils.EMPTY_BYTE_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new byte[0]), ArrayUtils.EMPTY_BYTE_ARRAY);
        byte[] byteArray = new byte[] { 1 };
        Assert.assertSame(ArrayUtils.nullToEmpty(byteArray), byteArray);

        // double
        Assert.assertSame(ArrayUtils.nullToEmpty((double[]) null), ArrayUtils.EMPTY_DOUBLE_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new double[0]), ArrayUtils.EMPTY_DOUBLE_ARRAY);
        double[] doubleArray = new double[] { 1.0 };
        Assert.assertSame(ArrayUtils.nullToEmpty(doubleArray), doubleArray);

        // float
        Assert.assertSame(ArrayUtils.nullToEmpty((float[]) null), ArrayUtils.EMPTY_FLOAT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new float[0]), ArrayUtils.EMPTY_FLOAT_ARRAY);
        float[] floatArray = new float[] { 1.0f };
        Assert.assertSame(ArrayUtils.nullToEmpty(floatArray), floatArray);

        // boolean
        Assert.assertSame(ArrayUtils.nullToEmpty((boolean[]) null), ArrayUtils.EMPTY_BOOLEAN_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty(new boolean[0]), ArrayUtils.EMPTY_BOOLEAN_ARRAY);
        boolean[] booleanArray = new boolean[] { true };
        Assert.assertSame(ArrayUtils.nullToEmpty(booleanArray), booleanArray);

        // Wrappers
        Assert.assertSame(ArrayUtils.nullToEmpty((Long[]) null), ArrayUtils.EMPTY_LONG_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Integer[]) null), ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Short[]) null), ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Character[]) null), ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Byte[]) null), ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Double[]) null), ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Float[]) null), ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY);
        Assert.assertSame(ArrayUtils.nullToEmpty((Boolean[]) null), ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY);
    }

    @Test
    public void subarrayTest() {
        // Object
        Object[] objArray = new Object[] { "a", "b", "c" };
        Assert.assertNull(ArrayUtils.subarray((Object[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(objArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(objArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(objArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(objArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(objArray, 1, 2).length);
        Assert.assertEquals("b", ArrayUtils.subarray(objArray, 1, 2)[0]);

        // long
        long[] longArray = new long[] { 1L, 2L, 3L };
        Assert.assertNull(ArrayUtils.subarray((long[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(longArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(longArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(longArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(longArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(longArray, 1, 2).length);
        Assert.assertEquals(2L, ArrayUtils.subarray(longArray, 1, 2)[0]);

        // int
        int[] intArray = new int[] { 1, 2, 3 };
        Assert.assertNull(ArrayUtils.subarray((int[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(intArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(intArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(intArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(intArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(intArray, 1, 2).length);
        Assert.assertEquals(2, ArrayUtils.subarray(intArray, 1, 2)[0]);

        // short
        short[] shortArray = new short[] { 1, 2, 3 };
        Assert.assertNull(ArrayUtils.subarray((short[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(shortArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(shortArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(shortArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(shortArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(shortArray, 1, 2).length);
        Assert.assertEquals(2, ArrayUtils.subarray(shortArray, 1, 2)[0]);

        // char
        char[] charArray = new char[] { 'a', 'b', 'c' };
        Assert.assertNull(ArrayUtils.subarray((char[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(charArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(charArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(charArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(charArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(charArray, 1, 2).length);
        Assert.assertEquals('b', ArrayUtils.subarray(charArray, 1, 2)[0]);

        // byte
        byte[] byteArray = new byte[] { 1, 2, 3 };
        Assert.assertNull(ArrayUtils.subarray((byte[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(byteArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(byteArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(byteArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(byteArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(byteArray, 1, 2).length);
        Assert.assertEquals(2, ArrayUtils.subarray(byteArray, 1, 2)[0]);

        // double
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        Assert.assertNull(ArrayUtils.subarray((double[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(doubleArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(doubleArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(doubleArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(doubleArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(doubleArray, 1, 2).length);
        Assert.assertEquals(2.0, ArrayUtils.subarray(doubleArray, 1, 2)[0], 0.0);

        // float
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        Assert.assertNull(ArrayUtils.subarray((float[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(floatArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(floatArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(floatArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(floatArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(floatArray, 1, 2).length);
        Assert.assertEquals(2.0f, ArrayUtils.subarray(floatArray, 1, 2)[0], 0.0);

        // boolean
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assert.assertNull(ArrayUtils.subarray((boolean[]) null, 0, 0));
        Assert.assertEquals(0, ArrayUtils.subarray(booleanArray, 0, 0).length);
        Assert.assertEquals(3, ArrayUtils.subarray(booleanArray, 0, 3).length);
        Assert.assertEquals(3, ArrayUtils.subarray(booleanArray, 0, 4).length);
        Assert.assertEquals(2, ArrayUtils.subarray(booleanArray, -1, 2).length);
        Assert.assertEquals(1, ArrayUtils.subarray(booleanArray, 1, 2).length);
        Assert.assertFalse(ArrayUtils.subarray(booleanArray, 1, 2)[0]);
    }

    @Test
    public void isSameLengthTest() {
        // Object
        Assert.assertTrue(ArrayUtils.isSameLength(null, (Object[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new Object[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new Object[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new Object[0], new Object[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new Object[] { "a" }, new Object[] { "b" }));
        Assert.assertFalse(ArrayUtils.isSameLength(new Object[] { "a" }, new Object[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new Object[0], new Object[] { "a" }));
        Assert.assertFalse(ArrayUtils.isSameLength(null, new Object[] { "a" }));
        Assert.assertFalse(ArrayUtils.isSameLength(new Object[] { "a" }, null));

        // long
        Assert.assertTrue(ArrayUtils.isSameLength(null, (long[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new long[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new long[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new long[0], new long[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new long[] { 1 }, new long[] { 2 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new long[] { 1 }, new long[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new long[0], new long[] { 1 }));

        // int
        Assert.assertTrue(ArrayUtils.isSameLength(null, (int[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new int[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new int[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new int[0], new int[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new int[] { 1 }, new int[] { 2 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new int[] { 1 }, new int[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new int[0], new int[] { 1 }));

        // short
        Assert.assertTrue(ArrayUtils.isSameLength(null, (short[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new short[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new short[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new short[0], new short[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new short[] { 1 }, new short[] { 2 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new short[] { 1 }, new short[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new short[0], new short[] { 1 }));

        // char
        Assert.assertTrue(ArrayUtils.isSameLength(null, (char[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new char[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new char[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new char[0], new char[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new char[] { 'a' }, new char[] { 'b' }));
        Assert.assertFalse(ArrayUtils.isSameLength(new char[] { 'a' }, new char[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new char[0], new char[] { 'a' }));

        // byte
        Assert.assertTrue(ArrayUtils.isSameLength(null, (byte[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new byte[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new byte[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new byte[0], new byte[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new byte[] { 1 }, new byte[] { 2 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new byte[] { 1 }, new byte[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new byte[0], new byte[] { 1 }));

        // double
        Assert.assertTrue(ArrayUtils.isSameLength(null, (double[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new double[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new double[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new double[0], new double[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new double[] { 1.0 }, new double[] { 2.0 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new double[] { 1.0 }, new double[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new double[0], new double[] { 1.0 }));

        // float
        Assert.assertTrue(ArrayUtils.isSameLength(null, (float[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new float[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new float[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new float[0], new float[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new float[] { 1.0f }, new float[] { 2.0f }));
        Assert.assertFalse(ArrayUtils.isSameLength(new float[] { 1.0f }, new float[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new float[0], new float[] { 1.0f }));

        // boolean
        Assert.assertTrue(ArrayUtils.isSameLength(null, (boolean[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new boolean[0], null));
        Assert.assertTrue(ArrayUtils.isSameLength(null, new boolean[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new boolean[0], new boolean[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new boolean[] { true }, new boolean[] { false }));
        Assert.assertFalse(ArrayUtils.isSameLength(new boolean[] { true }, new boolean[0]));
        Assert.assertFalse(ArrayUtils.isSameLength(new boolean[0], new boolean[] { true }));
    }

    @Test
    public void getLengthTest() {
        Assert.assertEquals(0, ArrayUtils.getLength(null));
        Assert.assertEquals(0, ArrayUtils.getLength(new Object[0]));
        Assert.assertEquals(1, ArrayUtils.getLength(new Object[] { "a" }));
        Assert.assertEquals(2, ArrayUtils.getLength(new int[] { 1, 2 }));
        Assert.assertEquals(1, ArrayUtils.getLength(new boolean[] { true }));

        try {
            ArrayUtils.getLength("not an array");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void isSameTypeTest() {
        Assert.assertTrue(ArrayUtils.isSameType(new Object[0], new Object[0]));
        Assert.assertTrue(ArrayUtils.isSameType(new String[0], new String[0]));
        Assert.assertFalse(ArrayUtils.isSameType(new Object[0], new String[0]));
        Assert.assertFalse(ArrayUtils.isSameType(new int[0], new long[0]));

        try {
            ArrayUtils.isSameType(null, new Object[0]);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            ArrayUtils.isSameType(new Object[0], null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void reverseTest() {
        // Object
        Object[] objArray = new Object[] { "a", "b", "c" };
        ArrayUtils.reverse(objArray);
        Assert.assertEquals("c", objArray[0]);
        Assert.assertEquals("b", objArray[1]);
        Assert.assertEquals("a", objArray[2]);
        ArrayUtils.reverse((Object[]) null); // should not throw exception

        // long
        long[] longArray = new long[] { 1L, 2L, 3L };
        ArrayUtils.reverse(longArray);
        Assert.assertEquals(3L, longArray[0]);
        Assert.assertEquals(2L, longArray[1]);
        Assert.assertEquals(1L, longArray[2]);
        ArrayUtils.reverse((long[]) null);

        // int
        int[] intArray = new int[] { 1, 2, 3 };
        ArrayUtils.reverse(intArray);
        Assert.assertEquals(3, intArray[0]);
        Assert.assertEquals(2, intArray[1]);
        Assert.assertEquals(1, intArray[2]);
        ArrayUtils.reverse((int[]) null);

        // short
        short[] shortArray = new short[] { 1, 2, 3 };
        ArrayUtils.reverse(shortArray);
        Assert.assertEquals(3, shortArray[0]);
        Assert.assertEquals(2, shortArray[1]);
        Assert.assertEquals(1, shortArray[2]);
        ArrayUtils.reverse((short[]) null);

        // char
        char[] charArray = new char[] { 'a', 'b', 'c' };
        ArrayUtils.reverse(charArray);
        Assert.assertEquals('c', charArray[0]);
        Assert.assertEquals('b', charArray[1]);
        Assert.assertEquals('a', charArray[2]);
        ArrayUtils.reverse((char[]) null);

        // byte
        byte[] byteArray = new byte[] { 1, 2, 3 };
        ArrayUtils.reverse(byteArray);
        Assert.assertEquals(3, byteArray[0]);
        Assert.assertEquals(2, byteArray[1]);
        Assert.assertEquals(1, byteArray[2]);
        ArrayUtils.reverse((byte[]) null);

        // double
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        ArrayUtils.reverse(doubleArray);
        Assert.assertEquals(3.0, doubleArray[0], 0.0);
        Assert.assertEquals(2.0, doubleArray[1], 0.0);
        Assert.assertEquals(1.0, doubleArray[2], 0.0);
        ArrayUtils.reverse((double[]) null);

        // float
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        ArrayUtils.reverse(floatArray);
        Assert.assertEquals(3.0f, floatArray[0], 0.0);
        Assert.assertEquals(2.0f, floatArray[1], 0.0);
        Assert.assertEquals(1.0f, floatArray[2], 0.0);
        ArrayUtils.reverse((float[]) null);

        // boolean
        boolean[] booleanArray = new boolean[] { true, false, true };
        ArrayUtils.reverse(booleanArray);
        Assert.assertTrue(booleanArray[0]);
        Assert.assertFalse(booleanArray[1]);
        Assert.assertTrue(booleanArray[2]);
        ArrayUtils.reverse((boolean[]) null);
    }

    @Test
    public void indexOfTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assert.assertEquals(-1, ArrayUtils.indexOf(null, null));
        Assert.assertEquals(-1, ArrayUtils.indexOf(null, "0"));
        Assert.assertEquals(0, ArrayUtils.indexOf(array, "0"));
        Assert.assertEquals(1, ArrayUtils.indexOf(array, "1"));
        Assert.assertEquals(2, ArrayUtils.indexOf(array, "2"));
        Assert.assertEquals(3, ArrayUtils.indexOf(array, "3"));
        Assert.assertEquals(4, ArrayUtils.indexOf(array, null));
        Assert.assertEquals(-1, ArrayUtils.indexOf(array, "not_found"));
        Assert.assertEquals(5, ArrayUtils.indexOf(array, "0", 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(array, "0", -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(array, "0", 10));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(0, ArrayUtils.indexOf(longArray, 0));
        Assert.assertEquals(1, ArrayUtils.indexOf(longArray, 1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(longArray, 99));
        Assert.assertEquals(4, ArrayUtils.indexOf(longArray, 0, 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(longArray, 0, -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf((long[]) null, 0));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(0, ArrayUtils.indexOf(intArray, 0));
        Assert.assertEquals(1, ArrayUtils.indexOf(intArray, 1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(intArray, 99));
        Assert.assertEquals(4, ArrayUtils.indexOf(intArray, 0, 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(intArray, 0, -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf((int[]) null, 0));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(0, ArrayUtils.indexOf(shortArray, (short) 0));
        Assert.assertEquals(1, ArrayUtils.indexOf(shortArray, (short) 1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(shortArray, (short) 99));
        Assert.assertEquals(4, ArrayUtils.indexOf(shortArray, (short) 0, 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(shortArray, (short) 0, -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf((short[]) null, (short) 0));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assert.assertEquals(0, ArrayUtils.indexOf(charArray, 'a'));
        Assert.assertEquals(1, ArrayUtils.indexOf(charArray, 'b'));
        Assert.assertEquals(-1, ArrayUtils.indexOf(charArray, 'z'));
        Assert.assertEquals(4, ArrayUtils.indexOf(charArray, 'a', 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(charArray, 'a', -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(0, ArrayUtils.indexOf(byteArray, (byte) 0));
        Assert.assertEquals(1, ArrayUtils.indexOf(byteArray, (byte) 1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(byteArray, (byte) 99));
        Assert.assertEquals(4, ArrayUtils.indexOf(byteArray, (byte) 0, 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(byteArray, (byte) 0, -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf((byte[]) null, (byte) 0));

        // double[]
        double[] doubleArray = new double[] { 0.0, 1.1, 2.2, 3.3, 0.0 };
        Assert.assertEquals(0, ArrayUtils.indexOf(doubleArray, 0.0));
        Assert.assertEquals(1, ArrayUtils.indexOf(doubleArray, 1.1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(doubleArray, 9.9));
        Assert.assertEquals(4, ArrayUtils.indexOf(doubleArray, 0.0, 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(doubleArray, 0.0, -1));
        assert ArrayUtils.indexOf(doubleArray, 1.1, 0.1) == 1; // tolerance
        assert ArrayUtils.indexOf(doubleArray, 1.1, 2, 0.1) == -1; // tolerance with start index
        Assert.assertEquals(-1, ArrayUtils.indexOf(null, 0.0));

        // float[]
        float[] floatArray = new float[] { 0.0f, 1.1f, 2.2f, 3.3f, 0.0f };
        Assert.assertEquals(0, ArrayUtils.indexOf(floatArray, 0.0f));
        Assert.assertEquals(1, ArrayUtils.indexOf(floatArray, 1.1f));
        Assert.assertEquals(-1, ArrayUtils.indexOf(floatArray, 9.9f));
        Assert.assertEquals(4, ArrayUtils.indexOf(floatArray, 0.0f, 2));
        Assert.assertEquals(0, ArrayUtils.indexOf(floatArray, 0.0f, -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf((float[]) null, 0.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assert.assertEquals(0, ArrayUtils.indexOf(booleanArray, true));
        Assert.assertEquals(1, ArrayUtils.indexOf(booleanArray, false));
        Assert.assertEquals(2, ArrayUtils.indexOf(booleanArray, true, 1));
        Assert.assertEquals(0, ArrayUtils.indexOf(booleanArray, true, -1));
        Assert.assertEquals(-1, ArrayUtils.indexOf(null, true));
    }

    @Test
    public void lastIndexOfTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(null, null));
        Assert.assertEquals(5, ArrayUtils.lastIndexOf(array, "0"));
        Assert.assertEquals(1, ArrayUtils.lastIndexOf(array, "1"));
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(array, null));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(array, "not_found"));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(array, "0", 4));
        Assert.assertEquals(5, ArrayUtils.lastIndexOf(array, "0", 10));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(array, "0", -1));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(longArray, 0));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(longArray, 0, 3));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf((long[]) null, 0));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(intArray, 0));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(intArray, 0, 3));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf((int[]) null, 0));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(shortArray, (short) 0));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(shortArray, (short) 0, 3));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf((short[]) null, (short) 0));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(charArray, 'a'));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(charArray, 'a', 3));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(byteArray, (byte) 0));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(byteArray, (byte) 0, 3));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf((byte[]) null, (byte) 0));

        // double[]
        double[] doubleArray = new double[] { 0.0, 1.1, 2.2, 3.3, 0.0 };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(doubleArray, 0.0));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(doubleArray, 0.0, 3));
        assert ArrayUtils.lastIndexOf(doubleArray, 1.1, 0.1) == 1; // tolerance
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(null, 0.0));

        // float[]
        float[] floatArray = new float[] { 0.0f, 1.1f, 2.2f, 3.3f, 0.0f };
        Assert.assertEquals(4, ArrayUtils.lastIndexOf(floatArray, 0.0f));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(floatArray, 0.0f, 3));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf((float[]) null, 0.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assert.assertEquals(2, ArrayUtils.lastIndexOf(booleanArray, true));
        Assert.assertEquals(0, ArrayUtils.lastIndexOf(booleanArray, true, 1));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(null, true));
    }

    @Test
    public void containsTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assert.assertTrue(ArrayUtils.contains(array, "0"));
        Assert.assertFalse(ArrayUtils.contains(array, "not_found"));
        Assert.assertTrue(ArrayUtils.contains(array, null));
        Assert.assertFalse(ArrayUtils.contains(null, "0"));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(longArray, 1));
        Assert.assertFalse(ArrayUtils.contains(longArray, 99));
        Assert.assertFalse(ArrayUtils.contains((long[]) null, 1));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(intArray, 1));
        Assert.assertFalse(ArrayUtils.contains(intArray, 99));
        Assert.assertFalse(ArrayUtils.contains((int[]) null, 1));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(shortArray, (short) 1));
        Assert.assertFalse(ArrayUtils.contains(shortArray, (short) 99));
        Assert.assertFalse(ArrayUtils.contains((short[]) null, (short) 1));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assert.assertTrue(ArrayUtils.contains(charArray, 'b'));
        Assert.assertFalse(ArrayUtils.contains(charArray, 'z'));
        Assert.assertFalse(ArrayUtils.contains((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(byteArray, (byte) 1));
        Assert.assertFalse(ArrayUtils.contains(byteArray, (byte) 99));
        Assert.assertFalse(ArrayUtils.contains((byte[]) null, (byte) 1));

        // double[]
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        Assert.assertTrue(ArrayUtils.contains(doubleArray, 1.0));
        Assert.assertFalse(ArrayUtils.contains(doubleArray, 9.9));
        Assert.assertTrue(ArrayUtils.contains(doubleArray, 1.1, 0.2));
        Assert.assertFalse(ArrayUtils.contains(doubleArray, 1.5, 0.1));
        Assert.assertFalse(ArrayUtils.contains(null, 1.0));

        // float[]
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        Assert.assertTrue(ArrayUtils.contains(floatArray, 1.0f));
        Assert.assertFalse(ArrayUtils.contains(floatArray, 9.9f));
        Assert.assertFalse(ArrayUtils.contains((float[]) null, 1.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false };
        Assert.assertTrue(ArrayUtils.contains(booleanArray, true));
        Assert.assertTrue(ArrayUtils.contains(booleanArray, false));
        Assert.assertFalse(ArrayUtils.contains(new boolean[] { true }, false));
        Assert.assertFalse(ArrayUtils.contains(null, true));
    }

    @Test
    public void toPrimitiveTest() {
        // Integer
        Assert.assertNull(ArrayUtils.toPrimitive((Integer[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Integer[0]).length);
        Assert.assertEquals(2, ArrayUtils.toPrimitive(new Integer[] { 1, 2 }).length);
        Assert.assertEquals(1, ArrayUtils.toPrimitive(new Integer[] { 1, 2 })[0]);
        try {
            ArrayUtils.toPrimitive(new Integer[] { null });
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(99, ArrayUtils.toPrimitive(new Integer[] { null }, 99)[0]);

        // Long
        Assert.assertNull(ArrayUtils.toPrimitive((Long[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Long[0]).length);
        Assert.assertEquals(1L, ArrayUtils.toPrimitive(new Long[] { 1L, 2L })[0]);
        Assert.assertEquals(99L, ArrayUtils.toPrimitive(new Long[] { null }, 99L)[0]);

        // Short
        Assert.assertNull(ArrayUtils.toPrimitive((Short[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Short[0]).length);
        Assert.assertEquals(ArrayUtils.toPrimitive(new Short[] { (short) 1, (short) 2 })[0], (short) 1);
        Assert.assertEquals(ArrayUtils.toPrimitive(new Short[] { null }, (short) 99)[0], (short) 99);

        // Byte
        Assert.assertNull(ArrayUtils.toPrimitive((Byte[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Byte[0]).length);
        Assert.assertEquals(ArrayUtils.toPrimitive(new Byte[] { (byte) 1, (byte) 2 })[0], (byte) 1);
        Assert.assertEquals(ArrayUtils.toPrimitive(new Byte[] { null }, (byte) 99)[0], (byte) 99);

        // Double
        Assert.assertNull(ArrayUtils.toPrimitive((Double[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Double[0]).length);
        Assert.assertEquals(1.1, ArrayUtils.toPrimitive(new Double[] { 1.1, 2.2 })[0], 0.0);
        Assert.assertEquals(99.9, ArrayUtils.toPrimitive(new Double[] { null }, 99.9)[0], 0.0);

        // Float
        Assert.assertNull(ArrayUtils.toPrimitive((Float[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Float[0]).length);
        Assert.assertEquals(1.1f, ArrayUtils.toPrimitive(new Float[] { 1.1f, 2.2f })[0], 0.0);
        Assert.assertEquals(99.9f, ArrayUtils.toPrimitive(new Float[] { null }, 99.9f)[0], 0.0);

        // Boolean
        Assert.assertNull(ArrayUtils.toPrimitive((Boolean[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Boolean[0]).length);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Boolean[] { true, false })[0]);
        Assert.assertFalse(ArrayUtils.toPrimitive(new Boolean[] { null }, false)[0]);

        // Character
        Assert.assertNull(ArrayUtils.toPrimitive((Character[]) null));
        Assert.assertEquals(0, ArrayUtils.toPrimitive(new Character[0]).length);
        Assert.assertEquals('a', ArrayUtils.toPrimitive(new Character[] { 'a', 'b' })[0]);
        Assert.assertEquals('z', ArrayUtils.toPrimitive(new Character[] { null }, 'z')[0]);
    }

    @Test
    public void toObjectTest() {
        // int
        Assert.assertNull(ArrayUtils.toObject((int[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new int[0]).length);
        Assert.assertEquals(2, ArrayUtils.toObject(new int[] { 1, 2 }).length);
        Assert.assertEquals(1, (int) ArrayUtils.toObject(new int[] { 1, 2 })[0]);

        // long
        Assert.assertNull(ArrayUtils.toObject((long[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new long[0]).length);
        Assert.assertEquals(1L, (long) ArrayUtils.toObject(new long[] { 1L, 2L })[0]);

        // short
        Assert.assertNull(ArrayUtils.toObject((short[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new short[0]).length);
        Assert.assertEquals((short) ArrayUtils.toObject(new short[] { (short) 1, (short) 2 })[0], (short) 1);

        // byte
        Assert.assertNull(ArrayUtils.toObject((byte[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new byte[0]).length);
        Assert.assertEquals((byte) ArrayUtils.toObject(new byte[] { (byte) 1, (byte) 2 })[0], (byte) 1);

        // double
        Assert.assertNull(ArrayUtils.toObject((double[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new double[0]).length);
        Assert.assertEquals(1.1, ArrayUtils.toObject(new double[] { 1.1, 2.2 })[0], 0.0);

        // float
        Assert.assertNull(ArrayUtils.toObject((float[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new float[0]).length);
        Assert.assertEquals(1.1f, ArrayUtils.toObject(new float[] { 1.1f, 2.2f })[0], 0.0);

        // boolean
        Assert.assertNull(ArrayUtils.toObject((boolean[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new boolean[0]).length);
        Assert.assertEquals(true, (boolean) ArrayUtils.toObject(new boolean[] { true, false })[0]);

        // char
        Assert.assertNull(ArrayUtils.toObject((char[]) null));
        Assert.assertEquals(0, ArrayUtils.toObject(new char[0]).length);
        Assert.assertEquals('a', (char) ArrayUtils.toObject(new char[] { 'a', 'b' })[0]);
    }

    @Test
    public void safeToObjectTest() {
        Assert.assertNull(ArrayUtils.safeToObject(null));
        Assert.assertEquals(1, ArrayUtils.safeToObject("string").length);
        Assert.assertEquals("string", ArrayUtils.safeToObject("string")[0]);

        // arrays
        Assert.assertEquals(2, ArrayUtils.safeToObject(new int[] { 1, 2 }).length);
        Assert.assertEquals(2, ArrayUtils.safeToObject(new Integer[] { 1, 2 }).length);
        Assert.assertEquals(1, ArrayUtils.safeToObject(new char[] { 'a' }).length);

        // iterable
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("a");
        list.add("b");
        Assert.assertEquals(2, ArrayUtils.safeToObject(list).length);
    }

    @Test
    public void isEmptyTest() {
        Assert.assertTrue(ArrayUtils.isEmpty((Object[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new Object[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new Object[] { "" }));
        Assert.assertTrue(ArrayUtils.isEmpty((long[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new long[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new long[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((int[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new int[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new int[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((short[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new short[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new short[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((char[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new char[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new char[] { 'a' }));
        Assert.assertTrue(ArrayUtils.isEmpty((byte[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new byte[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new byte[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((double[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new double[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new double[] { 1.1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((float[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new float[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new float[] { 1.1f }));
        Assert.assertTrue(ArrayUtils.isEmpty((boolean[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new boolean[0]));
        Assert.assertFalse(ArrayUtils.isEmpty(new boolean[] { true }));
    }

    @Test
    public void isNotEmptyTest() {
        Assert.assertFalse(ArrayUtils.isNotEmpty((Object[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new Object[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new Object[] { "" }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((long[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new long[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new long[] { 1 }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((int[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new int[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new int[] { 1 }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((short[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new short[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new short[] { 1 }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((char[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new char[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new char[] { 'a' }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((byte[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new byte[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new byte[] { 1 }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((double[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new double[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new double[] { 1.1 }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((float[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new float[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new float[] { 1.1f }));
        Assert.assertFalse(ArrayUtils.isNotEmpty((boolean[]) null));
        Assert.assertFalse(ArrayUtils.isNotEmpty(new boolean[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new boolean[] { true }));
    }

    @Test
    public void addAllTest() {
        Assert.assertNull(ArrayUtils.addAll(null, (Object[]) null));
        Assert.assertEquals(1, ArrayUtils.addAll(null, new String[] { "a" }).length);
        Assert.assertEquals(1, ArrayUtils.addAll(new String[] { "a" }, null).length);
        Assert.assertEquals(2, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b" }).length);
        Assert.assertEquals(3, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b", "c" }).length);

        // primitives
        Assert.assertNull(ArrayUtils.addAll(null, (int[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new int[] { 1 }, new int[] { 2 }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (boolean[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new boolean[] { true }, new boolean[] { false }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (char[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new char[] { 'a' }, new char[] { 'b' }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (byte[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new byte[] { 1 }, new byte[] { 2 }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (short[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new short[] { 1 }, new short[] { 2 }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (long[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new long[] { 1 }, new long[] { 2 }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (float[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new float[] { 1 }, new float[] { 2 }).length);
        Assert.assertNull(ArrayUtils.addAll(null, (double[]) null));
        Assert.assertEquals(2, ArrayUtils.addAll(new double[] { 1 }, new double[] { 2 }).length);
    }

    @Test
    public void addTest() {
        // Object
        String[] newArray = ArrayUtils.add(null, "a");
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals("a", newArray[0]);
        newArray = ArrayUtils.add(newArray, "b");
        Assert.assertEquals(2, newArray.length);
        Assert.assertEquals("b", newArray[1]);

        // int
        int[] newIntArray = ArrayUtils.add((int[]) null, 1);
        Assert.assertEquals(1, newIntArray.length);
        Assert.assertEquals(1, newIntArray[0]);
        newIntArray = ArrayUtils.add(newIntArray, 2);
        Assert.assertEquals(2, newIntArray.length);
        Assert.assertEquals(2, newIntArray[1]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.add(null, true);
        Assert.assertEquals(1, newBooleanArray.length);
        Assert.assertTrue(newBooleanArray[0]);
        newBooleanArray = ArrayUtils.add(newBooleanArray, false);
        Assert.assertEquals(2, newBooleanArray.length);
        Assert.assertFalse(newBooleanArray[1]);

        // char
        char[] newCharArray = ArrayUtils.add((char[]) null, 'a');
        Assert.assertEquals(1, newCharArray.length);
        Assert.assertEquals('a', newCharArray[0]);
        newCharArray = ArrayUtils.add(newCharArray, 'b');
        Assert.assertEquals(2, newCharArray.length);
        Assert.assertEquals('b', newCharArray[1]);

        // byte
        byte[] newByteArray = ArrayUtils.add((byte[]) null, (byte) 1);
        Assert.assertEquals(1, newByteArray.length);
        Assert.assertEquals(1, newByteArray[0]);
        newByteArray = ArrayUtils.add(newByteArray, (byte) 2);
        Assert.assertEquals(2, newByteArray.length);
        Assert.assertEquals(2, newByteArray[1]);

        // short
        short[] newShortArray = ArrayUtils.add((short[]) null, (short) 1);
        Assert.assertEquals(1, newShortArray.length);
        Assert.assertEquals(1, newShortArray[0]);
        newShortArray = ArrayUtils.add(newShortArray, (short) 2);
        Assert.assertEquals(2, newShortArray.length);
        Assert.assertEquals(2, newShortArray[1]);

        // long
        long[] newLongArray = ArrayUtils.add((long[]) null, 1L);
        Assert.assertEquals(1, newLongArray.length);
        Assert.assertEquals(1L, newLongArray[0]);
        newLongArray = ArrayUtils.add(newLongArray, 2L);
        Assert.assertEquals(2, newLongArray.length);
        Assert.assertEquals(2L, newLongArray[1]);

        // float
        float[] newFloatArray = ArrayUtils.add((float[]) null, 1.1f);
        Assert.assertEquals(1, newFloatArray.length);
        Assert.assertEquals(1.1f, newFloatArray[0], 0.0);
        newFloatArray = ArrayUtils.add(newFloatArray, 2.2f);
        Assert.assertEquals(2, newFloatArray.length);
        Assert.assertEquals(2.2f, newFloatArray[1], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.add(null, 1.1);
        Assert.assertEquals(1, newDoubleArray.length);
        Assert.assertEquals(1.1, newDoubleArray[0], 0.0);
        newDoubleArray = ArrayUtils.add(newDoubleArray, 2.2);
        Assert.assertEquals(2, newDoubleArray.length);
        Assert.assertEquals(2.2, newDoubleArray[1], 0.0);
    }

    @Test
    public void insertTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.add(null, 0, "a");
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals("a", newArray[0]);
        newArray = (String[]) ArrayUtils.add(newArray, 0, "b");
        Assert.assertEquals(2, newArray.length);
        Assert.assertEquals("b", newArray[0]);
        Assert.assertEquals("a", newArray[1]);

        // int
        int[] newIntArray = ArrayUtils.add((int[]) null, 0, 1);
        Assert.assertEquals(1, newIntArray.length);
        Assert.assertEquals(1, newIntArray[0]);
        newIntArray = ArrayUtils.add(newIntArray, 0, 2);
        Assert.assertEquals(2, newIntArray.length);
        Assert.assertEquals(2, newIntArray[0]);
        Assert.assertEquals(1, newIntArray[1]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.add(null, 0, true);
        Assert.assertEquals(1, newBooleanArray.length);
        Assert.assertTrue(newBooleanArray[0]);
        newBooleanArray = ArrayUtils.add(newBooleanArray, 0, false);
        Assert.assertEquals(2, newBooleanArray.length);
        Assert.assertFalse(newBooleanArray[0]);
        Assert.assertTrue(newBooleanArray[1]);

        // char
        char[] newCharArray = ArrayUtils.add((char[]) null, 0, 'a');
        Assert.assertEquals(1, newCharArray.length);
        Assert.assertEquals('a', newCharArray[0]);
        newCharArray = ArrayUtils.add(newCharArray, 0, 'b');
        Assert.assertEquals(2, newCharArray.length);
        Assert.assertEquals('b', newCharArray[0]);
        Assert.assertEquals('a', newCharArray[1]);

        // byte
        byte[] newByteArray = ArrayUtils.add((byte[]) null, 0, (byte) 1);
        Assert.assertEquals(1, newByteArray.length);
        Assert.assertEquals(1, newByteArray[0]);
        newByteArray = ArrayUtils.add(newByteArray, 0, (byte) 2);
        Assert.assertEquals(2, newByteArray.length);
        Assert.assertEquals(2, newByteArray[0]);
        Assert.assertEquals(1, newByteArray[1]);

        // short
        short[] newShortArray = ArrayUtils.add((short[]) null, 0, (short) 1);
        Assert.assertEquals(1, newShortArray.length);
        Assert.assertEquals(1, newShortArray[0]);
        newShortArray = ArrayUtils.add(newShortArray, 0, (short) 2);
        Assert.assertEquals(2, newShortArray.length);
        Assert.assertEquals(2, newShortArray[0]);
        Assert.assertEquals(1, newShortArray[1]);

        // long
        long[] newLongArray = ArrayUtils.add((long[]) null, 0, 1L);
        Assert.assertEquals(1, newLongArray.length);
        Assert.assertEquals(1L, newLongArray[0]);
        newLongArray = ArrayUtils.add(newLongArray, 0, 2L);
        Assert.assertEquals(2, newLongArray.length);
        Assert.assertEquals(2L, newLongArray[0]);
        Assert.assertEquals(1L, newLongArray[1]);

        // float
        float[] newFloatArray = ArrayUtils.add((float[]) null, 0, 1.1f);
        Assert.assertEquals(1, newFloatArray.length);
        Assert.assertEquals(1.1f, newFloatArray[0], 0.0);
        newFloatArray = ArrayUtils.add(newFloatArray, 0, 2.2f);
        Assert.assertEquals(2, newFloatArray.length);
        Assert.assertEquals(2.2f, newFloatArray[0], 0.0);
        Assert.assertEquals(1.1f, newFloatArray[1], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.add(null, 0, 1.1);
        Assert.assertEquals(1, newDoubleArray.length);
        Assert.assertEquals(1.1, newDoubleArray[0], 0.0);
        newDoubleArray = ArrayUtils.add(newDoubleArray, 0, 2.2);
        Assert.assertEquals(2, newDoubleArray.length);
        Assert.assertEquals(2.2, newDoubleArray[0], 0.0);
        Assert.assertEquals(1.1, newDoubleArray[1], 0.0);
    }

    @Test
    public void removeTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.remove(new String[] { "a", "b" }, 0);
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals("b", newArray[0]);

        // int
        int[] newIntArray = ArrayUtils.remove(new int[] { 1, 2 }, 0);
        Assert.assertEquals(1, newIntArray.length);
        Assert.assertEquals(2, newIntArray[0]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.remove(new boolean[] { true, false }, 0);
        Assert.assertEquals(1, newBooleanArray.length);
        Assert.assertFalse(newBooleanArray[0]);

        // char
        char[] newCharArray = ArrayUtils.remove(new char[] { 'a', 'b' }, 0);
        Assert.assertEquals(1, newCharArray.length);
        Assert.assertEquals('b', newCharArray[0]);

        // byte
        byte[] newByteArray = ArrayUtils.remove(new byte[] { 1, 2 }, 0);
        Assert.assertEquals(1, newByteArray.length);
        Assert.assertEquals(2, newByteArray[0]);

        // short
        short[] newShortArray = ArrayUtils.remove(new short[] { 1, 2 }, 0);
        Assert.assertEquals(1, newShortArray.length);
        Assert.assertEquals(2, newShortArray[0]);

        // long
        long[] newLongArray = ArrayUtils.remove(new long[] { 1L, 2L }, 0);
        Assert.assertEquals(1, newLongArray.length);
        Assert.assertEquals(2L, newLongArray[0]);

        // float
        float[] newFloatArray = ArrayUtils.remove(new float[] { 1.1f, 2.2f }, 0);
        Assert.assertEquals(1, newFloatArray.length);
        Assert.assertEquals(2.2f, newFloatArray[0], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.remove(new double[] { 1.1, 2.2 }, 0);
        Assert.assertEquals(1, newDoubleArray.length);
        Assert.assertEquals(2.2, newDoubleArray[0], 0.0);
    }

    @Test
    public void removeElementTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.removeElement(new String[] { "a", "b" }, "a");
        Assert.assertEquals(1, newArray.length);
        Assert.assertEquals("b", newArray[0]);

        // int
        int[] newIntArray = ArrayUtils.removeElement(new int[] { 1, 2 }, 1);
        Assert.assertEquals(1, newIntArray.length);
        Assert.assertEquals(2, newIntArray[0]);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.removeElement(new boolean[] { true, false }, true);
        Assert.assertEquals(1, newBooleanArray.length);
        Assert.assertFalse(newBooleanArray[0]);

        // char
        char[] newCharArray = ArrayUtils.removeElement(new char[] { 'a', 'b' }, 'a');
        Assert.assertEquals(1, newCharArray.length);
        Assert.assertEquals('b', newCharArray[0]);

        // byte
        byte[] newByteArray = ArrayUtils.removeElement(new byte[] { 1, 2 }, (byte) 1);
        Assert.assertEquals(1, newByteArray.length);
        Assert.assertEquals(2, newByteArray[0]);

        // short
        short[] newShortArray = ArrayUtils.removeElement(new short[] { 1, 2 }, (short) 1);
        Assert.assertEquals(1, newShortArray.length);
        Assert.assertEquals(2, newShortArray[0]);

        // long
        long[] newLongArray = ArrayUtils.removeElement(new long[] { 1L, 2L }, 1L);
        Assert.assertEquals(1, newLongArray.length);
        Assert.assertEquals(2L, newLongArray[0]);

        // float
        float[] newFloatArray = ArrayUtils.removeElement(new float[] { 1.1f, 2.2f }, 1.1f);
        Assert.assertEquals(1, newFloatArray.length);
        Assert.assertEquals(2.2f, newFloatArray[0], 0.0);

        // double
        double[] newDoubleArray = ArrayUtils.removeElement(new double[] { 1.1, 2.2 }, 1.1);
        Assert.assertEquals(1, newDoubleArray.length);
        Assert.assertEquals(2.2, newDoubleArray[0], 0.0);
    }

    @Test
    public void clearNullTest() {
        Assert.assertEquals(0, ArrayUtils.clearNull(null).length);
        Assert.assertEquals(0, ArrayUtils.clearNull(new Object[0]).length);
        Assert.assertEquals(0, ArrayUtils.clearNull(new Object[] { null }).length);
        Object[] result = ArrayUtils.clearNull(new Object[] { "a", null, "b" });
        Assert.assertEquals(2, result.length);
        Assert.assertEquals("a", result[0]);
        Assert.assertEquals("b", result[1]);
    }

    @Test
    public void toMapCoverageTest() {
        // Test with Map.Entry
        Map.Entry<String, String> entry1 = new AbstractMap.SimpleEntry<String, String>("key1", "value1");
        Map.Entry<String, String> entry2 = new AbstractMap.SimpleEntry<String, String>("key2", "value2");
        Object[] array = new Object[] { entry1, entry2 };
        Map map = ArrayUtils.toMap(array);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("value1", map.get("key1"));
        Assert.assertEquals("value2", map.get("key2"));

        // Test with Object[] of length >= 2
        Object[] item1 = new Object[] { "k1", "v1" };
        Object[] item2 = new Object[] { "k2", "v2", "extra" }; // extra should be ignored
        Object[] array2 = new Object[] { item1, item2 };
        Map map2 = ArrayUtils.toMap(array2);
        Assert.assertEquals(2, map2.size());
        Assert.assertEquals("v1", map2.get("k1"));
        Assert.assertEquals("v2", map2.get("k2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAllIncompatibleTypesTest() {
        ArrayUtils.addAll(new String[] { "a" }, new Integer[] { 1 });
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addNullArrayInvalidIndexTest() {
        ArrayUtils.add(null, 1, "a");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeInvalidIndexTest() {
        ArrayUtils.remove(new String[] { "a" }, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeNegativeIndexTest() {
        ArrayUtils.remove(new String[] { "a" }, -1);
    }

    @Test
    public void isSameLengthCoverageTest() {
        // long
        Assert.assertFalse(ArrayUtils.isSameLength(null, new long[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new long[] { 1 }, null));

        // int
        Assert.assertFalse(ArrayUtils.isSameLength(null, new int[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new int[] { 1 }, null));

        // short
        Assert.assertFalse(ArrayUtils.isSameLength(null, new short[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new short[] { 1 }, null));

        // char
        Assert.assertFalse(ArrayUtils.isSameLength(null, new char[] { 'a' }));
        Assert.assertFalse(ArrayUtils.isSameLength(new char[] { 'a' }, null));

        // byte
        Assert.assertFalse(ArrayUtils.isSameLength(null, new byte[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new byte[] { 1 }, null));

        // double
        Assert.assertFalse(ArrayUtils.isSameLength(null, new double[] { 1.0 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new double[] { 1.0 }, null));

        // float
        Assert.assertFalse(ArrayUtils.isSameLength(null, new float[] { 1.0f }));
        Assert.assertFalse(ArrayUtils.isSameLength(new float[] { 1.0f }, null));

        // boolean
        Assert.assertFalse(ArrayUtils.isSameLength(null, new boolean[] { true }));
        Assert.assertFalse(ArrayUtils.isSameLength(new boolean[] { true }, null));
    }

    @Test
    public void subarrayCoverageTest() {
        // Object[]
        Assert.assertNull(ArrayUtils.subarray((Object[]) null, 0, 0));
        Assert.assertArrayEquals(new Object[] {}, ArrayUtils.subarray(new Object[] { "a", "b" }, 0, 0));
        Assert.assertArrayEquals(new Object[] { "a" }, ArrayUtils.subarray(new Object[] { "a", "b" }, 0, 1));
        Assert.assertArrayEquals(new Object[] { "b" }, ArrayUtils.subarray(new Object[] { "a", "b" }, 1, 2));
        Assert.assertArrayEquals(new Object[] { "a", "b" }, ArrayUtils.subarray(new Object[] { "a", "b" }, 0, 2));
        Assert.assertArrayEquals(new Object[] { "a", "b" }, ArrayUtils.subarray(new Object[] { "a", "b" }, -1, 3));
        Assert.assertArrayEquals(new Object[] {}, ArrayUtils.subarray(new Object[] { "a", "b" }, 2, 1));

        // long[]
        Assert.assertNull(ArrayUtils.subarray((long[]) null, 0, 0));
        Assert.assertArrayEquals(new long[] {}, ArrayUtils.subarray(new long[] { 1, 2 }, 0, 0));
        Assert.assertArrayEquals(new long[] { 1 }, ArrayUtils.subarray(new long[] { 1, 2 }, 0, 1));
        Assert.assertArrayEquals(new long[] { 2 }, ArrayUtils.subarray(new long[] { 1, 2 }, 1, 2));
        Assert.assertArrayEquals(new long[] { 1, 2 }, ArrayUtils.subarray(new long[] { 1, 2 }, 0, 2));
        Assert.assertArrayEquals(new long[] { 1, 2 }, ArrayUtils.subarray(new long[] { 1, 2 }, -1, 3));
        Assert.assertArrayEquals(new long[] {}, ArrayUtils.subarray(new long[] { 1, 2 }, 2, 1));

        // int[]
        Assert.assertNull(ArrayUtils.subarray((int[]) null, 0, 0));
        Assert.assertArrayEquals(new int[] {}, ArrayUtils.subarray(new int[] { 1, 2 }, 0, 0));
        Assert.assertArrayEquals(new int[] { 1 }, ArrayUtils.subarray(new int[] { 1, 2 }, 0, 1));
        Assert.assertArrayEquals(new int[] { 2 }, ArrayUtils.subarray(new int[] { 1, 2 }, 1, 2));
        Assert.assertArrayEquals(new int[] { 1, 2 }, ArrayUtils.subarray(new int[] { 1, 2 }, 0, 2));
        Assert.assertArrayEquals(new int[] { 1, 2 }, ArrayUtils.subarray(new int[] { 1, 2 }, -1, 3));
        Assert.assertArrayEquals(new int[] {}, ArrayUtils.subarray(new int[] { 1, 2 }, 2, 1));

        // short[]
        Assert.assertNull(ArrayUtils.subarray((short[]) null, 0, 0));
        Assert.assertArrayEquals(new short[] {}, ArrayUtils.subarray(new short[] { 1, 2 }, 0, 0));
        Assert.assertArrayEquals(new short[] { 1 }, ArrayUtils.subarray(new short[] { 1, 2 }, 0, 1));
        Assert.assertArrayEquals(new short[] { 2 }, ArrayUtils.subarray(new short[] { 1, 2 }, 1, 2));
        Assert.assertArrayEquals(new short[] { 1, 2 }, ArrayUtils.subarray(new short[] { 1, 2 }, 0, 2));
        Assert.assertArrayEquals(new short[] { 1, 2 }, ArrayUtils.subarray(new short[] { 1, 2 }, -1, 3));
        Assert.assertArrayEquals(new short[] {}, ArrayUtils.subarray(new short[] { 1, 2 }, 2, 1));

        // char[]
        Assert.assertNull(ArrayUtils.subarray((char[]) null, 0, 0));
        Assert.assertArrayEquals(new char[] {}, ArrayUtils.subarray(new char[] { 'a', 'b' }, 0, 0));
        Assert.assertArrayEquals(new char[] { 'a' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, 0, 1));
        Assert.assertArrayEquals(new char[] { 'b' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, 1, 2));
        Assert.assertArrayEquals(new char[] { 'a', 'b' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, 0, 2));
        Assert.assertArrayEquals(new char[] { 'a', 'b' }, ArrayUtils.subarray(new char[] { 'a', 'b' }, -1, 3));
        Assert.assertArrayEquals(new char[] {}, ArrayUtils.subarray(new char[] { 'a', 'b' }, 2, 1));

        // byte[]
        Assert.assertNull(ArrayUtils.subarray((byte[]) null, 0, 0));
        Assert.assertArrayEquals(new byte[] {}, ArrayUtils.subarray(new byte[] { 1, 2 }, 0, 0));
        Assert.assertArrayEquals(new byte[] { 1 }, ArrayUtils.subarray(new byte[] { 1, 2 }, 0, 1));
        Assert.assertArrayEquals(new byte[] { 2 }, ArrayUtils.subarray(new byte[] { 1, 2 }, 1, 2));
        Assert.assertArrayEquals(new byte[] { 1, 2 }, ArrayUtils.subarray(new byte[] { 1, 2 }, 0, 2));
        Assert.assertArrayEquals(new byte[] { 1, 2 }, ArrayUtils.subarray(new byte[] { 1, 2 }, -1, 3));
        Assert.assertArrayEquals(new byte[] {}, ArrayUtils.subarray(new byte[] { 1, 2 }, 2, 1));

        // double[]
        Assert.assertNull(ArrayUtils.subarray((double[]) null, 0, 0));
        Assert.assertArrayEquals(new double[] {}, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 0, 0), 0.0);
        Assert.assertArrayEquals(new double[] { 1.1 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 0, 1), 0.0);
        Assert.assertArrayEquals(new double[] { 2.2 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 1, 2), 0.0);
        Assert.assertArrayEquals(new double[] { 1.1, 2.2 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 0, 2), 0.0);
        Assert.assertArrayEquals(new double[] { 1.1, 2.2 }, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, -1, 3), 0.0);
        Assert.assertArrayEquals(new double[] {}, ArrayUtils.subarray(new double[] { 1.1, 2.2 }, 2, 1), 0.0);

        // float[]
        Assert.assertNull(ArrayUtils.subarray((float[]) null, 0, 0));
        Assert.assertArrayEquals(new float[] {}, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 0, 0), 0.0f);
        Assert.assertArrayEquals(new float[] { 1.1f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 0, 1), 0.0f);
        Assert.assertArrayEquals(new float[] { 2.2f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 1, 2), 0.0f);
        Assert.assertArrayEquals(new float[] { 1.1f, 2.2f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 0, 2), 0.0f);
        Assert.assertArrayEquals(new float[] { 1.1f, 2.2f }, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, -1, 3), 0.0f);
        Assert.assertArrayEquals(new float[] {}, ArrayUtils.subarray(new float[] { 1.1f, 2.2f }, 2, 1), 0.0f);

        // boolean[]
        Assert.assertNull(ArrayUtils.subarray((boolean[]) null, 0, 0));
        Assert.assertArrayEquals(new boolean[] {}, ArrayUtils.subarray(new boolean[] { true, false }, 0, 0));
        Assert.assertArrayEquals(new boolean[] { true }, ArrayUtils.subarray(new boolean[] { true, false }, 0, 1));
        Assert.assertArrayEquals(new boolean[] { false }, ArrayUtils.subarray(new boolean[] { true, false }, 1, 2));
        Assert.assertArrayEquals(new boolean[] { true, false }, ArrayUtils.subarray(new boolean[] { true, false }, 0, 2));
        Assert.assertArrayEquals(new boolean[] { true, false }, ArrayUtils.subarray(new boolean[] { true, false }, -1, 3));
        Assert.assertArrayEquals(new boolean[] {}, ArrayUtils.subarray(new boolean[] { true, false }, 2, 1));
    }

    @Test
    public void removeElementMissingTest() {
        // Object
        String[] strArray = new String[] { "a" };
        String[] newStrArray = (String[]) ArrayUtils.removeElement(strArray, "b");
        Assert.assertArrayEquals(strArray, newStrArray);
        Assert.assertNotSame(strArray, newStrArray); // Should be a clone

        // long
        long[] longArray = new long[] { 1L };
        long[] newLongArray = ArrayUtils.removeElement(longArray, 2L);
        Assert.assertArrayEquals(longArray, newLongArray);
        Assert.assertNotSame(longArray, newLongArray);

        // int
        int[] intArray = new int[] { 1 };
        int[] newIntArray = ArrayUtils.removeElement(intArray, 2);
        Assert.assertArrayEquals(intArray, newIntArray);
        Assert.assertNotSame(intArray, newIntArray);

        // short
        short[] shortArray = new short[] { 1 };
        short[] newShortArray = ArrayUtils.removeElement(shortArray, (short) 2);
        Assert.assertArrayEquals(shortArray, newShortArray);
        Assert.assertNotSame(shortArray, newShortArray);

        // char
        char[] charArray = new char[] { 'a' };
        char[] newCharArray = ArrayUtils.removeElement(charArray, 'b');
        Assert.assertArrayEquals(charArray, newCharArray);
        Assert.assertNotSame(charArray, newCharArray);

        // byte
        byte[] byteArray = new byte[] { 1 };
        byte[] newByteArray = ArrayUtils.removeElement(byteArray, (byte) 2);
        Assert.assertArrayEquals(byteArray, newByteArray);
        Assert.assertNotSame(byteArray, newByteArray);

        // double
        double[] doubleArray = new double[] { 1.0 };
        double[] newDoubleArray = ArrayUtils.removeElement(doubleArray, 2.0);
        Assert.assertArrayEquals(doubleArray, newDoubleArray, 0.0);
        Assert.assertNotSame(doubleArray, newDoubleArray);

        // float
        float[] floatArray = new float[] { 1.0f };
        float[] newFloatArray = ArrayUtils.removeElement(floatArray, 2.0f);
        Assert.assertArrayEquals(floatArray, newFloatArray, 0.0f);
        Assert.assertNotSame(floatArray, newFloatArray);

        // boolean
        boolean[] booleanArray = new boolean[] { true };
        boolean[] newBooleanArray = ArrayUtils.removeElement(booleanArray, false);
        Assert.assertArrayEquals(booleanArray, newBooleanArray);
        Assert.assertNotSame(booleanArray, newBooleanArray);
    }

    @Test
    public void toMapEntryTest() {
        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>("key", "value");
        Map<Object, Object> map = ArrayUtils.toMap(new Object[] { entry });
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("value", map.get("key"));
    }

    @Test
    public void nullToEmptyWrapperTest() {
        Long[] longArray = new Long[] { 1L };
        Assert.assertSame(longArray, ArrayUtils.nullToEmpty(longArray));
        Assert.assertSame(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Long[0]));

        Integer[] integerArray = new Integer[] { 1 };
        Assert.assertSame(integerArray, ArrayUtils.nullToEmpty(integerArray));
        Assert.assertSame(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Integer[0]));

        Short[] shortArray = new Short[] { 1 };
        Assert.assertSame(shortArray, ArrayUtils.nullToEmpty(shortArray));
        Assert.assertSame(ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Short[0]));

        Character[] charArray = new Character[] { 'a' };
        Assert.assertSame(charArray, ArrayUtils.nullToEmpty(charArray));
        Assert.assertSame(ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Character[0]));

        Byte[] byteArray = new Byte[] { 1 };
        Assert.assertSame(byteArray, ArrayUtils.nullToEmpty(byteArray));
        Assert.assertSame(ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Byte[0]));

        Double[] doubleArray = new Double[] { 1.0 };
        Assert.assertSame(doubleArray, ArrayUtils.nullToEmpty(doubleArray));
        Assert.assertSame(ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Double[0]));

        Float[] floatArray = new Float[] { 1.0f };
        Assert.assertSame(floatArray, ArrayUtils.nullToEmpty(floatArray));
        Assert.assertSame(ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Float[0]));

        Boolean[] booleanArray = new Boolean[] { true };
        Assert.assertSame(booleanArray, ArrayUtils.nullToEmpty(booleanArray));
        Assert.assertSame(ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY, ArrayUtils.nullToEmpty(new Boolean[0]));
    }

    @Test
    public void subarrayTypeTest() {
        String[] stringArray = new String[] { "a", "b" };
        Object[] subarray = ArrayUtils.subarray(stringArray, 0, 1);
        Assert.assertEquals(String[].class, subarray.getClass());

        // Test empty subarray type preservation
        Object[] emptySubarray = ArrayUtils.subarray(stringArray, 0, 0);
        Assert.assertEquals(String[].class, emptySubarray.getClass());
    }

    @Test
    public void indexOfTypeTest() {
        Integer[] intArray = new Integer[] { 1, 2 };
        Assert.assertEquals(-1, ArrayUtils.indexOf(intArray, "string"));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(intArray, "string"));
    }

    @Test
    public void addAllTypeMismatchTest() {
        try {
            ArrayUtils.addAll(new Long[] { 1L }, new Double[] { 1.0 });
            Assert.fail("Should throw IllegalArgumentException");
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
        Assert.assertNull(ArrayUtils.safeToObject(null));

        // Primitives
        Assert.assertArrayEquals(new Character[] { 'a' }, ArrayUtils.safeToObject(new char[] { 'a' }));
        Assert.assertArrayEquals(new Byte[] { 1 }, ArrayUtils.safeToObject(new byte[] { 1 }));
        Assert.assertArrayEquals(new Short[] { 1 }, ArrayUtils.safeToObject(new short[] { 1 }));
        Assert.assertArrayEquals(new Integer[] { 1 }, ArrayUtils.safeToObject(new int[] { 1 }));
        Assert.assertArrayEquals(new Long[] { 1L }, ArrayUtils.safeToObject(new long[] { 1L }));
        Assert.assertArrayEquals(new Float[] { 1.0f }, ArrayUtils.safeToObject(new float[] { 1.0f }));
        Assert.assertArrayEquals(new Double[] { 1.0 }, ArrayUtils.safeToObject(new double[] { 1.0 }));
        Assert.assertArrayEquals(new Boolean[] { true }, ArrayUtils.safeToObject(new boolean[] { true }));

        // Wrappers
        Assert.assertArrayEquals(new Character[] { 'a' }, ArrayUtils.safeToObject(new Character[] { 'a' }));
        Assert.assertArrayEquals(new Byte[] { 1 }, ArrayUtils.safeToObject(new Byte[] { 1 }));
        Assert.assertArrayEquals(new Short[] { 1 }, ArrayUtils.safeToObject(new Short[] { 1 }));
        Assert.assertArrayEquals(new Integer[] { 1 }, ArrayUtils.safeToObject(new Integer[] { 1 }));
        Assert.assertArrayEquals(new Long[] { 1L }, ArrayUtils.safeToObject(new Long[] { 1L }));
        Assert.assertArrayEquals(new Float[] { 1.0f }, ArrayUtils.safeToObject(new Float[] { 1.0f }));
        Assert.assertArrayEquals(new Double[] { 1.0 }, ArrayUtils.safeToObject(new Double[] { 1.0 }));
        Assert.assertArrayEquals(new Boolean[] { true }, ArrayUtils.safeToObject(new Boolean[] { true }));

        // Object array
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.safeToObject(new String[] { "a" }));

        // Iterable
        Assert.assertArrayEquals(new Object[] { "a", "b" }, ArrayUtils.safeToObject(java.util.Arrays.asList("a", "b")));

        // Single Object
        Assert.assertArrayEquals(new Object[] { "a" }, ArrayUtils.safeToObject("a"));
    }

    @Test
    public void addAllCoverageTest() {
        Assert.assertNull(ArrayUtils.addAll(null, (Object[]) null));
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.addAll(new String[] { "a" }, null));
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.addAll(null, new String[] { "a" }));
        Assert.assertArrayEquals(new String[] { "a", "b" }, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b" }));
    }

    @Test
    public void addCoverageTest() {
        // add(Object[] array, int index, Object element)
        Assert.assertArrayEquals(new Object[] { null }, ArrayUtils.add(null, 0, null));
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.add(null, 0, "a"));
        Assert.assertArrayEquals(new String[] { "a", "b" }, ArrayUtils.add(new String[] { "a" }, 1, "b"));
        Assert.assertArrayEquals(new String[] { "b", "a" }, ArrayUtils.add(new String[] { "a" }, 0, "b"));

        try {
            ArrayUtils.add(new String[] { "a" }, 2, "b");
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            ArrayUtils.add(new String[] { "a" }, -1, "b");
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            ArrayUtils.add(null, 1, "a");
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void lastIndexOfDoubleCoverageTest() {
        double[] array = new double[] { 1.1, 2.2, 3.3, 2.2 };

        // lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance)
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(null, 2.2, 0, 0.1));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(new double[] {}, 2.2, 0, 0.1));
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(array, 2.2, -1, 0.1));

        Assert.assertEquals(3, ArrayUtils.lastIndexOf(array, 2.2, 10, 0.1)); // startIndex >= length
        Assert.assertEquals(3, ArrayUtils.lastIndexOf(array, 2.2, 3, 0.1));
        Assert.assertEquals(1, ArrayUtils.lastIndexOf(array, 2.2, 2, 0.1));

        Assert.assertEquals(3, ArrayUtils.lastIndexOf(array, 2.25, 3, 0.1)); // Tolerance match
        Assert.assertEquals(-1, ArrayUtils.lastIndexOf(array, 4.4, 3, 0.1));
    }
}
