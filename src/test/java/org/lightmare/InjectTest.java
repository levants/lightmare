package org.lightmare;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.bean.InjectMainBeanRemote;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.ejb.startup.MetaCreator;

@Ignore
public class InjectTest {

    private InjectMainBeanRemote bean;

    @BeforeClass
    public static void init() {

	MetaCreator.Builder builder = new MetaCreator.Builder();
	MetaCreator creator = builder.build();
	try {
	    URL url = InjectMainBeanRemote.class.getClassLoader().getResource(
		    "");
	    URL[] urls = { url };
	    creator.scanForBeans(urls);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    @Before
    public void start() {

	EjbConnector connector = new EjbConnector();
	try {
	    bean = connector.connectToBean("InjectMainBean",
		    InjectMainBeanRemote.class);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void testSimpleInjection() {

	String injectText = bean.getFromInjection();
	Assert.assertTrue("Could not inject bean", injectText != null
		&& !injectText.isEmpty());

	System.out.println(injectText);
    }
}
