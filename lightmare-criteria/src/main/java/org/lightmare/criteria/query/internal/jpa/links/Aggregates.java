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
package org.lightmare.criteria.query.internal.jpa.links;

import org.lightmare.criteria.utils.StringUtils;

/**
 * Aggregate function JPA query names
 * 
 * @author Levan Tsinadze
 *
 */
public enum Aggregates {

    COUNT("count"), // COUNT clause
    COUNT_DISTINCT("count"), // COUNT DISTINCT clause
    AVG("avg"), // AVG clause
    GREATEST("greatest"), // GREATEST clause
    LEAST("least"), // LEAST clause
    MAX("max"), // MAX clause
    MIN("min"), // MIN clause
    SUM("sum"); // SUM clause

    public final String key;

    private static final char OPEN = '(';

    private static final char CLOSE = ')';

    private Aggregates(final String key) {
        this.key = key;
    }

    /**
     * Generates query expression for aggregate function
     * 
     * @param field
     * @return {@link String} aggregate function on field name
     */
    public String expression(String field) {

        String value;

        if (this.equals(COUNT_DISTINCT)) {
            value = StringUtils.concat(key, OPEN, Filters.DISTINCT, field, CLOSE);
        } else {
            value = StringUtils.concat(key, OPEN, field, CLOSE);
        }

        return value;
    }
}