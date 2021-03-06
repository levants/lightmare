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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Utility class for {@link String} and {@link CharSequence} operations
 *
 * @author Levan Tsinadze
 */
public abstract class StringUtils {

    // Reusable characters or text
    public static final String EMPTY = "";

    public static final char SPACE = ' ';

    public static final char HYPHEN = '-';

    public static final char UNDERSCORE = '_';

    public static final String UNDERSCORE_STR = String.valueOf(UNDERSCORE);

    public static final char COMMA = ',';

    public static final char DOT = '.';

    public static final String DOT_STR = String.valueOf(DOT);

    public static final char QUOTE = '\'';

    public static final char SEMICOLON = ';';

    public static final String SEMICOLON_TEXT = ";";

    public static final String TAB = "\t";

    public static final String NEWLINE = "\n";

    public static final char LINE = '\n';

    public static final int SINGLE_STEP = 1;

    public static final int NOT_EXISTING = -1;

    /**
     * Checks if passed {@link CharSequence} is not <code>null</code> and is not
     * empty
     *
     * @param chars
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(CharSequence chars) {
        return (Objects.nonNull(chars) && chars.length() > CollectionUtils.EMPTY);
    }

    /**
     * Checks if passed {@link CharSequence} is not <code>null</code> and is not
     * empty and runs consumer implementation
     *
     * @param chars
     * @param consumer
     * @return <code>boolean</code> validation result
     */
    public static <T extends CharSequence> boolean valid(T chars, Consumer<T> consumer) {
        return ObjectUtils.valid(chars, StringUtils::valid, consumer);
    }

    /**
     * Checks if each of passed {@link CharSequence}s is not <code>null</code>
     * and is not empty
     *
     * @param lines
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(CharSequence... lines) {
        return (CollectionUtils.valid(lines) && Stream.of(lines).allMatch(StringUtils::valid));
    }

    /**
     * Checks if passed {@link CharSequence} is <code>null</code> or is empty
     *
     * @param chars
     * @return <code>boolean</code> validation result
     */
    public static boolean isEmpty(CharSequence chars) {
        return ObjectUtils.notTrue(valid(chars));
    }

    /**
     * Initializes new {@link String} from byte array and encoding swallowing
     * exception
     * 
     * @param bytes
     * @param charset
     * @return {@link String} from bytes by encoding
     */
    public static String fromBytes(byte[] bytes, String charset) {
        return ObjectUtils.applyQuietly(bytes, charset, String::new);
    }

    /**
     * Translates passed {@link String} to byte array by encoding swallowing
     * exception
     * 
     * @param text
     * @param charset
     * @return <code>byte</code> array from {@link String} by encoding
     */
    public static byte[] getBytes(String text, String charset) {
        return ObjectUtils.applyQuietly(text, charset, String::getBytes);
    }

    /**
     * Validates passed value on <code>null</code> and if it is returns
     * {@link java.util.function.Supplier} provided value
     * 
     * @param item
     * @param supplier
     * @return T value
     */
    public static <T extends CharSequence> T thisOrDefault(T item, Supplier<T> supplier) {

        T value;

        if (isEmpty(item)) {
            value = supplier.get();
        } else {
            value = item;
        }

        return value;
    }

    /**
     * Appends all parameters to single {@link StringBuilder} instance
     * 
     * @param text
     * @param parts
     */
    private static void appendAll(StringBuilder text, Object... parts) {
        CollectionUtils.forEach(parts, (i, c) -> text.append(c));
    }

    /**
     * Concatenates passed {@link Object}s in one {@link String} instance
     *
     * @param parts
     * @return {@link String}
     */
    public static String concat(Object... parts) {

        String result;

        if (parts == null) {
            result = null;
        } else if (CollectionUtils.isEmpty(parts)) {
            result = EMPTY;
        } else {
            StringBuilder text = new StringBuilder();
            appendAll(text, parts);
            result = text.toString();
        }

        return result;
    }

    /**
     * Deletes all characters from passed {@link StringBuilder} instance
     * 
     * @param text
     */
    public static void clear(StringBuilder text) {
        valid(text, c -> c.delete(CollectionUtils.FIRST, c.length()));
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

    /***
     * Validates if passed text starts with special characters
     * 
     * @param item
     * @param element
     * @return <code>boolean</code> validation result
     */
    private static boolean validAndStartsWith(CharSequence item, CharSequence element) {

        boolean valid;

        int length = item.length();
        int end = element.length();
        valid = (length > end);
        for (int i = CollectionUtils.FIRST; (i < end && valid); i++) {
            valid = (item.charAt(i) == element.charAt(i));
        }

        return valid;
    }

    /**
     * Validates if passed text starts with special characters
     * 
     * @param item
     * @param element
     * @return <code>boolean</code> validation result
     */
    public static boolean startsWith(CharSequence item, CharSequence element) {
        return (valid(item) && valid(element) && validAndStartsWith(item, element));
    }

    /**
     * Validates if passed text not starts with special characters
     * 
     * @param item
     * @param element
     * @return <code>boolean</code> validation result
     */
    public static boolean notStartsWith(CharSequence item, CharSequence element) {
        return ObjectUtils.notTrue(startsWith(item, element));
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
     * Replaces last character
     * 
     * @param item
     * @param character
     * @param toreplace
     */
    public static void replaceLast(StringBuilder item, char character, char toreplace) {

        int last = item.length();
        int index = last - CollectionUtils.SINGLETON;
        if (item.charAt(index) == character) {
            item.setCharAt(index, toreplace);
        }
    }

    /**
     * Replaces last character or append if such not exists
     * 
     * @param item
     * @param character
     * @param toreplace
     */
    public static void replaceOrAppend(StringBuilder item, char character, char toreplace) {

        int last = item.length();
        int index = last - CollectionUtils.SINGLETON;
        if (item.charAt(index) == character) {
            item.setCharAt(index, toreplace);
        } else {
            item.append(toreplace);
        }
    }

    /**
     * Appends "'" quotes as prefix and suffix of passed {@link Object} instance
     * 
     * @param item
     * @return {@link String} generated text
     */
    public static String quote(Object item) {
        return StringUtils.concat(QUOTE, item, QUOTE);
    }
}
