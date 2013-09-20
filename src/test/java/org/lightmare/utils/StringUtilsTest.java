package org.lightmare.utils;

import org.junit.Assert;
import org.junit.Test;
import org.lightmare.config.ConfigKeys;

public class StringUtilsTest {

    @Test
    public void arrayInstanceTest() {

	int[] intArray = new int[100];
	long[] longArray = new long[100];
	Integer[] integerArray = new Integer[100];
	String[] stringArray = new String[100];
	ConfigKeys[] enumArray = ConfigKeys.values();

	boolean instance;

	instance = CollectionUtils.isArray(intArray);
	Assert.assertTrue(
		String.format("Array %s is not instanceof Object[]",
			int.class.getSimpleName()), instance);

	instance = CollectionUtils.isArray(longArray);
	Assert.assertTrue(
		String.format("Array %s is not instanceof Object[]",
			long.class.getSimpleName()), instance);

	instance = CollectionUtils.isArray(integerArray);
	Assert.assertTrue(String.format("Array %s is not instanceof Object[]",
		Integer.class.getSimpleName()), instance);

	instance = CollectionUtils.isArray(stringArray);
	Assert.assertTrue(String.format("Array %s is not instanceof Object[]",
		String.class.getSimpleName()), instance);

	instance = CollectionUtils.isArray(enumArray);
	Assert.assertTrue(String.format("Array %s is not instanceof Object[]",
		ConfigKeys.class.getSimpleName()), instance);

	CharSequence sequence = new StringBuilder();
	instance = CollectionUtils.isArray(sequence);
	Assert.assertTrue(String.format("Array %s is not instanceof Object[]",
		CharSequence.class.getSimpleName()), !instance);
    }

    @Test
    public void concatTest() {

	int[] intArray = new int[3];
	intArray[0] = 7;
	intArray[1] = 8;
	intArray[2] = 9;

	long[] longArray = new long[3];
	longArray[0] = 10;
	longArray[1] = 11;
	longArray[2] = 12;

	char[] charArray = new char[3];
	charArray[0] = 13;
	charArray[1] = 14;
	charArray[2] = 15;

	String[] stringArray = new String[4];
	stringArray[0] = String.valueOf(16);
	stringArray[1] = String.valueOf(17);
	stringArray[2] = String.valueOf(18);
	stringArray[3] = String.valueOf(19);

	ConfigKeys[] enumArray = ConfigKeys.values();

	try {
	    String text = StringUtils.concatRecursively("1, 2, 3\n",
		    ", 4, 5, 6\n", intArray, longArray, charArray, stringArray,
		    enumArray);

	    System.out.println(text);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
