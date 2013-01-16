package org.lightmare.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lightmare.ejb.EjbConnector;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

/**
 * Listener class for connection to bean remotely
 * 
 * @author Levan
 * 
 */
public class Listener {

	public static final int PROTOCOL_SIZE = 16;

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

	public void callRemote(Class<?> interfaceClass, String methodName,
			Object... parameters) throws IOException {
		int length = parameters.length;
		byte[][] parameterBytes = new byte[length][];
		Object parameter;
		byte[] parameterByte;
		for (int i = 0; i < length; i++) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(out);

			try {
				parameter = parameters[i];
				objectOut.writeObject(parameter);
				parameterBytes[i] = out.toByteArray();
			} finally {
				out.close();
				objectOut.close();
			}
		}

	}

	public static Object callBeanMethod(RpcWrapper wrapper) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		String beanName = wrapper.getBeanName();
		Method beanMethod = wrapper.getBeanMethod();
		Class<?> interfaceClass = wrapper.getInterfaceClass();
		Object[] params = wrapper.getParams();

		Object bean = new EjbConnector()
				.connectToBean(beanName, interfaceClass);
		try {
			return beanMethod.invoke(bean, params);
		} catch (IllegalArgumentException ex) {
			throw new IOException(ex);
		} catch (InvocationTargetException ex) {
			throw new IOException(ex);
		}
	}
}
