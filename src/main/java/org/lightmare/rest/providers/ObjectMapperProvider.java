package org.lightmare.rest.providers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link ContextResolver} for {@link ObjectMapper} jackson
 * library
 * 
 * @author levan
 * 
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper defaultObjectMapper;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ObjectMapperProvider() {

	defaultObjectMapper = MAPPER;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {

	return defaultObjectMapper;
    }

}
