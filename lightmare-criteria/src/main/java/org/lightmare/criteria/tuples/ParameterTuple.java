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
package org.lightmare.criteria.tuples;

import javax.persistence.TemporalType;

/**
 * Query parameter name and value container class
 * 
 * @author Levan Tsinadze
 *
 */
public class ParameterTuple {

    private final String name;

    private final Object value;

    private final TemporalType temporalType;

    private static final String PARAMETER_FORMAT = "%s : %s";

    protected ParameterTuple(final String name, final Object value, final TemporalType temporalType) {
        this.name = name;
        this.value = value;
        this.temporalType = temporalType;
    }

    /**
     * Initializes {@link ParameterTuple} by parameter name, value and
     * {@link javax.persistence.TemporalType} instance
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return {@link ParameterTuple} instance
     */
    public static ParameterTuple of(final String name, final Object value, final TemporalType temporalType) {
        return new ParameterTuple(name, value, temporalType);
    }

    public ParameterTuple(final String name, final Object value) {
        this(name, value, null);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    @Override
    public String toString() {
        return String.format(PARAMETER_FORMAT, name, value);
    }
}
