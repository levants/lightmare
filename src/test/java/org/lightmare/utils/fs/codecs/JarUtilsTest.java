package org.lightmare.utils.fs.codecs;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;

import org.junit.Test;

public class JarUtilsTest {

    public static String URI_TEXT = "./lib/loader-tester.jar";

    public static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

    @Test
    public void checkUriTest() {

	try {
	    File file = new File(URI_TEXT);
	    URI uri = file.toURI();
	    String path = uri.getSchemeSpecificPart();
	    System.out.println(path);

	    boolean isZip = true;
	    byte[] buffer = new byte[MAGIC.length];
	    try {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		raf.readFully(buffer);
		for (int i = 0; i < MAGIC.length; i++) {
		    if (buffer[i] != MAGIC[i]) {
			isZip = false;
			break;
		    }
		}
		raf.close();
	    } catch (Throwable e) {
		isZip = false;
	    }
	    System.out.println(isZip);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
