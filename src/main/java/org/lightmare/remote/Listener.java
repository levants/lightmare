package org.lightmare.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	public void translate(String beanName, byte[]... parameters)
			throws IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		int length = parameters.length;
		Object[] params = new Object[length];
		for (int i = 0; i < length; i++) {

			byte[] parameter = parameters[i];
			ByteArrayInputStream bytes = new ByteArrayInputStream(parameter);
			ObjectInputStream input = new ObjectInputStream(bytes);

			try {
				Object param = input.readObject();
				params[i] = param;
			} finally {
				bytes.close();
				input.close();
			}
		}

		Class<?> interfaceClass = Class.forName(beanName);
	}
}
