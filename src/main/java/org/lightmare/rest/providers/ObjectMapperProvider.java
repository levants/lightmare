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
package org.lightmare.rest.providers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.lightmare.utils.io.serialization.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link ContextResolver} for {@link ObjectMapper} for <a
 * href="https://github.com/FasterXML/jackson-core">jackson-jaxrs-providers</a>
 * library
 * 
 * @author Levan Tsinadze
 * @since 0.0.50-SNAPSHOT
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    // Default JSON object converter
    private final ObjectMapper defaultObjectMapper;

    public ObjectMapperProvider() {
	// Gets appropriate ObjectMapper instance from JsonSerializer class
	defaultObjectMapper = JsonSerializer.getMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
	return defaultObjectMapper;
    }
}
