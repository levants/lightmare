package org.lightmare.criteria.lambda;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.io.IOUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Utility class to retrieve {@link SerializedLambda} data from lambda
 * expression
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReplacements {

    // Method name to get SerializedLambda on the fly
    private static final String METHOD = "writeReplace";

    private static final String CHARSET = "iso-8859-1";

    private static final String NATIVE_NAME = java.lang.invoke.SerializedLambda.class.getName();

    private static final String LINQ_NAME = org.lightmare.criteria.lambda.SLambda.class.getName();

    private static byte[] serialize(Object field) throws IOException {

	byte[] value;

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(bos);
	try {
	    oos.writeObject(field);
	    oos.flush();
	    value = bos.toByteArray();
	} finally {
	    IOUtils.closeAll(bos, oos);
	}

	return value;
    }

    private static SLambda toLambda(byte[] buff) throws IOException {

	SLambda lambda;

	ByteArrayInputStream bin = new ByteArrayInputStream(buff);
	try {
	    ObjectInputStream oin = new ObjectInputStream(bin);
	    Object raw = oin.readObject();
	    lambda = ObjectUtils.cast(raw);
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	} finally {
	    IOUtils.close(bin);
	}

	return lambda;
    }

    private static LambdaData translate(Object field) throws IOException {

	LambdaData lambda;

	byte[] value = serialize(field);
	byte[] translated = new String(value, CHARSET).replace(NATIVE_NAME, LINQ_NAME).getBytes(CHARSET);
	SLambda slambda = toLambda(translated);
	lambda = new LambdaData(slambda);

	return lambda;
    }

    private static <T> Method getMethod(Class<?> parent) throws IOException, NoSuchMethodException {

	Method method;

	try {
	    method = parent.getDeclaredMethod(METHOD);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return method;
    }

    /**
     * Gets {@link LambdaData} instance from passed lambda argument
     * 
     * @param field
     * @return {@link LambdaData} replacement
     * @throws IOException
     */
    public static <T> LambdaData getReplacement(Object field) throws IOException {

	LambdaData lambda;

	Class<?> parent = field.getClass();
	try {
	    Method method = getMethod(parent);
	    Object raw = ClassUtils.invokePrivate(method, field);
	    SerializedLambda serialized = ObjectUtils.cast(raw);
	    lambda = new LambdaData(serialized);
	} catch (NoSuchMethodException ex) {
	    lambda = translate(field);
	}

	return lambda;
    }
}
