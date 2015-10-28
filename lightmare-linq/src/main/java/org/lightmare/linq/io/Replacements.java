package org.lightmare.linq.io;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Replacements {

	private static final String METHOD = "writeReplace";

	public static SerializedLambda getReplacement(Object field) throws IOException {

		SerializedLambda lambda;

		Class<?> cl = field.getClass();
		try {
			Method method = cl.getDeclaredMethod(METHOD);
			method.setAccessible(Boolean.TRUE);
			Object replacement = method.invoke(field);
			lambda = (SerializedLambda) replacement;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException ex) {
			throw new IOException(ex);
		}

		return lambda;
	}
}
