package org.lightmare.rest.providers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.lightmare.utils.serialization.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link ContextResolver} for {@link ObjectMapper} for <a
 * href="https://github.com/FasterXML/jackson-core">jackson-jaxrs-providers</a>
 * library
 * 
 * @author levan
 * 
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    //Default JSON object mapper
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
