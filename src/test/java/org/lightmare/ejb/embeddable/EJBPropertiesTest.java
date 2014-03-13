package org.lightmare.ejb.embeddable;

import java.util.Map;

import javax.ejb.embeddable.EJBContainer;

import org.junit.Test;

public class EJBPropertiesTest {

    @Test
    public void createContainerTest() {

	Map<?, ?> properties = EJBPropertiesEnumForTest.INSTANCE.getProperties(
		"", "");
	EJBProperties.addProvider(properties);

	System.out.println(properties.get(EJBContainer.PROVIDER));
    }
}
