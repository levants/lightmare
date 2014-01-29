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
