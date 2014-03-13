package org.lightmare.ejb.embeddable;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;

import org.lightmare.utils.ObjectUtils;

public class EjbProperties {

    public static final String PROVIDER_CLASS = EJBContainerProviderImpl.class
	    .getName();

    public static void addProvider(Map<?, ?> properties) {

	Map<Object, Object> propertiesMap = ObjectUtils.cast(properties);
	propertiesMap.put(EJBContainer.PROVIDER, PROVIDER_CLASS);
    }

    public static Map<?, ?> createProperties() {

	Map<?, ?> properties = new HashMap<Object, Object>();

	addProvider(properties);

	return properties;
    }
}
