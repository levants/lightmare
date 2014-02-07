/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.rest.providers;

import org.lightmare.rest.RestConfig;

/**
 * Default values for REST service application initialization
 * 
 * @author Levan Tsinadze
 * @since 0.0.56-SNAPSHOT
 */
public enum ApplicationInit {

    // Default parameters for REST service configuration
    INIT_PARAM("javax.ws.rs.Application", RestConfig.class.getName()), // Initializer

    REST_DEFAULT_URI("/rest"); // URL path

    public String key;

    public String value;

    private ApplicationInit(String key) {
	this.key = key;
    }

    private ApplicationInit(String key, String value) {
	this(key);
	this.value = value;
    }
}
