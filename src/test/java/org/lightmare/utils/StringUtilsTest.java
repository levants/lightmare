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
}
