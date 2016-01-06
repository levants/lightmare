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
import java.util.Objects;

import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Utility class to retrieve {@link java.lang.invoke.SerializedLambda}
 * parameters from lambda function
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReplacements {

    // Method name to get SerializedLambda on the fly
    private static final String METHOD = "writeReplace";

    private static final String CHARSET = "iso-8859-1";

    private static final String NATIVE_NAME = SerializedLambda.class.getName();

    private static final String LINQ_NAME = SLambda.class.getName();

    /**
     * Serializes object and translates it to
     * {@link org.lightmare.criteria.lambda.SLambda} instance
     * 
     * @param buff
     * @return {@link org.lightmare.criteria.lambda.SLambda} from serialized
     *         object
     * @throws IOException
     */
    private static SLambda toLambda(byte[] buff) throws IOException {
        return ObjectUtils.deserialize(buff);
    }

    /**
     * Replaces special characters to get appropriated object type
     * 
     * @param value
     * @return <code>byte</code>array serialized object
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
     * Translates passed method reference to
     * {@link org.lightmare.criteria.lambda.SLambda} instance
     * 
     * @param method
     * @return {@link org.lightmare.criteria.lambda.SLambda} from method
     *         reference
     * @throws IOException
     */
    private static SLambda toSLambda(Object method) throws IOException {

        SLambda slambda;

        byte[] value = ObjectUtils.serialize(method);
        byte[] translated = replace(value);
        slambda = toLambda(translated);

        return slambda;
    }

    /**
     * Serializes object and translates it and wraps it's field to
     * {@link org.lightmare.criteria.lambda.LambdaInfo} object
     * 
     * @param method
     * @return {@link org.lightmare.criteria.lambda.LambdaInfo} from lambda
     *         expression
     * @throws IOException
     */
    private static LambdaInfo translate(Object method) throws IOException {

        LambdaInfo lambda;

        SLambda slambda = toSLambda(method);
        lambda = new LambdaInfo(slambda);

        return lambda;
    }

    /**
     * Gets serialization {@link java.lang.reflect.Method} from
     * {@link java.io.Serializable} implementation
     * 
     * @param parent
     * @return {@link java.lang.reflect.Method} for serialization
     */
    private static <T> Method getMethod(Class<?> parent) {
        return ClassUtils.findMethod(parent, METHOD);
    }

    /**
     * Gets {@link org.lightmare.criteria.lambda.LambdaInfo} instance from
     * passed lambda argument
     * 
     * @param field
     * @return {@link org.lightmare.criteria.lambda.LambdaInfo} replacement
     * @throws IOException
     */
    private static <T> LambdaInfo getLambdaReplacement(Serializable method) throws IOException {

        LambdaInfo lambda;

        Class<?> parent = method.getClass();
        Method writeReplace = getMethod(parent);
        if (Objects.nonNull(writeReplace)) {
            SerializedLambda serialized = ClassUtils.invoke(writeReplace, method);
            lambda = new LambdaInfo(serialized);
        } else {
            lambda = translate(method);
        }

        return lambda;
    }

    /**
     * Gets {@link org.lightmare.criteria.lambda.LambdaInfo} instance from
     * passed lambda argument
     * 
     * @param method
     * @return {@link org.lightmare.criteria.lambda.LambdaInfo} replacement
     */
    public static <T> LambdaInfo getReplacement(Serializable method) {

        LambdaInfo lambda;

        try {
            lambda = getLambdaReplacement(method);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return lambda;
    }
}
