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
package org.lightmare.criteria.utils;

import java.util.stream.Stream;

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

    public static final char LINE = '\n';

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
     * @return <code>boolean</code>
     */
    public static boolean validAll(CharSequence... lines) {

	boolean valid = CollectionUtils.valid(lines);

	if (valid) {
	    valid = Stream.of(lines).allMatch(c -> valid(c));
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

    private static void appendAll(StringBuilder text, Object... parts) {

	for (Object part : parts) {
	    text.append(part);
	}
    }

    /**
     * Concatenates passed {@link Object}s in one {@link String} instance
     *
     * @param parts
     * @return {@link String}
     */
    public static String concat(Object... parts) {

	String resultText;

	if (parts == null) {
	    resultText = null;
	} else if (CollectionUtils.isEmpty(parts)) {
	    resultText = EMPTY_STRING;
	} else {
	    StringBuilder text = new StringBuilder();
	    appendAll(text, parts);
	    resultText = text.toString();
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
	return (text == null || ObjectUtils.notTrue(text.contains(item)));
    }

    /**
     * Validates if passed {@link CharSequence} ends with specific part
     * 
     * @param item
     * @param text
     * @return <code>boolean</code> validation result
     */
    public static boolean endsWith(CharSequence item, CharSequence text) {

	boolean valid;

	int length = item.length();
	int start = length - text.length();
	if (start > CollectionUtils.EMPTY) {
	    CharSequence sub = item.subSequence(start, length);
	    valid = sub.equals(text);
	} else {
	    valid = Boolean.FALSE;
	}

	return valid;
    }

    /**
     * Validates if passed {@link CharSequence} not ends with specific part
     * 
     * @param item
     * @param text
     * @return <code>boolean</code> validation result
     */
    public static boolean notEndsWith(CharSequence item, CharSequence text) {
	return ObjectUtils.notTrue(endsWith(item, text));
    }

    /**
     * Validates if passed {@link CharSequence} not ends with any of specific
     * parts
     * 
     * @param item
     * @param texts
     * @return <code>boolean</code> validation result
     */
    public static boolean notEndsWithAll(CharSequence item, CharSequence... texts) {
	return CollectionUtils.validAll(texts, c -> notEndsWith(item, c));
    }
}
