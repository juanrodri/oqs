/*
 * $Id$
 *
 * Copyright 2006-2008 Alex Lin. All rights reserved.
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
package org.opoo.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 数组操作助手类。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class ArrayUtils {
    public static final String[] EMPTY_STRING_ARRAY = {};
    public static final int[] EMPTY_INT_ARRAY = {};
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};
    public static final Class[] EMPTY_CLASS_ARRAY = {};
    public static final Object[] EMPTY_OBJECT_ARRAY = {};

    /**
     * 连接2个数组。
     * @param array1 Object[]
     * @param array2 Object[]
     * @return Object[]
     */
    public static Object[] concat(Object[] array1, Object[] array2) {
	int length = array1.length + array2.length;
	if (length == 0) {
	    return EMPTY_OBJECT_ARRAY;
	}
	if (array1.length == 0) {
	    return array2;
	}
	if (array2.length == 0) {
	    return array1;
	}
	Object[] a = new Object[length];
	arraycopy(a, array1, array1.length, array2, array2.length);
	return a;
    }

    /**
     * 连接2个字符串数组。
     * @param array1 String[]
     * @param array2 String[]
     * @return String[]
     */
    public static String[] concat(String[] array1, String[] array2) {
	int length = array1.length + array2.length;
	if (length == 0) {
	    return EMPTY_STRING_ARRAY;
	}
	if (array1.length == 0) {
	    return array2;
	}
	if (array2.length == 0) {
	    return array1;
	}
	String[] a = new String[length];
	arraycopy(a, array1, array1.length, array2, array2.length);
	return a;
    }

    /**
     * 连接2个整形数组。
     * @param array1 int[]
     * @param array2 int[]
     * @return int[]
     */
    public static int[] concat(int[] array1, int[] array2) {
	int length = array1.length + array2.length;
	if (length == 0) {
	    return EMPTY_INT_ARRAY;
	}
	if (array1.length == 0) {
	    return array2;
	}
	if (array2.length == 0) {
	    return array1;
	}
	int[] a = new int[length];
	arraycopy(a, array1, array1.length, array2, array2.length);
	return a;
    }

    /**
     * 连接2个boolean形数组。
     * @param array1 boolean[]
     * @param array2 boolean[]
     * @return boolean[]
     */
    public static boolean[] concat(boolean[] array1, boolean[] array2) {
	int length = array1.length + array2.length;
	if (length == 0) {
	    return EMPTY_BOOLEAN_ARRAY;
	}
	if (array1.length == 0) {
	    return array2;
	}
	if (array2.length == 0) {
	    return array1;
	}
	boolean[] a = new boolean[length];
	arraycopy(a, array1, array1.length, array2, array2.length);
	return a;
    }

    private static void arraycopy(Object dest, Object src1, int length1,
				  Object src2, int length2) {
	System.arraycopy(src1, 0, dest, 0, length1);
	System.arraycopy(src2, 0, dest, length1, length2);
    }

    /**
     * 连接两个有type指定的类型的数组。
     * @param array1 Object
     * @param array2 Object
     * @param type Class
     * @return Object
     */
    public static Object concat(Object array1, Object array2, Class type) {
	int length1 = Array.getLength(array1);
	int length2 = Array.getLength(array2);
	int length = length1 + length2;
	if (length == 0) {
	    return Array.newInstance(type, 0);
	}
	if (length1 == 0) {
	    return array2;
	}
	if (length2 == 0) {
	    return array1;
	}
	Object dest = Array.newInstance(type, length);
	System.arraycopy(array1, 0, dest, 0, length1);
	System.arraycopy(array2, 0, dest, length1, length2);
	return dest;
    }


    /**
     * 根据use返回要使用的项。
     * @param values Object[]
     * @param use boolean[]
     * @return Object[]
     */
    public static Object[] usedArray(Object[] values, boolean[] use) {
	if (use == null) {
	    return values;
	}

	if (values.length != use.length) {
	    throw new IllegalArgumentException("Array length not match.");
	}

	List list = new ArrayList();
	for (int i = 0; i < values.length; i++) {
	    if (use[i]) {
		list.add(values[i]);
	    }
	}
	return list.toArray(EMPTY_OBJECT_ARRAY);
    }

    /**
     * 根据use返回要使用的项。
     * @param values String[]
     * @param use boolean[]
     * @return String[]
     */
    public static String[] usedArray(String[] values, boolean[] use) {
	if (use == null) {
	    return values;
	}

	if (values.length != use.length) {
	    throw new IllegalArgumentException("Array length not match.");
	}

	List list = new ArrayList();
	for (int i = 0; i < values.length; i++) {
	    if (use[i]) {
		list.add(values[i]);
	    }
	}
	return (String[]) list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * 根据索引index从数组中查询一个对象。如果数组为空或者索引越界（包括小于0）
     * 则返回null，不会出现NullPointerException。
     * @param array Object[]
     * @param index int
     * @return Object
     */
    public static Object get(Object[] array, int index) {
	if (array == null || index >= array.length || index < 0) {
	    return null;
	} else {
	    return array[index];
	}
    }


    /**
     * 将数组转化成字符串数组。
     * @param objects Object[]
     * @return String[]
     */
    public static String[] toStringArray(Object[] objects) {
	int length = objects.length;
	String[] result = new String[length];
	for (int i = 0; i < length; i++) {
	    result[i] = objects[i].toString();
	}
	return result;
    }

    /**
     * 填充一个字符串数组。
     * @param value String
     * @param length int
     * @return String[]
     */
    public static String[] fillArray(String value, int length) {
	String[] result = new String[length];
	Arrays.fill(result, value);
	return result;
    }

    /**
     * 填充一个整形数组。
     * @param value int
     * @param length int
     * @return int[]
     */
    public static int[] fillArray(int value, int length) {
	int[] result = new int[length];
	Arrays.fill(result, value);
	return result;
    }

    /**
     * 将一个集合转化成一个字符串数组。
     * @param coll Collection
     * @return String[]
     */
    public static String[] toStringArray(Collection coll) {
	return (String[]) coll.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     *
     * @param coll Collection
     * @return String[][]
     */
    public static String[][] to2DStringArray(Collection coll) {
	return (String[][]) coll.toArray(new String[coll.size()][]);
    }

    public static int[][] to2DIntArray(Collection coll) {
	return (int[][]) coll.toArray(new int[coll.size()][]);
    }

    public static int[] toIntArray(Collection coll) {
	Iterator iter = coll.iterator();
	int[] arr = new int[coll.size()];
	int i = 0;
	while (iter.hasNext()) {
	    arr[i++] = ((Integer) iter.next()).intValue();
	}
	return arr;
    }

    public static boolean[] toBooleanArray(Collection coll) {
	Iterator iter = coll.iterator();
	boolean[] arr = new boolean[coll.size()];
	int i = 0;
	while (iter.hasNext()) {
	    arr[i++] = ((Boolean) iter.next()).booleanValue();
	}
	return arr;
    }

    public static Object[] typecast(Object[] array, Object[] to) {
	return java.util.Arrays.asList(array).toArray(to);
    }

    //Arrays.asList doesn't do primitive arrays
    public static List toList(Object array) {
	if (array instanceof Object[])
	    return Arrays.asList((Object[]) array); //faster?
	int size = Array.getLength(array);
	ArrayList list = new ArrayList(size);
	for (int i = 0; i < size; i++) {
	    list.add(Array.get(array, i));
	}
	return list;
    }

    public static String[] slice(String[] strings, int begin, int length) {
	String[] result = new String[length];
	for (int i = 0; i < length; i++) {
	    result[i] = strings[begin + i];
	}
	return result;
    }

    public static Object[] slice(Object[] objects, int begin, int length) {
	Object[] result = new Object[length];
	for (int i = 0; i < length; i++) {
	    result[i] = objects[begin + i];
	}
	return result;
    }

    public static List toList(Iterator iter) {
	List list = new ArrayList();
	while (iter.hasNext()) {
	    list.add(iter.next());
	}
	return list;
    }

    public static String[] join(String[] x, String[] y) {
	String[] result = new String[x.length + y.length];
	for (int i = 0; i < x.length; i++)
	    result[i] = x[i];
	for (int i = 0; i < y.length; i++)
	    result[i + x.length] = y[i];
	return result;
    }

    public static String[] join(String[] x, String[] y, boolean[] use) {
	String[] result = new String[x.length + countTrue(use)];
	for (int i = 0; i < x.length; i++)
	    result[i] = x[i];
	int k = x.length;
	for (int i = 0; i < y.length; i++) {
	    if (use[i])
		result[k++] = y[i];
	}
	return result;
    }

    public static int[] join(int[] x, int[] y) {
	int[] result = new int[x.length + y.length];
	for (int i = 0; i < x.length; i++)
	    result[i] = x[i];
	for (int i = 0; i < y.length; i++)
	    result[i + x.length] = y[i];
	return result;
    }


    public static boolean isAllNegative(int[] array) {
	for (int i = 0; i < array.length; i++) {
	    if (array[i] >= 0)
		return false;
	}
	return true;
    }

    public static boolean isAllTrue(boolean[] array) {
	for (int i = 0; i < array.length; i++) {
	    if (!array[i])
		return false;
	}
	return true;
    }

    public static boolean isAllNotNull(Object[] array) {
	for (int i = 0; i < array.length; i++) {
	    if (array[i] == null)
		return false;
	}
	return true;
    }

    public static boolean isAllNotNull(Object array) {
	int n = Array.getLength(array);
	for (int i = 0; i < n; i++) {
	    if (Array.get(array, i) == null)
		return false;
	}
	return true;
    }

    public static boolean isAllNull(Object[] array) {
	for (int i = 0; i < array.length; i++) {
	    if (array[i] != null)
		return false;
	}
	return true;
    }

    public static int countTrue(boolean[] array) {
	int result = 0;
	for (int i = 0; i < array.length; i++) {
	    if (array[i])
		result++;
	}
	return result;
    }

    public static boolean isAllFalse(boolean[] array) {
	for (int i = 0; i < array.length; i++) {
	    if (array[i])
		return false;
	}
	return true;
    }

    public static void addAll(Collection collection, Object[] array) {
	for (int i = 0; i < array.length; i++) {
	    collection.add(array[i]);
	}
    }

    /**
     * Compare 2 arrays only at the first level
     * @param o1 char[]
     * @param o2 char[]
     * @return boolean
     */
    public static boolean isEquals(char[] o1, char[] o2) {
	if (o1 == o2) {
	    return true;
	}
	if (o1 == null || o2 == null) {
	    return false;
	}
	int length = o1.length;
	if (length != o2.length)
	    return false;
	for (int index = 0; index < length; index++) {
	    if (!(o1[index] == o2[index])) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Compare 2 arrays only at the first level
     * @param b1 byte[]
     * @param b2 byte[]
     * @return boolean
     */
    public static boolean isEquals(byte[] b1, byte[] b2) {
	if (b1 == b2)
	    return true;
	if (b1 == null || b2 == null)
	    return false;
	int length = b1.length;
	if (length != b2.length) {
	    return false;
	}
	for (int index = 0; index < length; index++) {
	    if (!(b1[index] == b2[index])) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Return whether the given array is empty: that is, <code>null</code>
     * or of zero length.
     * @param array the array to check
     * @return whether the given array is empty
     */
    public static boolean isEmpty(Object[] array) {
	return (array == null || array.length == 0);
    }
}
