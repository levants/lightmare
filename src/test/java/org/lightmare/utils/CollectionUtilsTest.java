package org.lightmare.utils;

import java.util.Arrays;

import org.junit.Test;

public class CollectionUtilsTest {

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
