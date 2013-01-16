package org.lightmare.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lightmare.ejb.EjbConnector;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

/**
 * Listener class for serialization and de-serialization of objects and call
 * bean {@link Method}s connection to bean remotely
 * 
 * @author Levan
 * 
 */
public class RpcUtils {

	public static final int PROTOCOL_SIZE = 20;

	public static final int INT_SIZE = 4;

	public static final int BYTE_SIZE = 1;

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

	public static Object callRemoteMethod(Object proxy, Method method,
			Object[] arguments) throws IOException {

		RpcWrapper wrapper = new RpcWrapper();
		wrapper.setBeanName(proxy.getClass().getSimpleName());
		wrapper.setMethodName(method.getName());
		wrapper.setParamTypes(method.getParameterTypes());
		wrapper.setInterfaceClass(proxy.getClass());
		wrapper.setParams(arguments);

		RPCall rpCall = new RPCall();

		return rpCall.call(wrapper);
	}

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
		} catch (InstantiationException ex) {
			throw new IOException(ex);
		} catch (IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}
}
