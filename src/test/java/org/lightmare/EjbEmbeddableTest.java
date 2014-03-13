package org.lightmare;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;

import org.lightmare.ejb.embeddable.EJBContainerProvider;

public class EjbEmbeddableTest {

    public void ejbEmbeddableTest() {

	Map<Object, Object> properties = new HashMap<Object, Object>();
	properties.put(EJBContainer.PROVIDER,
		EJBContainerProvider.class.getSimpleName());
	EJBContainer.createEJBContainer(properties);
    }
}
