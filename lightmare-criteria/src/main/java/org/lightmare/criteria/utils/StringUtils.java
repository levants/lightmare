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

import java.util.function.Consumer;
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

    public static final char COMMA = ',';

    public static final char DOT = '.';

    public static final char QUOTE = '\'';

    public static final char SEMICOLON = ';';

    public static final String SEMICOLON_TEXT = ";";

    public static final String TAB = "\t";

    public static final String NEWLINE = "\n";

    public static final char LINE = '\n';

    public static final int SINGLE_STEP = 1;

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
     * Checks if passed {@link CharSequence} is not null and is not empty and
     * runs consumer implementation
     *
     * @param chars
     * @param consumer
     * @return <code>boolean</code>
     */
    public static boolean valid(CharSequence chars, Consumer<CharSequence> consumer) {

        boolean valid = valid(chars);

        if (valid) {
            consumer.accept(chars);
        }

        return valid;
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

    /**
     * Checks if passed {@link CharSequence} is null or is empty
     *
     * @param chars
     * @return <code>boolean</code>
     */
    public static boolean isEmpty(CharSequence chars) {
        return !valid(chars);
    }

    /**
     * Appends all parameters to single {@link StringBuilder} instance
     * 
     * @param text
     * @param parts
     */
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
     * Deletes all characters from passed {@link StringBuilder} instance
     * 
     * @param text
     */
    public static void clear(StringBuilder text) {

        if (valid(text)) {
            text.delete(CollectionUtils.FIRST_INDEX, text.length());
        }
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
     * Validates if passed {@link CharSequence} ends with instant character
     * 
     * @param item
     * @param element
     * @return <code>boolean</code> validation result
     */
    public static boolean endsWith(CharSequence item, char element) {

        boolean valid = valid(item);

        if (valid) {
            int index = (item.length() - SINGLE_STEP);
            valid = (item.charAt(index) == element);
        }

        return valid;
    }

    /**
     * Validates if passed {@link CharSequence} not ends with instant character
     * 
     * @param item
     * @param element
     * @return <code>boolean</code> validation result
     */
    public static boolean notEndsWith(CharSequence item, char element) {
        return ObjectUtils.notTrue(endsWith(item, element));
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

    /**
     * Appends "'" quotes as prefix and suffix passed {@link Object} instance
     * 
     * @param item
     * @return {@link String} generated text
     */
    public static String qlize(Object item) {
        return StringUtils.concat(QUOTE, item, QUOTE);
    }
}
