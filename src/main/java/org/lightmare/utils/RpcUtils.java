package org.lightmare.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lightmare.ejb.EjbConnector;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Listener class for serialization and de-serialization (both java and json) of
 * objects and call bean {@link Method}s connection to bean remotely
 * 
 * @author Levan
 * 
 */
public class RpcUtils {

    public static final int PROTOCOL_SIZE = 20;

    public static final int INT_SIZE = 4;

    public static final int BYTE_SIZE = 1;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static boolean mapperConfigured;

    private static void configureMapper() {

	synchronized (RpcUtils.class) {
	    if (ObjectUtils.notTrue(mapperConfigured)) {
		MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapperConfigured = Boolean.TRUE;
	    }
	}
    }

    /**
     * Configures {@link ObjectMapper} if {@link RpcUtils#mapperConfigured} is
     * <code>false</code>
     * 
     * @return {@link ObjectMapper}
     */
    private static ObjectMapper getMapper() {

	if (ObjectUtils.notTrue(mapperConfigured)) {
	    configureMapper();
	}

	return MAPPER;
    }

    /**
     * Serializes java type ({@link Object}) to byte array with java native
     * serialization api
     * 
     * @param value
     * @return byte[]
     * @throws IOException
     */
    public static byte[] serialize(Object value) throws IOException {

	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	ObjectOutputStream objectStream = new ObjectOutputStream(stream);

	try {

	    objectStream.writeObject(value);
	    byte[] data = stream.toByteArray();

	    return data;

	} finally {
	    stream.close();
	    objectStream.close();
	}
    }

    /**
     * Serializes {@link Object} to josn {@link String} with <a
     * href="https://github.com/FasterXML/jackson-databind">jackson api</a>
     * 
     * @param value
     * @return {@link String}
     * @throws IOException
     */
    public static <T> String write(Object value) throws IOException {
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
     * Deserializes byte array in java type ({@link Object}) with java native
     * serialization api
     * 
     * @param data
     * @return {@link Object}
     * @throws IOException
     */
    public static Object deserialize(byte[] data) throws IOException {

	ByteArrayInputStream stream = new ByteArrayInputStream(data);
	ObjectInputStream objectStream = new ObjectInputStream(stream);
	try {

	    Object value = objectStream.readObject();

	    return value;

	} catch (ClassNotFoundException ex) {

	    throw new IOException(ex);

	} finally {
	    stream.close();
	    objectStream.close();
	}
    }

    /**
     * Deserializes josn {@link String} to {@link Object} with <a
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
     * Calls remote method for java RPC api
     * 
     * @param proxy
     * @param method
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object callRemoteMethod(Object proxy, Method method,
	    Object[] arguments, RPCall rpCall) throws IOException {

	RpcWrapper wrapper = new RpcWrapper();
	wrapper.setBeanName(proxy.getClass().getSimpleName());
	wrapper.setMethodName(method.getName());
	wrapper.setParamTypes(method.getParameterTypes());
	wrapper.setInterfaceClass(proxy.getClass());
	wrapper.setParams(arguments);

	return rpCall.call(wrapper);
    }

    /**
     * Calls {@link javax.ejb.Stateless} bean method by {@link RcpWrapper} for
     * java RPC calls
     * 
     * @param wrapper
     * @return {@link Object}
     * @throws IOException
     */
    public static Object callBeanMethod(RpcWrapper wrapper) throws IOException {

	String beanName = wrapper.getBeanName();
	String methodName = wrapper.getMethodName();
	Class<?>[] paramTypes = wrapper.getParamTypes();
	Class<?> interfaceClass = wrapper.getInterfaceClass();
	Object[] params = wrapper.getParams();

	try {

	    Object bean = new EjbConnector().connectToBean(beanName,
		    interfaceClass);
	    Method beanMethod = bean.getClass().getDeclaredMethod(methodName,
		    paramTypes);
	    return beanMethod.invoke(bean, params);

	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	}
    }
}
