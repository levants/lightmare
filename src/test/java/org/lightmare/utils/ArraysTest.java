package org.lightmare.utils;

import java.net.URL;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ArraysTest {

    private String format = "%s %s %s\n";

    private <T> void print(T[] empty) {

	int length = empty.length;
	Class<?> arrayClass = empty.getClass();
	Assert.assertTrue(
		String.format("array of type %s is not empty", arrayClass),
		length == 0);
	System.out.format(format, arrayClass, Arrays.toString(empty), length);
    }

    @Test
    public void emptyArrayTest() {

	String[] strings = ObjectUtils.emptyArray(String.class);
	print(strings);

	Integer[] integers = ObjectUtils.emptyArray(Integer.class);
	print(integers);

	Long[] longs = ObjectUtils.emptyArray(Long.class);
	print(longs);

	URL[] urls = ObjectUtils.emptyArray(URL.class);
	print(urls);
    }
}
