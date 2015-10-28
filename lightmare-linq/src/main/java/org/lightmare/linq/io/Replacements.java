package org.lightmare.linq.io;

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
public class Replacements {

    // Method name to get SerializedLambda on the fly
    private static final String METHOD = "writeReplace";

    /**
     * Gets {@link SerializedLambda} instance from passed lambda argument
     * 
     * @param field
     * @return {@link SerializedLambda} replacement
     * @throws IOException
     */
    public static SerializedLambda getReplacement(Object field) throws IOException {

	SerializedLambda lambda;

	Class<?> parent = field.getClass();
	Method method = ClassUtils.getDeclaredMethod(parent, METHOD);
	Object raw = ClassUtils.invokePrivate(method, field);
	lambda = ObjectUtils.cast(raw);

	return lambda;
    }
}
