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

    private static final String TO_TEXT_FORMAT = StringUtils.concat("SerializedLambda[implClass=%s,",
            " implMethodName=%s,", " implMethodSignature=%s]");

    public LambdaInfo(final String implClass, final String implMethodName, final String implMethodSignature) {
        this.implClass = implClass;
        this.implMethodName = implMethodName;
        this.implMethodSignature = implMethodSignature;
    }

    public LambdaInfo(SerializedLambda serialized) {
        this(serialized.getImplClass(), serialized.getImplMethodName(), serialized.getImplMethodSignature());
    }

    public LambdaInfo(SLambda slambda) {
        this(slambda.implClass, slambda.implMethodName, slambda.implMethodSignature);
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

    @Override
    public String toString() {
        return String.format(TO_TEXT_FORMAT, implClass, implMethodName, implMethodSignature);
    }
}
