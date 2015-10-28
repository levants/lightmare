package org.lightmare.utils.libraries;

import org.junit.Test;

public class ClassLoaderUtilsTest {

    @Test
    public void javaVedotCheckTest() {

	try {
	    System.out.println();
	    System.out
		    .println("==========JVM and Java Platform properties==================");
	    System.out.println();
	    System.out.println("=========================================");
	    System.out.println("=========================================");

	    System.out.println(System.getProperty("java.vendor"));
	    System.out.println(System.getProperty("java.vendor.url"));
	    System.out.println(System.getProperty("java.version"));
	    System.out.println(System.getProperty("java.vm.vendor"));

	    System.out.println("=========================================");
	    System.out.println("=========================================");
	    System.out.println("=========================================");

	    System.out.println(System.getProperty("os.arch"));

	    System.out.println();
	    System.out
		    .println("==========JVM and Java Platform properties==================");
	    System.out.println();
	    System.out.println("=========================================");
	    System.out.println();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
