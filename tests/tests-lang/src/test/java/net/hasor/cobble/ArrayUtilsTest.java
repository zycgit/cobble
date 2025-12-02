/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        Assert.assertTrue(ArrayUtils.toMap(null) == null);

        String[][] mapData = new String[][] { { "RED", "#FF0000" }, { "GREEN", "#00FF00" }, { "BLUE", "#0000FF" } };
        Map<Object, Object> colorMap = ArrayUtils.toMap(mapData);
        Assert.assertTrue(colorMap.size() == 3);
        Assert.assertTrue(colorMap.get("RED").equals("#FF0000"));
        Assert.assertTrue(colorMap.get("GREEN").equals("#00FF00"));
        Assert.assertTrue(colorMap.get("BLUE").equals("#0000FF"));

        try {
            ArrayUtils.toMap(new String[][] { { "RED" } });
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            ArrayUtils.toMap(new Object[] { "RED" });
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void cloneTest() {
        Assert.assertTrue(ArrayUtils.clone((Object[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((long[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((int[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((short[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((char[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((byte[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((double[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((float[]) null) == null);
        Assert.assertTrue(ArrayUtils.clone((boolean[]) null) == null);

        String[] strArray = new String[] { "a", "b" };
        String[] strArrayClone = ArrayUtils.clone(strArray);
        Assert.assertTrue(strArray != strArrayClone);
        Assert.assertTrue(strArray.length == strArrayClone.length);
        Assert.assertTrue(strArray[0].equals(strArrayClone[0]));

        int[] intArray = new int[] { 1, 2 };
        int[] intArrayClone = ArrayUtils.clone(intArray);
        Assert.assertTrue(intArray != intArrayClone);
        Assert.assertTrue(intArray.length == intArrayClone.length);
        Assert.assertTrue(intArray[0] == intArrayClone[0]);
    }

    @Test
    public void nullToEmptyTest() {
        // Object
        Assert.assertTrue(ArrayUtils.nullToEmpty((Object[]) null) == ArrayUtils.EMPTY_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new Object[0]) == ArrayUtils.EMPTY_OBJECT_ARRAY);
        Object[] objArray = new Object[] { new Object() };
        Assert.assertTrue(ArrayUtils.nullToEmpty(objArray) == objArray);

        // String
        Assert.assertTrue(ArrayUtils.nullToEmpty((String[]) null) == ArrayUtils.EMPTY_STRING_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new String[0]) == ArrayUtils.EMPTY_STRING_ARRAY);
        String[] strArray = new String[] { "a" };
        Assert.assertTrue(ArrayUtils.nullToEmpty(strArray) == strArray);

        // long
        Assert.assertTrue(ArrayUtils.nullToEmpty((long[]) null) == ArrayUtils.EMPTY_LONG_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new long[0]) == ArrayUtils.EMPTY_LONG_ARRAY);
        long[] longArray = new long[] { 1L };
        Assert.assertTrue(ArrayUtils.nullToEmpty(longArray) == longArray);

        // int
        Assert.assertTrue(ArrayUtils.nullToEmpty((int[]) null) == ArrayUtils.EMPTY_INT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new int[0]) == ArrayUtils.EMPTY_INT_ARRAY);
        int[] intArray = new int[] { 1 };
        Assert.assertTrue(ArrayUtils.nullToEmpty(intArray) == intArray);

        // short
        Assert.assertTrue(ArrayUtils.nullToEmpty((short[]) null) == ArrayUtils.EMPTY_SHORT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new short[0]) == ArrayUtils.EMPTY_SHORT_ARRAY);
        short[] shortArray = new short[] { 1 };
        Assert.assertTrue(ArrayUtils.nullToEmpty(shortArray) == shortArray);

        // char
        Assert.assertTrue(ArrayUtils.nullToEmpty((char[]) null) == ArrayUtils.EMPTY_CHAR_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new char[0]) == ArrayUtils.EMPTY_CHAR_ARRAY);
        char[] charArray = new char[] { 'a' };
        Assert.assertTrue(ArrayUtils.nullToEmpty(charArray) == charArray);

        // byte
        Assert.assertTrue(ArrayUtils.nullToEmpty((byte[]) null) == ArrayUtils.EMPTY_BYTE_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new byte[0]) == ArrayUtils.EMPTY_BYTE_ARRAY);
        byte[] byteArray = new byte[] { 1 };
        Assert.assertTrue(ArrayUtils.nullToEmpty(byteArray) == byteArray);

        // double
        Assert.assertTrue(ArrayUtils.nullToEmpty((double[]) null) == ArrayUtils.EMPTY_DOUBLE_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new double[0]) == ArrayUtils.EMPTY_DOUBLE_ARRAY);
        double[] doubleArray = new double[] { 1.0 };
        Assert.assertTrue(ArrayUtils.nullToEmpty(doubleArray) == doubleArray);

        // float
        Assert.assertTrue(ArrayUtils.nullToEmpty((float[]) null) == ArrayUtils.EMPTY_FLOAT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new float[0]) == ArrayUtils.EMPTY_FLOAT_ARRAY);
        float[] floatArray = new float[] { 1.0f };
        Assert.assertTrue(ArrayUtils.nullToEmpty(floatArray) == floatArray);

        // boolean
        Assert.assertTrue(ArrayUtils.nullToEmpty((boolean[]) null) == ArrayUtils.EMPTY_BOOLEAN_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty(new boolean[0]) == ArrayUtils.EMPTY_BOOLEAN_ARRAY);
        boolean[] booleanArray = new boolean[] { true };
        Assert.assertTrue(ArrayUtils.nullToEmpty(booleanArray) == booleanArray);

        // Wrappers
        Assert.assertTrue(ArrayUtils.nullToEmpty((Long[]) null) == ArrayUtils.EMPTY_LONG_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Integer[]) null) == ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Short[]) null) == ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Character[]) null) == ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Byte[]) null) == ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Double[]) null) == ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Float[]) null) == ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY);
        Assert.assertTrue(ArrayUtils.nullToEmpty((Boolean[]) null) == ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY);
    }

    @Test
    public void subarrayTest() {
        // Object
        Object[] objArray = new Object[] { "a", "b", "c" };
        Assert.assertTrue(ArrayUtils.subarray((Object[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(objArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(objArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(objArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(objArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(objArray, 1, 2).length == 1);
        Assert.assertTrue(((String) ArrayUtils.subarray(objArray, 1, 2)[0]).equals("b"));

        // long
        long[] longArray = new long[] { 1L, 2L, 3L };
        Assert.assertTrue(ArrayUtils.subarray((long[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(longArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(longArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(longArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(longArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(longArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(longArray, 1, 2)[0] == 2L);

        // int
        int[] intArray = new int[] { 1, 2, 3 };
        Assert.assertTrue(ArrayUtils.subarray((int[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(intArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(intArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(intArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(intArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(intArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(intArray, 1, 2)[0] == 2);

        // short
        short[] shortArray = new short[] { 1, 2, 3 };
        Assert.assertTrue(ArrayUtils.subarray((short[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(shortArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(shortArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(shortArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(shortArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(shortArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(shortArray, 1, 2)[0] == 2);

        // char
        char[] charArray = new char[] { 'a', 'b', 'c' };
        Assert.assertTrue(ArrayUtils.subarray((char[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(charArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(charArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(charArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(charArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(charArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(charArray, 1, 2)[0] == 'b');

        // byte
        byte[] byteArray = new byte[] { 1, 2, 3 };
        Assert.assertTrue(ArrayUtils.subarray((byte[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(byteArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(byteArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(byteArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(byteArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(byteArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(byteArray, 1, 2)[0] == 2);

        // double
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        Assert.assertTrue(ArrayUtils.subarray((double[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(doubleArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(doubleArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(doubleArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(doubleArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(doubleArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(doubleArray, 1, 2)[0] == 2.0);

        // float
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        Assert.assertTrue(ArrayUtils.subarray((float[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(floatArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(floatArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(floatArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(floatArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(floatArray, 1, 2).length == 1);
        Assert.assertTrue(ArrayUtils.subarray(floatArray, 1, 2)[0] == 2.0f);

        // boolean
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assert.assertTrue(ArrayUtils.subarray((boolean[]) null, 0, 0) == null);
        Assert.assertTrue(ArrayUtils.subarray(booleanArray, 0, 0).length == 0);
        Assert.assertTrue(ArrayUtils.subarray(booleanArray, 0, 3).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(booleanArray, 0, 4).length == 3);
        Assert.assertTrue(ArrayUtils.subarray(booleanArray, -1, 2).length == 2);
        Assert.assertTrue(ArrayUtils.subarray(booleanArray, 1, 2).length == 1);
        Assert.assertTrue(!ArrayUtils.subarray(booleanArray, 1, 2)[0]);
    }

    @Test
    public void isSameLengthTest() {
        // Object
        Assert.assertTrue(ArrayUtils.isSameLength((Object[]) null, (Object[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new Object[0], (Object[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((Object[]) null, new Object[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new Object[0], new Object[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new Object[] { "a" }, new Object[] { "b" }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new Object[] { "a" }, new Object[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new Object[0], new Object[] { "a" }));
        Assert.assertTrue(!ArrayUtils.isSameLength((Object[]) null, new Object[] { "a" }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new Object[] { "a" }, (Object[]) null));

        // long
        Assert.assertTrue(ArrayUtils.isSameLength((long[]) null, (long[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new long[0], (long[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((long[]) null, new long[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new long[0], new long[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new long[] { 1 }, new long[] { 2 }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new long[] { 1 }, new long[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new long[0], new long[] { 1 }));

        // int
        Assert.assertTrue(ArrayUtils.isSameLength((int[]) null, (int[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new int[0], (int[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((int[]) null, new int[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new int[0], new int[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new int[] { 1 }, new int[] { 2 }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new int[] { 1 }, new int[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new int[0], new int[] { 1 }));

        // short
        Assert.assertTrue(ArrayUtils.isSameLength((short[]) null, (short[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new short[0], (short[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((short[]) null, new short[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new short[0], new short[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new short[] { 1 }, new short[] { 2 }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new short[] { 1 }, new short[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new short[0], new short[] { 1 }));

        // char
        Assert.assertTrue(ArrayUtils.isSameLength((char[]) null, (char[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new char[0], (char[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((char[]) null, new char[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new char[0], new char[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new char[] { 'a' }, new char[] { 'b' }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new char[] { 'a' }, new char[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new char[0], new char[] { 'a' }));

        // byte
        Assert.assertTrue(ArrayUtils.isSameLength((byte[]) null, (byte[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new byte[0], (byte[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((byte[]) null, new byte[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new byte[0], new byte[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new byte[] { 1 }, new byte[] { 2 }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new byte[] { 1 }, new byte[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new byte[0], new byte[] { 1 }));

        // double
        Assert.assertTrue(ArrayUtils.isSameLength((double[]) null, (double[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new double[0], (double[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((double[]) null, new double[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new double[0], new double[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new double[] { 1.0 }, new double[] { 2.0 }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new double[] { 1.0 }, new double[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new double[0], new double[] { 1.0 }));

        // float
        Assert.assertTrue(ArrayUtils.isSameLength((float[]) null, (float[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new float[0], (float[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((float[]) null, new float[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new float[0], new float[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new float[] { 1.0f }, new float[] { 2.0f }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new float[] { 1.0f }, new float[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new float[0], new float[] { 1.0f }));

        // boolean
        Assert.assertTrue(ArrayUtils.isSameLength((boolean[]) null, (boolean[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength(new boolean[0], (boolean[]) null));
        Assert.assertTrue(ArrayUtils.isSameLength((boolean[]) null, new boolean[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new boolean[0], new boolean[0]));
        Assert.assertTrue(ArrayUtils.isSameLength(new boolean[] { true }, new boolean[] { false }));
        Assert.assertTrue(!ArrayUtils.isSameLength(new boolean[] { true }, new boolean[0]));
        Assert.assertTrue(!ArrayUtils.isSameLength(new boolean[0], new boolean[] { true }));
    }

    @Test
    public void getLengthTest() {
        Assert.assertTrue(ArrayUtils.getLength(null) == 0);
        Assert.assertTrue(ArrayUtils.getLength(new Object[0]) == 0);
        Assert.assertTrue(ArrayUtils.getLength(new Object[] { "a" }) == 1);
        Assert.assertTrue(ArrayUtils.getLength(new int[] { 1, 2 }) == 2);
        Assert.assertTrue(ArrayUtils.getLength(new boolean[] { true }) == 1);

        try {
            ArrayUtils.getLength("not an array");
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void isSameTypeTest() {
        Assert.assertTrue(ArrayUtils.isSameType(new Object[0], new Object[0]));
        Assert.assertTrue(ArrayUtils.isSameType(new String[0], new String[0]));
        Assert.assertTrue(!ArrayUtils.isSameType(new Object[0], new String[0]));
        Assert.assertTrue(!ArrayUtils.isSameType(new int[0], new long[0]));

        try {
            ArrayUtils.isSameType(null, new Object[0]);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            ArrayUtils.isSameType(new Object[0], null);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void reverseTest() {
        // Object
        Object[] objArray = new Object[] { "a", "b", "c" };
        ArrayUtils.reverse(objArray);
        Assert.assertTrue(objArray[0].equals("c"));
        Assert.assertTrue(objArray[1].equals("b"));
        Assert.assertTrue(objArray[2].equals("a"));
        ArrayUtils.reverse((Object[]) null); // should not throw exception

        // long
        long[] longArray = new long[] { 1L, 2L, 3L };
        ArrayUtils.reverse(longArray);
        Assert.assertTrue(longArray[0] == 3L);
        Assert.assertTrue(longArray[1] == 2L);
        Assert.assertTrue(longArray[2] == 1L);
        ArrayUtils.reverse((long[]) null);

        // int
        int[] intArray = new int[] { 1, 2, 3 };
        ArrayUtils.reverse(intArray);
        Assert.assertTrue(intArray[0] == 3);
        Assert.assertTrue(intArray[1] == 2);
        Assert.assertTrue(intArray[2] == 1);
        ArrayUtils.reverse((int[]) null);

        // short
        short[] shortArray = new short[] { 1, 2, 3 };
        ArrayUtils.reverse(shortArray);
        Assert.assertTrue(shortArray[0] == 3);
        Assert.assertTrue(shortArray[1] == 2);
        Assert.assertTrue(shortArray[2] == 1);
        ArrayUtils.reverse((short[]) null);

        // char
        char[] charArray = new char[] { 'a', 'b', 'c' };
        ArrayUtils.reverse(charArray);
        Assert.assertTrue(charArray[0] == 'c');
        Assert.assertTrue(charArray[1] == 'b');
        Assert.assertTrue(charArray[2] == 'a');
        ArrayUtils.reverse((char[]) null);

        // byte
        byte[] byteArray = new byte[] { 1, 2, 3 };
        ArrayUtils.reverse(byteArray);
        Assert.assertTrue(byteArray[0] == 3);
        Assert.assertTrue(byteArray[1] == 2);
        Assert.assertTrue(byteArray[2] == 1);
        ArrayUtils.reverse((byte[]) null);

        // double
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        ArrayUtils.reverse(doubleArray);
        Assert.assertTrue(doubleArray[0] == 3.0);
        Assert.assertTrue(doubleArray[1] == 2.0);
        Assert.assertTrue(doubleArray[2] == 1.0);
        ArrayUtils.reverse((double[]) null);

        // float
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        ArrayUtils.reverse(floatArray);
        Assert.assertTrue(floatArray[0] == 3.0f);
        Assert.assertTrue(floatArray[1] == 2.0f);
        Assert.assertTrue(floatArray[2] == 1.0f);
        ArrayUtils.reverse((float[]) null);

        // boolean
        boolean[] booleanArray = new boolean[] { true, false, true };
        ArrayUtils.reverse(booleanArray);
        Assert.assertTrue(booleanArray[0]);
        Assert.assertTrue(!booleanArray[1]);
        Assert.assertTrue(booleanArray[2]);
        ArrayUtils.reverse((boolean[]) null);
    }

    @Test
    public void indexOfTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assert.assertTrue(ArrayUtils.indexOf(null, null) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(null, "0") == -1);
        Assert.assertTrue(ArrayUtils.indexOf(array, "0") == 0);
        Assert.assertTrue(ArrayUtils.indexOf(array, "1") == 1);
        Assert.assertTrue(ArrayUtils.indexOf(array, "2") == 2);
        Assert.assertTrue(ArrayUtils.indexOf(array, "3") == 3);
        Assert.assertTrue(ArrayUtils.indexOf(array, null) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(array, "not_found") == -1);
        Assert.assertTrue(ArrayUtils.indexOf(array, "0", 2) == 5);
        Assert.assertTrue(ArrayUtils.indexOf(array, "0", -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(array, "0", 10) == -1);

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.indexOf(longArray, 0) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(longArray, 1) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(longArray, 99) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(longArray, 0, 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(longArray, 0, -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((long[]) null, 0) == -1);

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.indexOf(intArray, 0) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(intArray, 1) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(intArray, 99) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(intArray, 0, 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(intArray, 0, -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((int[]) null, 0) == -1);

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.indexOf(shortArray, (short) 0) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(shortArray, (short) 1) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(shortArray, (short) 99) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(shortArray, (short) 0, 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(shortArray, (short) 0, -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((short[]) null, (short) 0) == -1);

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assert.assertTrue(ArrayUtils.indexOf(charArray, 'a') == 0);
        Assert.assertTrue(ArrayUtils.indexOf(charArray, 'b') == 1);
        Assert.assertTrue(ArrayUtils.indexOf(charArray, 'z') == -1);
        Assert.assertTrue(ArrayUtils.indexOf(charArray, 'a', 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(charArray, 'a', -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((char[]) null, 'a') == -1);

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.indexOf(byteArray, (byte) 0) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(byteArray, (byte) 1) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(byteArray, (byte) 99) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(byteArray, (byte) 0, 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(byteArray, (byte) 0, -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((byte[]) null, (byte) 0) == -1);

        // double[]
        double[] doubleArray = new double[] { 0.0, 1.1, 2.2, 3.3, 0.0 };
        Assert.assertTrue(ArrayUtils.indexOf(doubleArray, 0.0) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(doubleArray, 1.1) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(doubleArray, 9.9) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(doubleArray, 0.0, 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(doubleArray, 0.0, -1) == 0);
        assert ArrayUtils.indexOf(doubleArray, 1.1, 0.1) == 1; // tolerance
        assert ArrayUtils.indexOf(doubleArray, 1.1, 2, 0.1) == -1; // tolerance with start index
        Assert.assertTrue(ArrayUtils.indexOf((double[]) null, 0.0) == -1);

        // float[]
        float[] floatArray = new float[] { 0.0f, 1.1f, 2.2f, 3.3f, 0.0f };
        Assert.assertTrue(ArrayUtils.indexOf(floatArray, 0.0f) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(floatArray, 1.1f) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(floatArray, 9.9f) == -1);
        Assert.assertTrue(ArrayUtils.indexOf(floatArray, 0.0f, 2) == 4);
        Assert.assertTrue(ArrayUtils.indexOf(floatArray, 0.0f, -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((float[]) null, 0.0f) == -1);

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assert.assertTrue(ArrayUtils.indexOf(booleanArray, true) == 0);
        Assert.assertTrue(ArrayUtils.indexOf(booleanArray, false) == 1);
        Assert.assertTrue(ArrayUtils.indexOf(booleanArray, true, 1) == 2);
        Assert.assertTrue(ArrayUtils.indexOf(booleanArray, true, -1) == 0);
        Assert.assertTrue(ArrayUtils.indexOf((boolean[]) null, true) == -1);
    }

    @Test
    public void lastIndexOfTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assert.assertTrue(ArrayUtils.lastIndexOf(null, null) == -1);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, "0") == 5);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, "1") == 1);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, null) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, "not_found") == -1);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, "0", 4) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, "0", 10) == 5);
        Assert.assertTrue(ArrayUtils.lastIndexOf(array, "0", -1) == -1);

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.lastIndexOf(longArray, 0) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(longArray, 0, 3) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((long[]) null, 0) == -1);

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.lastIndexOf(intArray, 0) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(intArray, 0, 3) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((int[]) null, 0) == -1);

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.lastIndexOf(shortArray, (short) 0) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(shortArray, (short) 0, 3) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((short[]) null, (short) 0) == -1);

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assert.assertTrue(ArrayUtils.lastIndexOf(charArray, 'a') == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(charArray, 'a', 3) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((char[]) null, 'a') == -1);

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.lastIndexOf(byteArray, (byte) 0) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(byteArray, (byte) 0, 3) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((byte[]) null, (byte) 0) == -1);

        // double[]
        double[] doubleArray = new double[] { 0.0, 1.1, 2.2, 3.3, 0.0 };
        Assert.assertTrue(ArrayUtils.lastIndexOf(doubleArray, 0.0) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(doubleArray, 0.0, 3) == 0);
        assert ArrayUtils.lastIndexOf(doubleArray, 1.1, 0.1) == 1; // tolerance
        Assert.assertTrue(ArrayUtils.lastIndexOf((double[]) null, 0.0) == -1);

        // float[]
        float[] floatArray = new float[] { 0.0f, 1.1f, 2.2f, 3.3f, 0.0f };
        Assert.assertTrue(ArrayUtils.lastIndexOf(floatArray, 0.0f) == 4);
        Assert.assertTrue(ArrayUtils.lastIndexOf(floatArray, 0.0f, 3) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((float[]) null, 0.0f) == -1);

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false, true };
        Assert.assertTrue(ArrayUtils.lastIndexOf(booleanArray, true) == 2);
        Assert.assertTrue(ArrayUtils.lastIndexOf(booleanArray, true, 1) == 0);
        Assert.assertTrue(ArrayUtils.lastIndexOf((boolean[]) null, true) == -1);
    }

    @Test
    public void containsTest() {
        // Object[]
        Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        Assert.assertTrue(ArrayUtils.contains(array, "0"));
        Assert.assertTrue(!ArrayUtils.contains(array, "not_found"));
        Assert.assertTrue(ArrayUtils.contains(array, null));
        Assert.assertTrue(!ArrayUtils.contains(null, "0"));

        // long[]
        long[] longArray = new long[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(longArray, 1));
        Assert.assertTrue(!ArrayUtils.contains(longArray, 99));
        Assert.assertTrue(!ArrayUtils.contains((long[]) null, 1));

        // int[]
        int[] intArray = new int[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(intArray, 1));
        Assert.assertTrue(!ArrayUtils.contains(intArray, 99));
        Assert.assertTrue(!ArrayUtils.contains((int[]) null, 1));

        // short[]
        short[] shortArray = new short[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(shortArray, (short) 1));
        Assert.assertTrue(!ArrayUtils.contains(shortArray, (short) 99));
        Assert.assertTrue(!ArrayUtils.contains((short[]) null, (short) 1));

        // char[]
        char[] charArray = new char[] { 'a', 'b', 'c', 'd', 'a' };
        Assert.assertTrue(ArrayUtils.contains(charArray, 'b'));
        Assert.assertTrue(!ArrayUtils.contains(charArray, 'z'));
        Assert.assertTrue(!ArrayUtils.contains((char[]) null, 'a'));

        // byte[]
        byte[] byteArray = new byte[] { 0, 1, 2, 3, 0 };
        Assert.assertTrue(ArrayUtils.contains(byteArray, (byte) 1));
        Assert.assertTrue(!ArrayUtils.contains(byteArray, (byte) 99));
        Assert.assertTrue(!ArrayUtils.contains((byte[]) null, (byte) 1));

        // double[]
        double[] doubleArray = new double[] { 1.0, 2.0, 3.0 };
        Assert.assertTrue(ArrayUtils.contains(doubleArray, 1.0));
        Assert.assertTrue(!ArrayUtils.contains(doubleArray, 9.9));
        Assert.assertTrue(ArrayUtils.contains(doubleArray, 1.1, 0.2));
        Assert.assertTrue(!ArrayUtils.contains(doubleArray, 1.5, 0.1));
        Assert.assertTrue(!ArrayUtils.contains((double[]) null, 1.0));

        // float[]
        float[] floatArray = new float[] { 1.0f, 2.0f, 3.0f };
        Assert.assertTrue(ArrayUtils.contains(floatArray, 1.0f));
        Assert.assertTrue(!ArrayUtils.contains(floatArray, 9.9f));
        Assert.assertTrue(!ArrayUtils.contains((float[]) null, 1.0f));

        // boolean[]
        boolean[] booleanArray = new boolean[] { true, false };
        Assert.assertTrue(ArrayUtils.contains(booleanArray, true));
        Assert.assertTrue(ArrayUtils.contains(booleanArray, false));
        Assert.assertTrue(!ArrayUtils.contains(new boolean[] { true }, false));
        Assert.assertTrue(!ArrayUtils.contains((boolean[]) null, true));
    }

    @Test
    public void toPrimitiveTest() {
        // Integer
        Assert.assertTrue(ArrayUtils.toPrimitive((Integer[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Integer[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Integer[] { 1, 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Integer[] { 1, 2 })[0] == 1);
        try {
            ArrayUtils.toPrimitive(new Integer[] { null });
            Assert.assertTrue(false);
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        Assert.assertTrue(ArrayUtils.toPrimitive(new Integer[] { null }, 99)[0] == 99);

        // Long
        Assert.assertTrue(ArrayUtils.toPrimitive((Long[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Long[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Long[] { 1L, 2L })[0] == 1L);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Long[] { null }, 99L)[0] == 99L);

        // Short
        Assert.assertTrue(ArrayUtils.toPrimitive((Short[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Short[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Short[] { (short) 1, (short) 2 })[0] == (short) 1);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Short[] { null }, (short) 99)[0] == (short) 99);

        // Byte
        Assert.assertTrue(ArrayUtils.toPrimitive((Byte[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Byte[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Byte[] { (byte) 1, (byte) 2 })[0] == (byte) 1);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Byte[] { null }, (byte) 99)[0] == (byte) 99);

        // Double
        Assert.assertTrue(ArrayUtils.toPrimitive((Double[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Double[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Double[] { 1.1, 2.2 })[0] == 1.1);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Double[] { null }, 99.9)[0] == 99.9);

        // Float
        Assert.assertTrue(ArrayUtils.toPrimitive((Float[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Float[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Float[] { 1.1f, 2.2f })[0] == 1.1f);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Float[] { null }, 99.9f)[0] == 99.9f);

        // Boolean
        Assert.assertTrue(ArrayUtils.toPrimitive((Boolean[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Boolean[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Boolean[] { true, false })[0]);
        Assert.assertTrue(!ArrayUtils.toPrimitive(new Boolean[] { null }, false)[0]);

        // Character
        Assert.assertTrue(ArrayUtils.toPrimitive((Character[]) null) == null);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Character[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toPrimitive(new Character[] { 'a', 'b' })[0] == 'a');
        Assert.assertTrue(ArrayUtils.toPrimitive(new Character[] { null }, 'z')[0] == 'z');
    }

    @Test
    public void toObjectTest() {
        // int
        Assert.assertTrue(ArrayUtils.toObject((int[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new int[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new int[] { 1, 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.toObject(new int[] { 1, 2 })[0].equals(1));

        // long
        Assert.assertTrue(ArrayUtils.toObject((long[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new long[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new long[] { 1L, 2L })[0].equals(1L));

        // short
        Assert.assertTrue(ArrayUtils.toObject((short[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new short[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new short[] { (short) 1, (short) 2 })[0].equals((short) 1));

        // byte
        Assert.assertTrue(ArrayUtils.toObject((byte[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new byte[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new byte[] { (byte) 1, (byte) 2 })[0].equals((byte) 1));

        // double
        Assert.assertTrue(ArrayUtils.toObject((double[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new double[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new double[] { 1.1, 2.2 })[0].equals(1.1));

        // float
        Assert.assertTrue(ArrayUtils.toObject((float[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new float[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new float[] { 1.1f, 2.2f })[0].equals(1.1f));

        // boolean
        Assert.assertTrue(ArrayUtils.toObject((boolean[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new boolean[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new boolean[] { true, false })[0].equals(true));

        // char
        Assert.assertTrue(ArrayUtils.toObject((char[]) null) == null);
        Assert.assertTrue(ArrayUtils.toObject(new char[0]).length == 0);
        Assert.assertTrue(ArrayUtils.toObject(new char[] { 'a', 'b' })[0].equals('a'));
    }

    @Test
    public void safeToObjectTest() {
        Assert.assertTrue(ArrayUtils.safeToObject(null) == null);
        Assert.assertTrue(ArrayUtils.safeToObject("string").length == 1);
        Assert.assertTrue(ArrayUtils.safeToObject("string")[0].equals("string"));

        // arrays
        Assert.assertTrue(ArrayUtils.safeToObject(new int[] { 1, 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.safeToObject(new Integer[] { 1, 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.safeToObject(new char[] { 'a' }).length == 1);

        // iterable
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("a");
        list.add("b");
        Assert.assertTrue(ArrayUtils.safeToObject(list).length == 2);
    }

    @Test
    public void isEmptyTest() {
        Assert.assertTrue(ArrayUtils.isEmpty((Object[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new Object[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new Object[] { "" }));
        Assert.assertTrue(ArrayUtils.isEmpty((long[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new long[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new long[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((int[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new int[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new int[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((short[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new short[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new short[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((char[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new char[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new char[] { 'a' }));
        Assert.assertTrue(ArrayUtils.isEmpty((byte[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new byte[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new byte[] { 1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((double[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new double[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new double[] { 1.1 }));
        Assert.assertTrue(ArrayUtils.isEmpty((float[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new float[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new float[] { 1.1f }));
        Assert.assertTrue(ArrayUtils.isEmpty((boolean[]) null));
        Assert.assertTrue(ArrayUtils.isEmpty(new boolean[0]));
        Assert.assertTrue(!ArrayUtils.isEmpty(new boolean[] { true }));
    }

    @Test
    public void isNotEmptyTest() {
        Assert.assertTrue(!ArrayUtils.isNotEmpty((Object[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new Object[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new Object[] { "" }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((long[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new long[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new long[] { 1 }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((int[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new int[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new int[] { 1 }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((short[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new short[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new short[] { 1 }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((char[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new char[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new char[] { 'a' }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((byte[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new byte[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new byte[] { 1 }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((double[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new double[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new double[] { 1.1 }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((float[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new float[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new float[] { 1.1f }));
        Assert.assertTrue(!ArrayUtils.isNotEmpty((boolean[]) null));
        Assert.assertTrue(!ArrayUtils.isNotEmpty(new boolean[0]));
        Assert.assertTrue(ArrayUtils.isNotEmpty(new boolean[] { true }));
    }

    @Test
    public void addAllTest() {
        Assert.assertTrue(ArrayUtils.addAll((Object[]) null, (Object[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(null, new String[] { "a" }).length == 1);
        Assert.assertTrue(ArrayUtils.addAll(new String[] { "a" }, null).length == 1);
        Assert.assertTrue(ArrayUtils.addAll(new String[] { "a" }, new String[] { "b" }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll(new String[] { "a" }, new String[] { "b", "c" }).length == 3);

        // primitives
        Assert.assertTrue(ArrayUtils.addAll((int[]) null, (int[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new int[] { 1 }, new int[] { 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((boolean[]) null, (boolean[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new boolean[] { true }, new boolean[] { false }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((char[]) null, (char[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new char[] { 'a' }, new char[] { 'b' }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((byte[]) null, (byte[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new byte[] { 1 }, new byte[] { 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((short[]) null, (short[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new short[] { 1 }, new short[] { 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((long[]) null, (long[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new long[] { 1 }, new long[] { 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((float[]) null, (float[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new float[] { 1 }, new float[] { 2 }).length == 2);
        Assert.assertTrue(ArrayUtils.addAll((double[]) null, (double[]) null) == null);
        Assert.assertTrue(ArrayUtils.addAll(new double[] { 1 }, new double[] { 2 }).length == 2);
    }

    @Test
    public void addTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.add((String[]) null, "a");
        Assert.assertTrue(newArray.length == 1);
        Assert.assertTrue(newArray[0].equals("a"));
        newArray = (String[]) ArrayUtils.add(newArray, "b");
        Assert.assertTrue(newArray.length == 2);
        Assert.assertTrue(newArray[1].equals("b"));

        // int
        int[] newIntArray = ArrayUtils.add((int[]) null, 1);
        Assert.assertTrue(newIntArray.length == 1);
        Assert.assertTrue(newIntArray[0] == 1);
        newIntArray = ArrayUtils.add(newIntArray, 2);
        Assert.assertTrue(newIntArray.length == 2);
        Assert.assertTrue(newIntArray[1] == 2);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.add((boolean[]) null, true);
        Assert.assertTrue(newBooleanArray.length == 1);
        Assert.assertTrue(newBooleanArray[0]);
        newBooleanArray = ArrayUtils.add(newBooleanArray, false);
        Assert.assertTrue(newBooleanArray.length == 2);
        Assert.assertTrue(!newBooleanArray[1]);

        // char
        char[] newCharArray = ArrayUtils.add((char[]) null, 'a');
        Assert.assertTrue(newCharArray.length == 1);
        Assert.assertTrue(newCharArray[0] == 'a');
        newCharArray = ArrayUtils.add(newCharArray, 'b');
        Assert.assertTrue(newCharArray.length == 2);
        Assert.assertTrue(newCharArray[1] == 'b');

        // byte
        byte[] newByteArray = ArrayUtils.add((byte[]) null, (byte) 1);
        Assert.assertTrue(newByteArray.length == 1);
        Assert.assertTrue(newByteArray[0] == 1);
        newByteArray = ArrayUtils.add(newByteArray, (byte) 2);
        Assert.assertTrue(newByteArray.length == 2);
        Assert.assertTrue(newByteArray[1] == 2);

        // short
        short[] newShortArray = ArrayUtils.add((short[]) null, (short) 1);
        Assert.assertTrue(newShortArray.length == 1);
        Assert.assertTrue(newShortArray[0] == 1);
        newShortArray = ArrayUtils.add(newShortArray, (short) 2);
        Assert.assertTrue(newShortArray.length == 2);
        Assert.assertTrue(newShortArray[1] == 2);

        // long
        long[] newLongArray = ArrayUtils.add((long[]) null, 1L);
        Assert.assertTrue(newLongArray.length == 1);
        Assert.assertTrue(newLongArray[0] == 1L);
        newLongArray = ArrayUtils.add(newLongArray, 2L);
        Assert.assertTrue(newLongArray.length == 2);
        Assert.assertTrue(newLongArray[1] == 2L);

        // float
        float[] newFloatArray = ArrayUtils.add((float[]) null, 1.1f);
        Assert.assertTrue(newFloatArray.length == 1);
        Assert.assertTrue(newFloatArray[0] == 1.1f);
        newFloatArray = ArrayUtils.add(newFloatArray, 2.2f);
        Assert.assertTrue(newFloatArray.length == 2);
        Assert.assertTrue(newFloatArray[1] == 2.2f);

        // double
        double[] newDoubleArray = ArrayUtils.add((double[]) null, 1.1);
        Assert.assertTrue(newDoubleArray.length == 1);
        Assert.assertTrue(newDoubleArray[0] == 1.1);
        newDoubleArray = ArrayUtils.add(newDoubleArray, 2.2);
        Assert.assertTrue(newDoubleArray.length == 2);
        Assert.assertTrue(newDoubleArray[1] == 2.2);
    }

    @Test
    public void insertTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.add((String[]) null, 0, "a");
        Assert.assertTrue(newArray.length == 1);
        Assert.assertTrue(newArray[0].equals("a"));
        newArray = (String[]) ArrayUtils.add(newArray, 0, "b");
        Assert.assertTrue(newArray.length == 2);
        Assert.assertTrue(newArray[0].equals("b"));
        Assert.assertTrue(newArray[1].equals("a"));

        // int
        int[] newIntArray = ArrayUtils.add((int[]) null, 0, 1);
        Assert.assertTrue(newIntArray.length == 1);
        Assert.assertTrue(newIntArray[0] == 1);
        newIntArray = ArrayUtils.add(newIntArray, 0, 2);
        Assert.assertTrue(newIntArray.length == 2);
        Assert.assertTrue(newIntArray[0] == 2);
        Assert.assertTrue(newIntArray[1] == 1);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.add((boolean[]) null, 0, true);
        Assert.assertTrue(newBooleanArray.length == 1);
        Assert.assertTrue(newBooleanArray[0]);
        newBooleanArray = ArrayUtils.add(newBooleanArray, 0, false);
        Assert.assertTrue(newBooleanArray.length == 2);
        Assert.assertTrue(!newBooleanArray[0]);
        Assert.assertTrue(newBooleanArray[1]);

        // char
        char[] newCharArray = ArrayUtils.add((char[]) null, 0, 'a');
        Assert.assertTrue(newCharArray.length == 1);
        Assert.assertTrue(newCharArray[0] == 'a');
        newCharArray = ArrayUtils.add(newCharArray, 0, 'b');
        Assert.assertTrue(newCharArray.length == 2);
        Assert.assertTrue(newCharArray[0] == 'b');
        Assert.assertTrue(newCharArray[1] == 'a');

        // byte
        byte[] newByteArray = ArrayUtils.add((byte[]) null, 0, (byte) 1);
        Assert.assertTrue(newByteArray.length == 1);
        Assert.assertTrue(newByteArray[0] == 1);
        newByteArray = ArrayUtils.add(newByteArray, 0, (byte) 2);
        Assert.assertTrue(newByteArray.length == 2);
        Assert.assertTrue(newByteArray[0] == 2);
        Assert.assertTrue(newByteArray[1] == 1);

        // short
        short[] newShortArray = ArrayUtils.add((short[]) null, 0, (short) 1);
        Assert.assertTrue(newShortArray.length == 1);
        Assert.assertTrue(newShortArray[0] == 1);
        newShortArray = ArrayUtils.add(newShortArray, 0, (short) 2);
        Assert.assertTrue(newShortArray.length == 2);
        Assert.assertTrue(newShortArray[0] == 2);
        Assert.assertTrue(newShortArray[1] == 1);

        // long
        long[] newLongArray = ArrayUtils.add((long[]) null, 0, 1L);
        Assert.assertTrue(newLongArray.length == 1);
        Assert.assertTrue(newLongArray[0] == 1L);
        newLongArray = ArrayUtils.add(newLongArray, 0, 2L);
        Assert.assertTrue(newLongArray.length == 2);
        Assert.assertTrue(newLongArray[0] == 2L);
        Assert.assertTrue(newLongArray[1] == 1L);

        // float
        float[] newFloatArray = ArrayUtils.add((float[]) null, 0, 1.1f);
        Assert.assertTrue(newFloatArray.length == 1);
        Assert.assertTrue(newFloatArray[0] == 1.1f);
        newFloatArray = ArrayUtils.add(newFloatArray, 0, 2.2f);
        Assert.assertTrue(newFloatArray.length == 2);
        Assert.assertTrue(newFloatArray[0] == 2.2f);
        Assert.assertTrue(newFloatArray[1] == 1.1f);

        // double
        double[] newDoubleArray = ArrayUtils.add((double[]) null, 0, 1.1);
        Assert.assertTrue(newDoubleArray.length == 1);
        Assert.assertTrue(newDoubleArray[0] == 1.1);
        newDoubleArray = ArrayUtils.add(newDoubleArray, 0, 2.2);
        Assert.assertTrue(newDoubleArray.length == 2);
        Assert.assertTrue(newDoubleArray[0] == 2.2);
        Assert.assertTrue(newDoubleArray[1] == 1.1);
    }

    @Test
    public void removeTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.remove(new String[] { "a", "b" }, 0);
        Assert.assertTrue(newArray.length == 1);
        Assert.assertTrue(newArray[0].equals("b"));

        // int
        int[] newIntArray = ArrayUtils.remove(new int[] { 1, 2 }, 0);
        Assert.assertTrue(newIntArray.length == 1);
        Assert.assertTrue(newIntArray[0] == 2);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.remove(new boolean[] { true, false }, 0);
        Assert.assertTrue(newBooleanArray.length == 1);
        Assert.assertTrue(!newBooleanArray[0]);

        // char
        char[] newCharArray = ArrayUtils.remove(new char[] { 'a', 'b' }, 0);
        Assert.assertTrue(newCharArray.length == 1);
        Assert.assertTrue(newCharArray[0] == 'b');

        // byte
        byte[] newByteArray = ArrayUtils.remove(new byte[] { 1, 2 }, 0);
        Assert.assertTrue(newByteArray.length == 1);
        Assert.assertTrue(newByteArray[0] == 2);

        // short
        short[] newShortArray = ArrayUtils.remove(new short[] { 1, 2 }, 0);
        Assert.assertTrue(newShortArray.length == 1);
        Assert.assertTrue(newShortArray[0] == 2);

        // long
        long[] newLongArray = ArrayUtils.remove(new long[] { 1L, 2L }, 0);
        Assert.assertTrue(newLongArray.length == 1);
        Assert.assertTrue(newLongArray[0] == 2L);

        // float
        float[] newFloatArray = ArrayUtils.remove(new float[] { 1.1f, 2.2f }, 0);
        Assert.assertTrue(newFloatArray.length == 1);
        Assert.assertTrue(newFloatArray[0] == 2.2f);

        // double
        double[] newDoubleArray = ArrayUtils.remove(new double[] { 1.1, 2.2 }, 0);
        Assert.assertTrue(newDoubleArray.length == 1);
        Assert.assertTrue(newDoubleArray[0] == 2.2);
    }

    @Test
    public void removeElementTest() {
        // Object
        String[] newArray = (String[]) ArrayUtils.removeElement(new String[] { "a", "b" }, "a");
        Assert.assertTrue(newArray.length == 1);
        Assert.assertTrue(newArray[0].equals("b"));

        // int
        int[] newIntArray = ArrayUtils.removeElement(new int[] { 1, 2 }, 1);
        Assert.assertTrue(newIntArray.length == 1);
        Assert.assertTrue(newIntArray[0] == 2);

        // boolean
        boolean[] newBooleanArray = ArrayUtils.removeElement(new boolean[] { true, false }, true);
        Assert.assertTrue(newBooleanArray.length == 1);
        Assert.assertTrue(!newBooleanArray[0]);

        // char
        char[] newCharArray = ArrayUtils.removeElement(new char[] { 'a', 'b' }, 'a');
        Assert.assertTrue(newCharArray.length == 1);
        Assert.assertTrue(newCharArray[0] == 'b');

        // byte
        byte[] newByteArray = ArrayUtils.removeElement(new byte[] { 1, 2 }, (byte) 1);
        Assert.assertTrue(newByteArray.length == 1);
        Assert.assertTrue(newByteArray[0] == 2);

        // short
        short[] newShortArray = ArrayUtils.removeElement(new short[] { 1, 2 }, (short) 1);
        Assert.assertTrue(newShortArray.length == 1);
        Assert.assertTrue(newShortArray[0] == 2);

        // long
        long[] newLongArray = ArrayUtils.removeElement(new long[] { 1L, 2L }, 1L);
        Assert.assertTrue(newLongArray.length == 1);
        Assert.assertTrue(newLongArray[0] == 2L);

        // float
        float[] newFloatArray = ArrayUtils.removeElement(new float[] { 1.1f, 2.2f }, 1.1f);
        Assert.assertTrue(newFloatArray.length == 1);
        Assert.assertTrue(newFloatArray[0] == 2.2f);

        // double
        double[] newDoubleArray = ArrayUtils.removeElement(new double[] { 1.1, 2.2 }, 1.1);
        Assert.assertTrue(newDoubleArray.length == 1);
        Assert.assertTrue(newDoubleArray[0] == 2.2);
    }

    @Test
    public void clearNullTest() {
        Assert.assertTrue(ArrayUtils.clearNull(null).length == 0);
        Assert.assertTrue(ArrayUtils.clearNull(new Object[0]).length == 0);
        Assert.assertTrue(ArrayUtils.clearNull(new Object[] { null }).length == 0);
        Object[] result = ArrayUtils.clearNull(new Object[] { "a", null, "b" });
        Assert.assertTrue(result.length == 2);
        Assert.assertTrue(result[0].equals("a"));
        Assert.assertTrue(result[1].equals("b"));
    }

    @Test
    public void toMapCoverageTest() {
        // Test with Map.Entry
        Map.Entry<String, String> entry1 = new AbstractMap.SimpleEntry<String, String>("key1", "value1");
        Map.Entry<String, String> entry2 = new AbstractMap.SimpleEntry<String, String>("key2", "value2");
        Object[] array = new Object[] { entry1, entry2 };
        Map map = ArrayUtils.toMap(array);
        Assert.assertTrue(map.size() == 2);
        Assert.assertTrue(map.get("key1").equals("value1"));
        Assert.assertTrue(map.get("key2").equals("value2"));

        // Test with Object[] of length >= 2
        Object[] item1 = new Object[] { "k1", "v1" };
        Object[] item2 = new Object[] { "k2", "v2", "extra" }; // extra should be ignored
        Object[] array2 = new Object[] { item1, item2 };
        Map map2 = ArrayUtils.toMap(array2);
        Assert.assertTrue(map2.size() == 2);
        Assert.assertTrue(map2.get("k1").equals("v1"));
        Assert.assertTrue(map2.get("k2").equals("v2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAllIncompatibleTypesTest() {
        ArrayUtils.addAll(new String[] { "a" }, new Integer[] { 1 });
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addNullArrayInvalidIndexTest() {
        ArrayUtils.add((Object[]) null, 1, "a");
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
        Assert.assertFalse(ArrayUtils.isSameLength((long[]) null, new long[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new long[] { 1 }, (long[]) null));

        // int
        Assert.assertFalse(ArrayUtils.isSameLength((int[]) null, new int[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new int[] { 1 }, (int[]) null));

        // short
        Assert.assertFalse(ArrayUtils.isSameLength((short[]) null, new short[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new short[] { 1 }, (short[]) null));

        // char
        Assert.assertFalse(ArrayUtils.isSameLength((char[]) null, new char[] { 'a' }));
        Assert.assertFalse(ArrayUtils.isSameLength(new char[] { 'a' }, (char[]) null));

        // byte
        Assert.assertFalse(ArrayUtils.isSameLength((byte[]) null, new byte[] { 1 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new byte[] { 1 }, (byte[]) null));

        // double
        Assert.assertFalse(ArrayUtils.isSameLength((double[]) null, new double[] { 1.0 }));
        Assert.assertFalse(ArrayUtils.isSameLength(new double[] { 1.0 }, (double[]) null));

        // float
        Assert.assertFalse(ArrayUtils.isSameLength((float[]) null, new float[] { 1.0f }));
        Assert.assertFalse(ArrayUtils.isSameLength(new float[] { 1.0f }, (float[]) null));

        // boolean
        Assert.assertFalse(ArrayUtils.isSameLength((boolean[]) null, new boolean[] { true }));
        Assert.assertFalse(ArrayUtils.isSameLength(new boolean[] { true }, (boolean[]) null));
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
        Assert.assertTrue(Arrays.equals(new boolean[] {}, ArrayUtils.subarray(new boolean[] { true, false }, 0, 0)));
        Assert.assertTrue(Arrays.equals(new boolean[] { true }, ArrayUtils.subarray(new boolean[] { true, false }, 0, 1)));
        Assert.assertTrue(Arrays.equals(new boolean[] { false }, ArrayUtils.subarray(new boolean[] { true, false }, 1, 2)));
        Assert.assertTrue(Arrays.equals(new boolean[] { true, false }, ArrayUtils.subarray(new boolean[] { true, false }, 0, 2)));
        Assert.assertTrue(Arrays.equals(new boolean[] { true, false }, ArrayUtils.subarray(new boolean[] { true, false }, -1, 3)));
        Assert.assertTrue(Arrays.equals(new boolean[] {}, ArrayUtils.subarray(new boolean[] { true, false }, 2, 1)));
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
        Assert.assertTrue(Arrays.equals(booleanArray, newBooleanArray));
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
        Assert.assertNull(ArrayUtils.addAll((Object[]) null, (Object[]) null));
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.addAll(new String[] { "a" }, null));
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.addAll(null, new String[] { "a" }));
        Assert.assertArrayEquals(new String[] { "a", "b" }, ArrayUtils.addAll(new String[] { "a" }, new String[] { "b" }));
    }

    @Test
    public void addCoverageTest() {
        // add(Object[] array, int index, Object element)
        Assert.assertArrayEquals(new Object[] { null }, ArrayUtils.add((Object[]) null, 0, null));
        Assert.assertArrayEquals(new String[] { "a" }, ArrayUtils.add((Object[]) null, 0, "a"));
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
            ArrayUtils.add((Object[]) null, 1, "a");
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
