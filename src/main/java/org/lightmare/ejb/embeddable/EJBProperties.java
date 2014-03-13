package org.lightmare.ejb.embeddable;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;

import org.lightmare.utils.ObjectUtils;

/**
 * Property for initializing {@link javax.ejb.embeddable.EJBContainer#PROVIDER}
 * property
 * 
 * @author Levan Tsinadze
 * @since 0.1.1
 */
public class EJBProperties {

    // Name of EJB container provider class "Lightmare" implementation
    public static final String PROVIDER_CLASS = EJBContainerProvider.class
	    .getName();

    /**
     * Adds {@link javax.ejb.embeddable.EJBContainer#PROVIDER} property value to
     * passed {@link Map} of properties
     * 
     * @param properties
     */
    public static void addProvider(Map<?, ?> properties) {

	Map<Object, Object> propertiesMap = ObjectUtils.cast(properties);
	propertiesMap.put(EJBContainer.PROVIDER, PROVIDER_CLASS);
    }

    /**
     * Creates {@link Map} of properties and adds
     * {@link javax.ejb.embeddable.EJBContainer#PROVIDER} property value
     * 
     * @return {@link Map} properties with
     *         {@link javax.ejb.embeddable.EJBContainer#PROVIDER} value
     */
    public static Map<?, ?> createProperties() {

	Map<?, ?> properties = new HashMap<Object, Object>();

	addProvider(properties);

	return properties;
    }
}
