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

    private int count;

    private static final String PARAMETER_FORMAT = "%s : %s";

    private ParameterTuple(final String name, final Object value, final TemporalType temporalType) {
        this.name = name;
        this.value = value;
        this.temporalType = temporalType;
    }

    private ParameterTuple(final Couple<String, Integer> couple, final Object value, final TemporalType temporalType) {
        this(couple.getFirst(), value, temporalType);
        this.count = couple.getSecond();
    }

    /**
     * Initializes {@link org.lightmare.criteria.tuples.ParameterTuple} by
     * parameter name, value and {@link javax.persistence.TemporalType} instance
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return {@link org.lightmare.criteria.tuples.ParameterTuple} instance
     */
    public static ParameterTuple of(final String name, final Object value, final TemporalType temporalType) {
        return new ParameterTuple(name, value, temporalType);
    }

    /**
     * Initializes {@link org.lightmare.criteria.tuples.ParameterTuple} by
     * parameter {@link org.lightmare.criteria.tuples.Couple}, value and
     * {@link javax.persistence.TemporalType} instance
     * 
     * @param couple
     * @param value
     * @param temporalType
     * @return {@link org.lightmare.criteria.tuples.ParameterTuple} instance
     */
    public static ParameterTuple of(final Couple<String, Integer> couple, final Object value,
            final TemporalType temporalType) {
        return new ParameterTuple(couple, value, temporalType);
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

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return String.format(PARAMETER_FORMAT, name, value);
    }
}
