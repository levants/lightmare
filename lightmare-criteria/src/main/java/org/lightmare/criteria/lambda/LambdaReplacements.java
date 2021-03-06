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

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Utility class to retrieve {@link java.lang.invoke.SerializedLambda}
 * parameters from lambda function
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReplacements {

    // Method name to get SerializedLambda on the fly
    private static final String METHOD_NAME = "writeReplace";

    // Serialization parameters
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
     */
    private static SLambda toLambda(byte[] buff) {
        return ObjectUtils.deserialize(buff);
    }

    /**
     * Replaces special characters to get appropriated object type
     * 
     * @param value
     * @return <code>byte</code>array serialized object
     */
    private static byte[] replace(byte[] value) {

        byte[] translated;

        String buffText = StringUtils.fromBytes(value, CHARSET);
        String replText = buffText.replace(NATIVE_NAME, LINQ_NAME);
        translated = StringUtils.getBytes(replText, CHARSET);

        return translated;
    }

    /**
     * Translates passed method reference to
     * {@link org.lightmare.criteria.lambda.SLambda} instance
     * 
     * @param method
     * @return {@link org.lightmare.criteria.lambda.SLambda} from method
     *         reference
     */
    private static SLambda toSLambda(Object method) {

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
     */
    private static LambdaInfo translate(Object method) {

        LambdaInfo lambda;

        SLambda slambda = toSLambda(method);
        lambda = LambdaInfo.of(slambda);

        return lambda;
    }

    /**
     * Gets serialization {@link java.lang.reflect.Method} from
     * {@link java.io.Serializable} implementation
     * 
     * @param parent
     * @return {@link java.lang.reflect.Method} for serialization
     */
    private static <T> Method findWriteReplaceMethod(Class<?> parent) {
        return ClassUtils.findMethod(parent, METHOD_NAME);
    }

    /**
     * Gets serialization {@link java.lang.reflect.Method} from
     * {@link java.io.Serializable} instance
     * 
     * @param method
     * @return {@link java.lang.reflect.Method} for serialization
     */
    private static <T> Method getMethod(Serializable method) {
        return findWriteReplaceMethod(method.getClass());
    }

    /**
     * Gets @link org.lightmare.criteria.lambda.LambdaInfo} instance from lambda
     * implementation through {@link java.lang.invoke.SerializedLambda}
     * parameters
     * 
     * @param writeReplace
     * @param field
     * @return {@link org.lightmare.criteria.lambda.LambdaInfo} replacement
     */
    private static <T> LambdaInfo invokeMethod(Method writeReplace, Serializable field) {

        LambdaInfo lambda;

        SerializedLambda serialized = ClassUtils.invoke(writeReplace, field);
        lambda = LambdaInfo.of(serialized);

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
        return ObjectUtils.applyNonNull(method, LambdaReplacements::getMethod, c -> invokeMethod(c, method),
                LambdaReplacements::translate);
    }
}
