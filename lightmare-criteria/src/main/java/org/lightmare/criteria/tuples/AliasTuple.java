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

import org.lightmare.criteria.utils.StringUtils;

/**
 * Container class with query alias and appropriated increment coefficient for
 * sub query alias generation
 * 
 * @author Levan Tsinadze
 *
 */
public class AliasTuple {

    private final String alias;

    private final SuffixTuple counter;

    private AliasTuple(final String alias, final SuffixTuple counter) {
        this.alias = alias;
        this.counter = counter;
    }

    /**
     * Initializes {@link org.lightmare.criteria.tuples.AliasTuple} by alias
     * name and counter to keep unique
     * 
     * @param alias
     * @param counter
     * @return {@link org.lightmare.criteria.tuples.AliasTuple} instance
     */
    public static AliasTuple of(final String alias, final SuffixTuple counter) {
        return new AliasTuple(alias, counter);
    }

    public SuffixTuple getCounter() {
        return counter;
    }

    public String generate() {
        return StringUtils.concat(alias, counter.getAndIncrement());
    }
}
