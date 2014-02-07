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
package org.lightmare.utils.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Reads write java objects from {@link Byte} array or {@link String} with java
 * JSON serialization vendor specific JSON library
 * 
 * @author Levan Tsinadze
 * @since 0.0.62-SNAPSHOT
 */
public abstract class JsonSerializer {

    // Converter class
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Check for configuration of ObjectMapper instance
    private static boolean mapperConfigured;

    // Lock for initializing ObjectMapper instance
    private static final Lock LOCK = new ReentrantLock();

    private static void configureMapper() {

	ObjectUtils.lock(LOCK);
	try {
	    if (ObjectUtils.notTrue(mapperConfigured)) {
		MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapperConfigured = Boolean.TRUE;
	    }
	} finally {
	    ObjectUtils.unlock(LOCK);
	}
    }

    /**
     * Configures {@link ObjectMapper} if {@link RpcUtils#mapperConfigured} is
     * <code>false</code>
     * 
     * @return {@link ObjectMapper}
     */
    public static ObjectMapper getMapper() {

	if (ObjectUtils.notTrue(mapperConfigured)) {
	    configureMapper();
	}

	return MAPPER;
    }

    /**
     * Serializes {@link Object} to JSON {@link String} with <a
     * href="https://github.com/FasterXML/jackson-databind">jackson api</a>
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
     * De-serializes JSON {@link String} to {@link Object} with <a
     * href="https://github.com/FasterXML/jackson-databind">jackson api</a>
     * 
     * @param data
     * @param valueClass
     * @return T
     * @throws IOException
     */
    public static <T> T read(String data, Class<T> valueClass)
	    throws IOException {

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
     * Deserializes JSON {@link InputStream} to {@link Object} with <a
     * href="https://github.com/FasterXML/jackson-databind">jackson api</a>
     * 
     * @param data
     * @param valueClass
     * @return T
     * @throws IOException
     */
    public static <T> T read(InputStream stream, Class<T> valueClass)
	    throws IOException {

	T value = getMapper().readValue(stream, valueClass);

	return value;
    }

    /**
     * Deserializes JSON (<code>byte[]</code>) to {@link Object} with <a
     * href="https://github.com/FasterXML/jackson-databind">jackson api</a>
     * 
     * @param data
     * @param valueClass
     * @return T
     * @throws IOException
     */
    public static <T> T read(byte[] bytes, Class<T> valueClass)
	    throws IOException {

	T value = getMapper().readValue(bytes, valueClass);

	return value;
    }
}
