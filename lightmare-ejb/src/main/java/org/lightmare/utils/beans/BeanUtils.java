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
package org.lightmare.utils.beans;

import javax.ejb.Stateless;

import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class for EJB beans
 *
 * @author Levan Tsinadze
 * @since 0.0.26-SNAPSHOT
 */
public class BeanUtils {

    // Suffixes of local and remote interface names
    private static final String REMOTE_IDENT = "Remote";

    private static final String LOCAL_IDENT = "Local";

    /**
     * Retrieves bean name from class name
     *
     * @param name
     * @return String
     */
    public static String parseName(String name) {

	String simpleName = name;

	int index = name.lastIndexOf(StringUtils.DOT);
	if (index > StringUtils.NOT_EXISTING_INDEX) {
	    index++;
	    simpleName = name.substring(index);
	}

	return simpleName;
    }

    /**
     * Removes <b>Remote</b> or <b>Local</b> part from bean interface name
     *
     * @param interfaceClass
     * @return {@link String} EJB bean name
     */
    public static String nameFromInterface(Class<?> interfaceClass) {

	String beanName;

	String interfaceName = interfaceClass.getSimpleName();
	int start;
	if (interfaceName.endsWith(REMOTE_IDENT)) {
	    start = interfaceName.lastIndexOf(REMOTE_IDENT);
	    beanName = interfaceName.substring(CollectionUtils.FIRST_INDEX, start);
	} else if (interfaceName.endsWith(LOCAL_IDENT)) {
	    start = interfaceName.lastIndexOf(LOCAL_IDENT);
	    beanName = interfaceName.substring(CollectionUtils.FIRST_INDEX, start);
	} else {
	    beanName = interfaceName;
	}

	return beanName;
    }

    /**
     * Gets EJB bean name from passed {@link Class} instance
     *
     * @param beanClass
     * @return {@link String} EJB bean name
     */
    public static String beanName(Class<?> beanClass) {

	String beanEjbName;

	Stateless annotation = beanClass.getAnnotation(Stateless.class);
	beanEjbName = annotation.name();
	if (StringUtils.invalid(beanEjbName)) {
	    beanEjbName = beanClass.getSimpleName();
	}

	return beanEjbName;
    }
}
