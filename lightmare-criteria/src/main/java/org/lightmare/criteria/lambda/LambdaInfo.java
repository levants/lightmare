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

import java.lang.invoke.SerializedLambda;

import org.lightmare.criteria.utils.StringUtils;

/**
 * Lambda expression meta information container class
 * 
 * @author Levan Tsinadze
 * @see java.lang.invoke.SerializedLambda
 */
public class LambdaInfo {

    // Class file name
    private final String implClass;

    // Method name
    private final String implMethodName;

    // Method signature
    private final String implMethodSignature;

    // Implementation method kind
    private final int implMethodKind;

    // Method type
    private final String instantiatedMethodType;

    private static final String FORMAT = StringUtils.concat("SerializedLambda[implClass=%s,", " implMethodName=%s,",
            " implMethodSignature=%s]");

    private LambdaInfo(final String implClass, final String implMethodName, final String implMethodSignature,
            final int implMethodKind, final String instantiatedMethodType) {
        this.implClass = implClass;
        this.implMethodName = implMethodName;
        this.implMethodSignature = implMethodSignature;
        this.implMethodKind = implMethodKind;
        this.instantiatedMethodType = instantiatedMethodType;
    }

    public static LambdaInfo of(SerializedLambda serialized) {
        return new LambdaInfo(serialized.getImplClass(), serialized.getImplMethodName(),
                serialized.getImplMethodSignature(), serialized.getImplMethodKind(),
                serialized.getInstantiatedMethodType());
    }

    public static LambdaInfo of(SLambda slambda) {
        return new LambdaInfo(slambda.implClass, slambda.implMethodName, slambda.implMethodSignature,
                slambda.implMethodKind, slambda.instantiatedMethodType);
    }

    /**
     * Get the name of the class containing the implementation method.
     * 
     * @return the name of the class containing the implementation method
     */
    public String getImplClass() {
        return implClass;
    }

    /**
     * Get the name of the implementation method.
     * 
     * @return the name of the implementation method
     */
    public String getImplMethodName() {
        return implMethodName;
    }

    /**
     * Get the signature of the implementation method.
     * 
     * @return the signature of the implementation method
     */
    public String getImplMethodSignature() {
        return implMethodSignature;
    }

    /**
     * Get the method handle kind (see {@link java.lang.invoke.MethodHandleInfo}
     * ) of the implementation method.
     * 
     * @return the method handle kind of the implementation method
     */
    public int getImplMethodKind() {
        return implMethodKind;
    }

    /**
     * Get the signature of the primary functional interface method after type
     * variables are substituted with their instantiation from the capture site.
     * 
     * @return {@link String} the signature of the primary functional interface
     *         method after type variable processing
     */
    public String getInstantiatedMethodType() {
        return instantiatedMethodType;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, implClass, implMethodName, implMethodSignature);
    }
}
