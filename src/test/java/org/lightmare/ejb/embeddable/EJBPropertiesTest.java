package org.lightmare.ejb.embeddable;

import java.util.Map;

import javax.ejb.embeddable.EJBContainer;

import org.junit.Test;

public class EJBPropertiesTest {

    private static final String DS_PATH = "./ds/standalone.xml";
    private static final String UNIT_NAME = "testUnit";

    @Test
    public void readContainerTest() {

	Map<?, ?> properties = EJBPropertiesEnumForTest.INSTANCE.getProperties(
		"", "");
	EJBProperties.addProvider(properties);

	System.out.println(properties.get(EJBContainer.PROVIDER));
    }

    @Test
    public void createContainerTest() {

	Map<?, ?> properties = EJBPropertiesEnumForTest.INSTANCE.getProperties(
		DS_PATH, UNIT_NAME);
	EJBProperties.addProvider(properties);

	System.out.println(properties.get(EJBContainer.PROVIDER));

	try {
	    EJBContainer.createEJBContainer(properties);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
