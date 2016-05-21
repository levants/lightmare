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
package org.lightmare.criteria.query.orm.links;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Query parts for JPA / JDBC expression
 * 
 * @author Levan Tsinadze
 *
 */
public interface Parts {

    String FROM = " from ";

    String DISTINCT = "distinct ";

    String SET_SPACE = "    ";

    char COLUMN_PREFIX = '.';

    char PARAM_PREFIX = ':';

    char COMMA = ',';

    char LIKE_SIGN = '%';

    String TRIM_FROM = "FROM";

    String DOT_EXPR = Pattern.quote(StringUtils.DOT_STR);

    String UNDERSCORE_EXPR = Matcher.quoteReplacement(StringUtils.UNDERSCORE_STR);

    /**
     * Refines parameter name replacing dots with underscores
     * 
     * @param name
     * @return {@link String} parameter name
     */
    static String refineName(String name) {
        return ObjectUtils.ifIsNotNull(name, c -> c.replaceAll(DOT_EXPR, UNDERSCORE_EXPR));
    }
}
