package org.lightmare.utils;

import java.net.URL;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilsTest {

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

	String[] strings = CollectionUtils.emptyArray(String.class);
	print(strings);

	Integer[] integers = CollectionUtils.emptyArray(Integer.class);
	print(integers);

	Long[] longs = CollectionUtils.emptyArray(Long.class);
	print(longs);

	URL[] urls = CollectionUtils.emptyArray(URL.class);
	print(urls);
    }

    @Test
    public void arrayCastTest() {

	try {

	    boolean[] bools = new boolean[2];
	    bools[0] = false;
	    bools[1] = true;

	    boolean[] casted = ObjectUtils.cast(bools, boolean[].class);

	    System.out.println(Arrays.toString(casted));

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
