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
 * Used to specify how strings are trimmed.
 */
public enum Trimspec {

    /**
     * Trim from leading end.
     */
    LEADING("LEADING "),

    /**
     * Trim from trailing end.
     */
    TRAILING("TRAILING "),

    /**
     * Trim from both ends.
     */
    BOTH("BOTH ");

    public final String prefix;

    private static final String FROM = "FROM";

    public final String pattern;

    private Trimspec(final String prefix) {
        this.prefix = prefix;
        this.pattern = StringUtils.concat(prefix, FROM);
    }

    public String locate(char ch) {
        return StringUtils.concat(prefix, StringUtils.QUOTE, ch, StringUtils.QUOTE, StringUtils.SPACE, FROM);
    }

    public static String locateAll(char ch) {
        return StringUtils.concat(StringUtils.QUOTE, ch, StringUtils.QUOTE, StringUtils.SPACE, FROM);
    }
}
