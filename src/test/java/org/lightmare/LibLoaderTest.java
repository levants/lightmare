package org.lightmare;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.loader.LoaderTester;

public class LibLoaderTest {

    public static Class<?> loaderTesterClass;

    public static Method loadMessage;

    public static final String CLASS_NAME_TO_LOAD = "org.lightmare.loadertester.LoaderTesterImpl";

    @BeforeClass
    public static void start() {
	try {
	    String libPath = "./lib/loader-tester.jar";
	    LibraryLoader.loadLibraries(libPath);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    // @Ignore
    @Test
    public void loadMessageTest() {
	try {
	    loaderTesterClass = Class.forName(CLASS_NAME_TO_LOAD, true, Thread
		    .currentThread().getContextClassLoader());
	    loadMessage = loaderTesterClass.getDeclaredMethod("loaderMessage");
	    Object tester = loaderTesterClass.newInstance();
	    Assert.assertNotNull("Could not initialize LoaderTester", tester);
	    loadMessage.invoke(tester);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Ignore
    public void loadMessageWithoutInitTest() {
	try {
	    loaderTesterClass = Class.forName(CLASS_NAME_TO_LOAD);
	    loadMessage = loaderTesterClass.getDeclaredMethod("loaderMessage");
	    Object tester = loaderTesterClass.newInstance();
	    Assert.assertNotNull("Could not initialize LoaderTester", tester);
	    loadMessage.invoke(tester);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    // @Ignore
    @Test
    public void loadMessageDirectTest() {
	try {
	    loaderTesterClass = Class.forName(CLASS_NAME_TO_LOAD, true, Thread
		    .currentThread().getContextClassLoader());
	    LoaderTester tester = (LoaderTester) loaderTesterClass
		    .newInstance();
	    Assert.assertNotNull("Could not initialize LoaderTester", tester);
	    tester.loaderMessage();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    // @Ignore
    @Test
    public void loadMessageDirectWithoutInitTest() {
	try {
	    loaderTesterClass = Class.forName(CLASS_NAME_TO_LOAD);
	    LoaderTester tester = (LoaderTester) loaderTesterClass
		    .newInstance();
	    Assert.assertNotNull("Could not initialize LoaderTester", tester);
	    tester.loaderMessage();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }
}
