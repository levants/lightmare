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
package org.lightmare.utils.io.serialization;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

/**
 * Reads write java objects from {@link Byte} array or {@link String} with java
 * JSON serialization vendor specific JSON library
 * 
 * @author Levan Tsinadze
 * @since 0.0.62-SNAPSHOT
 */
public abstract class JsonSerializer {

    /**
     * Enumeration to initialize and configure single {@link ObjectMapper}
     * instance
     * 
     * @author Levan Tsinadze
     * @since 0.1.1
     */
    protected static enum JsonMapper {

	MAPPER; // Single instance of converter container class

	// Converter class
	protected final ObjectMapper objectMapper = new ObjectMapper();

	private JsonMapper() {
	    objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new AfterburnerModule())
		    .registerModule(new Hibernate4Module());
	}
    }

    /**
     * Configures {@link ObjectMapper} if it is not configured
     * 
     * @return {@link ObjectMapper}
     */
    public static ObjectMapper getMapper() {
	return JsonMapper.MAPPER.objectMapper;
    }

    /**
     * Serializes {@link Object} to JSON {@link String} with
     * <a href="https://github.com/FasterXML/jackson-databind">jackson API</a>
     * 
     * @param value
     * @return {@link String}
     * @throws IOException
     */
    public static String write(Object value) throws IOException {

	String data;

	try {
	    data = getMapper().writeValueAsString(value);
	} catch (JsonGenerationException ex) {
	    throw new IOException(ex);
	} catch (JsonMappingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}

	return data;
    }

    /**
     * For deserialization of JSON {@link String} to {@link Object} with
     * <a href="https://github.com/FasterXML/jackson-databind">jackson api</a>
     * 
     * @param data
     * @param valueClass
     * @return T
     * @throws IOException
     */
    public static <T> T read(String data, Class<T> valueClass) throws IOException {

	T value;

	try {
	    value = getMapper().readValue(data, valueClass);
	} catch (JsonGenerationException ex) {
	    throw new IOException(ex);
	} catch (JsonMappingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}

	return value;
    }

    /**
     * For deserialization of JSON {@link InputStream} to {@link Object} with
     * <a href="https://github.com/FasterXML/jackson-databind">jackson API</a>
     * 
     * @param stream
     * @param valueClass
     * @return T from JSON
     * @throws IOException
     */
    public static <T> T read(InputStream stream, Class<T> valueClass) throws IOException {
	return getMapper().readValue(stream, valueClass);
    }

    /**
     * For deserialization of JSON (<code>byte[]</code>) to {@link Object} with
     * <a href="https://github.com/FasterXML/jackson-databind">jackson API</a>
     * 
     * @param bytes
     * @param valueClass
     * @return T value from JSON
     * @throws IOException
     */
    public static <T> T read(byte[] bytes, Class<T> valueClass) throws IOException {
	return getMapper().readValue(bytes, valueClass);
    }
}
