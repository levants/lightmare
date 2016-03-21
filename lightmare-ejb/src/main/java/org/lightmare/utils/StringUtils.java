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
package org.lightmare.utils;

import java.lang.reflect.Array;

import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class for {@link String} and {@link CharSequence} operations
 *
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
 */
public abstract class StringUtils {

    public static final String EMPTY_STRING = "";

    public static final char SPACE = ' ';

    public static final char HYPHEN = '-';

    public static final char DOT = '.';

    public static final String TAB = "\t";

    public static final String NEWLINE = "\n";

    public static final int NOT_EXISTING_INDEX = -1;

    /**
     * Checks if passed {@link CharSequence} is not null and is not empty
     *
     * @param chars
     * @return <code>boolean</code>
     */
    public static boolean valid(CharSequence chars) {
        return chars != null && chars.length() > CollectionUtils.EMPTY_ARRAY_LENGTH;
    }

    /**
     * Checks if each of passed {@link CharSequence}s is not null and is not
     * empty
     *
     * @param lines
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(CharSequence... lines) {

        boolean valid = CollectionUtils.valid(lines);

        if (valid) {
            int length = lines.length;
            CharSequence line;
            for (int i = CollectionUtils.FIRST_INDEX; i < length && valid; i++) {
                line = lines[i];
                // TODO Think only second part of && is needed
                valid = valid && valid(line);
            }
        }

        return valid;
    }

    /**
     * Checks if passed {@link CharSequence} is null or is empty
     *
     * @param chars
     * @return <code>boolean</code>
     */
    public static boolean invalid(CharSequence chars) {
        return !valid(chars);
    }

    /**
     * Appends contents of passed array to passed {@link StringBuilder} and for
     * each content if it is instance of array then append its content
     * recursively
     *
     * @param tockens
     * @param builder
     */
    private static void append(Object tocken, StringBuilder builder) {

        if (CollectionUtils.isObjectArray(tocken)) {
            Object[] tockens = ObjectUtils.cast(tocken);
            for (Object subTocken : tockens) {
                append(subTocken, builder);
            }
        } else if (CollectionUtils.isPrimitiveArray(tocken)) {
            int length = Array.getLength(tocken);
            Object subTocken;
            for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
                subTocken = Array.get(tocken, i);
                append(subTocken, builder);
            }
        } else {
            builder.append(tocken);
        }
    }

    /**
     * Creates concatenates passed objects in one text and if one of them is
     * array then concatenates contents of this array recursively
     *
     * @param tockens
     * @return {@link String}
     */
    public static String concatRecursively(Object... tockens) {

        String concat;

        if (CollectionUtils.valid(tockens)) {
            StringBuilder builder = new StringBuilder();
            append(tockens, builder);
            concat = builder.toString();
        } else {
            concat = null;
        }

        return concat;
    }

    /**
     * Concatenates passed {@link Object}s in one {@link String} instance
     *
     * @param parts
     * @return {@link String} composed by parts
     */
    public static String concat(Object... parts) {

        String resultText;

        if (CollectionUtils.valid(parts)) {
            StringBuilder resultBuider = new StringBuilder();
            for (Object part : parts) {
                resultBuider.append(part);
            }
            resultText = resultBuider.toString();
        } else {
            resultText = null;
        }

        return resultText;
    }

    /**
     * Checks if passed {@link String} instance not contains passed
     * {@link CharSequence} instance
     *
     * @param text
     * @param item
     * @return <code>boolean</code> validation result
     */
    public static boolean notContains(String text, CharSequence item) {
        return (text == null || Boolean.FALSE.equals(text.contains(item)));
    }
}
