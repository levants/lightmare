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

/**
 * Container class for {@link java.lang.invoke.SerializedLambda} parameters
 * 
 * @author Levan Tsinadze
 * @see java.lang.invoke.SerializedLambda
 */
class SLambda implements Serializable {

    // Serial version UID for original class
    private static final long serialVersionUID = 8025925345765570181L;

    // Implementing class file name
    public String implClass;

    // Implementing method name
    public String implMethodName;

    // Implementing method signature
    public String implMethodSignature;

    // Implementation method kind
    public int implMethodKind;

    // Implemented method type
    public String instantiatedMethodType;
}
