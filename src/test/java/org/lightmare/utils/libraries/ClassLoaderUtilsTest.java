package org.lightmare.utils.libraries;

import org.junit.Test;

public class ClassLoaderUtilsTest {

    @Test
    public void javaVedotCheckTest() {

	System.out.println(System.getProperty("java.vendor"));
	System.out.println(System.getProperty("java.vendor.url"));
	System.out.println(System.getProperty("java.version"));
	System.out.println(System.getProperty("java.vm.vendor"));
    }
}
