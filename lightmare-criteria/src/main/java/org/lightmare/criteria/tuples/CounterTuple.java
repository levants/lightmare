/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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

import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Tuple for parameter and alias suffixes
 * 
 * @author Levan Tsinadze
 *
 */
public class CounterTuple {

    private int alias;

    private int parameter = CollectionUtils.SINGLETON;

    private CounterTuple() {
    }

    public static CounterTuple get() {
        return new CounterTuple();
    }

    public int getAndIncrementAlias() {
        return alias++;
    }

    private int getAndIncrementParameter() {
        return parameter++;
    }

    /**
     * Increments counter and generates parameter name
     * 
     * @param name
     * @return {@link org.lightmare.criteria.tuples.Couple} of counter and
     *         parameter name
     */
    public Couple<String, Integer> getAndIncrement(String name) {

        Couple<String, Integer> couple;

        int count = getAndIncrementParameter();
        String countName = StringUtils.concat(name, StringUtils.UNDERSCORE, count);
        couple = Couple.of(countName, count);

        return couple;
    }
}
