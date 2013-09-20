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
 * JSON serialization using jackson JSON library
 * 
 * @author Levan
 * 
 */
public class JsonSerializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static boolean mapperConfigured;

    // Lock for initializing ObjectMapper instance
    private static final Lock LOCK = new ReentrantLock();

    private static void configureMapper() {

	LOCK.lock();
	try {
	    if (ObjectUtils.notTrue(mapperConfigured)) {
		MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapperConfigured = Boolean.TRUE;
	    }
	} finally {
	    LOCK.unlock();
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
	    return data;
	} catch (JsonGenerationException ex) {
	    throw new IOException(ex);
	} catch (JsonMappingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Deserializes JSON {@link String} to {@link Object} with <a
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
	    return value;
	} catch (JsonGenerationException ex) {
	    throw new IOException(ex);
	} catch (JsonMappingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
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
