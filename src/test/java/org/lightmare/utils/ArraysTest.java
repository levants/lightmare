package org.lightmare.utils;

import java.net.URL;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ArraysTest {

    private Class<String> stringType = String.class;

    private Class<Integer> integerType = Integer.class;

    private Class<Long> longType = Long.class;

    private Class<URL> urlType = URL.class;

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

	String[] strings = ObjectUtils.emptyArray(stringType);
	print(strings);

	Integer[] integers = ObjectUtils.emptyArray(integerType);
	print(integers);

	Long[] longs = ObjectUtils.emptyArray(longType);
	print(longs);

	URL[] urls = ObjectUtils.emptyArray(urlType);
	print(urls);
    }
}
