package org.lightmare.ejb.embeddable;

import java.util.Map;
import java.util.ServiceLoader;

import javax.ejb.embeddable.EJBContainer;
import javax.ejb.spi.EJBContainerProvider;

import org.junit.Test;

//@Ignore
public class EJBPropertiesTest {

    private static final String DS_PATH = "./ds/standalone.xml";
    private static final String UNIT_NAME = "testUnit";

    @Test
    public void readContainerTest() {

	try {
	    Map<?, ?> properties = EJBPropertiesEnumForTest.INSTANCE
		    .getProperties(DS_PATH, UNIT_NAME);
	    EJBProperties.addProvider(properties);
	    System.out.println(properties.get(EJBContainer.PROVIDER));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void createContainerTest() {

	try {
	    Map<?, ?> properties = EJBPropertiesEnumForTest.INSTANCE
		    .getProperties(DS_PATH, UNIT_NAME);
	    EJBProperties.addProvider(properties);
	    System.out.println(properties.get(EJBContainer.PROVIDER));
	    EJBContainer.createEJBContainer(properties);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void findClassTest() {

	try {
	    ServiceLoader<EJBContainerProvider> loader = ServiceLoader
		    .load(EJBContainerProvider.class);
	    loader.reload();
	    for (EJBContainerProvider provider : loader) {
		System.out.println(provider.getClass().getName());
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
