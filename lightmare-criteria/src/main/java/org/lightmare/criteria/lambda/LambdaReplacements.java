/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.criteria.lambda;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.io.serialization.NativeSerializer;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Utility class to retrieve {@link SerializedLambda} data from lambda function
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

    /**
     * Serializes object and translates it to {@link SLambda} instance
     * 
     * @param buff
     * @return {@link SLambda} from serialized object
     * @throws IOException
     */
    private static SLambda toLambda(byte[] buff) throws IOException {

	SLambda lambda;

	Object raw = NativeSerializer.deserialize(buff);
	lambda = ObjectUtils.cast(raw);

	return lambda;
    }

    /**
     * Replaces special characters to get appropriated object type
     * 
     * @param value
     * @return <code>byte</code>[] instance of translated type
     * @throws UnsupportedEncodingException
     */
    private static byte[] replace(byte[] value) throws UnsupportedEncodingException {

	byte[] translated;

	String buffText = new String(value, CHARSET);
	String replText = buffText.replace(NATIVE_NAME, LINQ_NAME);
	translated = replText.getBytes(CHARSET);

	return translated;
    }

    /**
     * Serializes object and translates it and wraps it's field to
     * {@link LambdaData} object
     * 
     * @param field
     * @return {@link LambdaData} from lambda expression
     * @throws IOException
     */
    private static LambdaData translate(Object field) throws IOException {

	LambdaData lambda;

	byte[] value = NativeSerializer.serialize(field);
	byte[] translated = replace(value);
	SLambda slambda = toLambda(translated);
	lambda = new LambdaData(slambda);

	return lambda;
    }

    /**
     * Gets serialization {@link Method} from {@link Serializable}
     * implementation
     * 
     * @param parent
     * @return {@link Method} for serialization
     * @throws IOException
     * @throws NoSuchMethodException
     */
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

	try {
	    Class<?> parent = field.getClass();
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
