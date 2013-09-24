package org.lightmare;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.lightmare.scannotation.AnnotationDB;
import org.lightmare.utils.ears.AbstractIOUtils;

public class IOUtilsTest {

    private static final String EAR_PATH = "./lib/loader-tester.ear";

    private static AnnotationDB annotationDB = new AnnotationDB();

    @Test
    public void scanTest() {
	try {
	    AbstractIOUtils ioUtils = AbstractIOUtils
		    .getAppropriatedType(new File(EAR_PATH).toURI().toURL());
	    ioUtils.scan(true);

	    Map<URL, URL> xmlURLs = ioUtils.getXmlURLs();
	    Assert.assertTrue("Could not find application ejb jar urls",
		    xmlURLs.size() > 0);
	    Set<URL> urlSet = xmlURLs.keySet();
	    URL[] urls = urlSet.toArray(new URL[urlSet.size()]);
	    annotationDB.scanArchives(urls);
	    Map<String, String> classOwnershipFiles = annotationDB
		    .getClassOwnersFiles();
	    Assert.assertTrue("Could not find class ownership files",
		    classOwnershipFiles.size() > 0);
	    System.out
		    .println("============Files of scanned classes ================");
	    for (Map.Entry<String, String> entry : classOwnershipFiles
		    .entrySet()) {
		System.out.format("%s ------ %s\n", entry.getKey(),
			entry.getValue());
	    }
	    System.out
		    .println("====================================================");

	} catch (MalformedURLException ex) {
	    ex.printStackTrace();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
