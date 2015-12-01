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
package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.internal.jpa.links.Parts;
import org.lightmare.criteria.query.internal.jpa.links.Texts;
import org.lightmare.criteria.query.internal.jpa.links.Trimspec;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Text function methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface TextFunction<T> {

    /**
     * Generates functional expression
     * 
     * @param function
     * @param x
     * @param y
     * @param z
     * @return {@link JPAFunction} current instance
     */
    JPAFunction<T> operateText(String function, Object x, Object y, Object z);

    /**
     * Generates text function body
     * 
     * @param function
     * @param prefix
     * @param x
     * @param pattern
     * @param y
     * @return {@link JPAFunction} current instance
     */
    JPAFunction<T> generateText(String function, String prefix, Object x, String pattern, Object y);

    /**
     * Generates text function body
     * 
     * @param function
     * @param pattern
     * @param y
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> generateText(String function, String pattern, Object y) {
        return generateText(function, StringUtils.EMPTY_STRING, null, pattern, y);
    }

    /**
     * Generates functional expression
     * 
     * @param function
     * @param x
     * @param y
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> operateText(String function, Object x, Object y) {
        return operateText(function, x, y, null);
    }

    /**
     * Generates text functional expression
     * 
     * @param function
     * @param x
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> operateText(String function, Object x) {
        return operateText(function, x, null, null);
    }

    /**
     * Create an expression for string concatenation.
     *
     * @param x
     *            string expression
     * @param y
     *            string expression
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> concat(EntityField<T, String> x, EntityField<T, String> y) {
        return operateText(Texts.CONCAT, x, y);
    }

    /**
     * Create an expression for string concatenation.
     *
     * @param x
     *            string expression
     * @param y
     *            string
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> concat(EntityField<T, String> x, String y) {
        return operateText(Texts.CONCAT, x, StringUtils.qlize(y));
    }

    /**
     * Create an expression for string concatenation.
     *
     * @param x
     *            string
     * @param y
     *            string expression
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> concat(String x, EntityField<T, String> y) {
        return operateText(Texts.CONCAT, StringUtils.qlize(x), y);
    }

    /**
     * Create an expression for substring extraction. Extracts a substring
     * starting at the specified position through to end of the string. First
     * position is 1.
     *
     * @param x
     *            string expression
     * @param from
     *            start position
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> substring(EntityField<T, String> x, int from) {
        return operateText(Texts.SUBSTRING, x, from);
    }

    /**
     * Create an expression for substring extraction. Extracts a substring of
     * given length starting at the specified position. First position is 1.
     *
     * @param x
     *            string expression
     * @param from
     *            start position
     * @param len
     *            length
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> substring(EntityField<T, String> x, int from, int len) {
        return operateText(Texts.SUBSTRING, x, from, len);
    }

    /**
     * Create expression to trim blanks from both ends of a string.
     *
     * @param x
     *            expression for string to trim
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> trim(EntityField<T, String> x) {
        return operateText(Texts.TRIM, x);
    }

    /**
     * Create expression to trim blanks from a string.
     *
     * @param ts
     *            trim specification
     * @param x
     *            expression for string to trim
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> trim(Trimspec ts, EntityField<T, String> x) {
        return operateText(Texts.TRIM, ts.pattern, x);
    }

    /**
     * Create expression to trim character from both ends of a string.
     *
     * @param t
     *            expression for character to be trimmed
     * @param x
     *            expression for string to trim
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> trim(EntityField<T, Character> t, EntityField<T, String> x) {
        return generateText(Texts.TRIM, StringUtils.EMPTY_STRING, t, Parts.TRIM_FROM, x);
    }

    /**
     * Create expression to trim character from both ends of a string.
     *
     * @param t
     *            expression for character to be trimmed
     * @param ts
     *            trim specification
     * @param x
     *            expression for string to trim
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> trim(EntityField<T, Character> t, Trimspec ts, EntityField<T, String> x) {
        return generateText(Texts.TRIM, ts.prefix, t, Parts.TRIM_FROM, x);
    }

    /**
     * Create expression to trim character from both ends of a string.
     *
     * @param t
     *            character to be trimmed
     * @param x
     *            expression for string to trim
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> trim(char ch, EntityField<T, String> x) {
        return operateText(Texts.TRIM, Trimspec.locateAll(ch), x);
    }

    /**
     * Create expression to trim character from both ends of a string.
     *
     * @param t
     *            character to be trimmed
     * @param ts
     *            trim specification
     * @param x
     *            expression for string to trim
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> trim(char ch, Trimspec ts, EntityField<T, String> x) {
        return generateText(Texts.TRIM, ts.locate(ch), x);
    }

    /**
     * Create expression for converting a string to lower case.
     *
     * @param x
     *            string expression
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> lower(EntityField<T, String> x) {
        return operateText(Texts.LOWER, x);
    }

    /**
     * Create expression for converting a string to upper case.
     *
     * @param x
     *            string expression
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> upper(EntityField<T, String> x) {
        return operateText(Texts.UPPER, x);
    }

    /**
     * Create expression to return length of a string.
     *
     * @param x
     *            string expression
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> length(EntityField<T, String> x) {
        return operateText(Texts.LENGTH, x);
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            expression for string to be located
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> locate(EntityField<T, String> x, EntityField<T, String> pattern) {
        return operateText(Texts.LOCATE, x, pattern);
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            string to be located
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> locate(EntityField<T, String> x, String pattern) {
        return operateText(Texts.LOCATE, x, StringUtils.qlize(pattern));
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            expression for string to be located
     * @param from
     *            expression for position at which to start search
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> locate(EntityField<T, String> x, EntityField<T, String> pattern, int from) {
        return operateText(Texts.LOCATE, pattern, x, from);
    }
}
