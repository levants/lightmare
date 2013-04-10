package org.lightmare.utils.fs;

import java.io.File;
import java.net.URL;

import org.junit.Test;

public class WatchUtilsTest {

    @Test
    public void clearURLTest() {

	try {

	    String homePath = System.getProperty("user.home");
	    File file = new File(homePath);

	    URL url = file.toURI().toURL();
	    System.out.println(url);
	    URL clear = WatchUtils.clearURL(url);
	    System.out.println(clear);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
