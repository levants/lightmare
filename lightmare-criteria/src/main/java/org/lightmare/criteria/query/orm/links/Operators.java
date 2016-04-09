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

import org.lightmare.criteria.utils.StringUtils;

/**
 * Query operator keywords
 * 
 * @author Levan Tsinadze
 *
 */
public interface Operators {

    /**
     * Open and close brackets
     * 
     * @author Levan Tsinadze
     *
     */
    public static interface Brackets {

        char OPEN = '(';

        char CLOSE = ')';
    }

    // ==================boolean================//

    String AND = "and ";

    String OR = "or ";

    String NOT = " not ";

    String NO = " !";

    // =================comparators=============//

    String EQ = " = ";

    String NOT_EQ = " != ";

    String GREATER = " > ";

    String LESS = " < ";

    String GREATER_OR_EQ = " >= ";

    String LESS_OR_EQ = " <= ";

    String BETWEEN = " between ";

    String NOT_BETWEEN = " not between ";

    String LIKE = " like ";

    String ESCAPE = " escape ";

    String NOT_LIKE = " not like ";

    String IS_NULL = " is null ";

    String NOT_NULL = " is not null ";

    String IN = " in";

    String NOT_IN = " not in";

    String MEMBER = " member of ";

    String NOT_MEMBER = " not member of ";

    String OPEN_BRACKET = StringUtils.concat(StringUtils.SPACE, Brackets.OPEN);

    String CLOSE_BRACKET = StringUtils.concat(Brackets.CLOSE, StringUtils.SPACE);

    // =================sub==queries============//

    String ANY = "any";

    String ALL = "all";

    String SOME = "some";

    String EXISTS = " exists ";

    String NOT_EXISTS = " not exists ";
}
