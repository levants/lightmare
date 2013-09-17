package org.lightmare.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {

    @Test
    public void toArrayTest() {

	try {
	    String[] strings = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
		    "10" };
	    List<String> list = new ArrayList<String>(Arrays.asList(strings));

	    String[] strings1 = new String[list.size()];
	    strings1 = list.toArray(strings1);
	    String[] strings2 = CollectionUtils.toArray(list, String.class);

	    Assert.assertArrayEquals("Arrays do not match", strings1, strings2);

	    String homePath = System.getProperty("user.home");
	    File file = new File(homePath);

	    URL[] urls = { file.toURI().toURL() };
	    List<URL> urlList = new ArrayList<URL>(Arrays.asList(urls));

	    URL[] urls1 = new URL[urlList.size()];
	    urls1 = urlList.toArray(urls1);
	    URL[] urls2 = CollectionUtils.toArray(urlList, URL.class);

	    Assert.assertArrayEquals("Arrays do not match", urls1, urls2);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void castTest() {

	try {
	    String line = null;
	    Object toCast = line;
	    String utilCasted = ObjectUtils.cast(toCast);
	    String directCasted = (String) toCast;

	    System.out.format("%s %s\n", utilCasted, directCasted);

	    toCast = line;
	    String utilTypedCasted = ObjectUtils.cast(toCast, String.class);

	    System.out.format("%s %s\n", utilTypedCasted, directCasted);

	    int toICast = 100;
	    int utilICasted = ObjectUtils.cast(toICast);
	    int directIcasted = (int) toICast;

	    System.out.format("%s %s\n", utilICasted, directIcasted);

	    int toITypedCast = 100;
	    int utilITypedCasted = ObjectUtils
		    .cast(toITypedCast, Integer.class);

	    System.out.format("%s %s\n", utilITypedCasted, directIcasted);

	    System.out.format("%s %s\n", int.class, Integer.class);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
