package org.lightmare.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Listener class for connection to bean remotely
 * 
 * @author Levan
 * 
 */
public class Listener {

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
			Object param = input.readObject();
			params[i] = param;
		}

		Class<?> interfaceClass = Class.forName(beanName);
	}
}
