package org.lightmare.linq.lambda;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Utility class to retrieve {@link SerializedLambda} from lambda method call
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReplacements {

    // Method name to get SerializedLambda on the fly
    private static final String METHOD = "writeReplace";

    private static <T> Method getMethod(Class<?> parent, Class<T> type) throws IOException {

	Method method;

	try {
	    method = parent.getMethod(METHOD);
	} catch (NoSuchMethodException ex) {
	    method = ClassUtils.getDeclaredMethod(type, METHOD);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return method;
    }

    /**
     * Gets {@link SerializedLambda} instance from passed lambda argument
     * 
     * @param field
     * @return {@link SerializedLambda} replacement
     * @throws IOException
     */
    public static <T> SerializedLambda getReplacement(Object field, Class<T> type) throws IOException {

	SerializedLambda lambda;

	Class<?> parent = field.getClass();
	Method method = getMethod(parent, type);
	Object raw = ClassUtils.invokePrivate(method, field);
	lambda = ObjectUtils.cast(raw);

	return lambda;
    }
}
