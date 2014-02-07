package org.lightmare.utils.fs.codecs;

import java.net.URI;
import java.net.URL;
import java.util.zip.ZipFile;

public class JarUtilsTest {

    public static String URI_TEXT = "./lib/loader-tester.jar";

    public void checkUriTest() {

	try {
	    URL url = new URL(URI_TEXT);
	    URI uri = url.toURI();
	    String path = uri.getSchemeSpecificPart();
	    System.out.println(path);
	    ZipFile zipFile = new ZipFile(path);
	    zipFile.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
