package org.lightmare.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lightmare.ejb.EjbConnector;

/**
 * Listener class for connection to bean remotely
 * 
 * @author Levan
 * 
 */
public class Listener {

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

	@SuppressWarnings("unused")
	public Object translate(String beanName, Class<?> interfaceClass,
			Method method, byte[]... parameters) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		int length = parameters.length;
		Object[] params = new Object[length];
		Class<?>[] paramTypes = new Class<?>[length];
		for (int i = 0; i < length; i++) {

			byte[] parameter = parameters[i];
			ByteArrayInputStream bytes = new ByteArrayInputStream(parameter);
			ObjectInputStream input = new ObjectInputStream(bytes);

			try {
				Object param = input.readObject();
				params[i] = param;
				paramTypes[i] = param.getClass();
			} finally {
				bytes.close();
				input.close();
			}
		}

		Object bean = new EjbConnector()
				.connectToBean(beanName, interfaceClass);
		try {
			return method.invoke(bean, params);
		} catch (IllegalArgumentException ex) {
			throw new IOException(ex);
		} catch (InvocationTargetException ex) {
			throw new IOException(ex);
		}
	}
}
