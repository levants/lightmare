/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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

    // Name of EJB container provider class implementation
    public static final String PROVIDER_CLASS = EmbeddableContainerProvider.class.getName();

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
