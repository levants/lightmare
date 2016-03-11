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
package org.lightmare.criteria.query.internal.layers;

import java.util.function.BiFunction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * Expressions for JPA criteria API
 * 
 * @author Levan Tsinadze
 *
 */
public interface CriteriaExpressions {

    /**
     * Binary expression with parameters for criteria function
     * 
     * @author Levan Tsinadze
     *
     */
    @FunctionalInterface
    public static interface BinaryExpression<T> {

        Predicate apply(CriteriaBuilder builder, Expression<? extends T> expression, T value);
    }

    /**
     * Comparable expressions
     * 
     * @author Levan Tsinadze
     *
     */
    @FunctionalInterface
    public static interface ComparableExpression {

        <Y extends Comparable<? super Y>> Predicate apply(CriteriaBuilder builder, Expression<? extends Y> expression,
                Y value);
    }

    /**
     * Binary expressions
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Binaries {

        EQ("equal", CriteriaBuilder::equal),

        NOT_EQ("notEqual", CriteriaBuilder::notEqual);

        public final String key;

        public final BinaryExpression<Object> function;

        private Binaries(final String key, final BinaryExpression<Object> function) {
            this.key = key;
            this.function = function;
        }
    }

    /**
     * Expressions with {@link Comparable} implementation parameters
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Comparables {

        GREATER("greaterThan", CriteriaBuilder::greaterThan),

        LESS("lessThan", CriteriaBuilder::lessThan),

        GREATER_OR_EQ("greaterThanOrEqualTo", CriteriaBuilder::greaterThanOrEqualTo),

        LESS_OR_EQ("lessThanOrEqualTo", CriteriaBuilder::lessThanOrEqualTo);

        public final String key;

        public final ComparableExpression function;

        private Comparables(final String key, final ComparableExpression function) {
            this.key = key;
            this.function = function;
        }
    }

    public static enum Numerics {

        GREATER("greaterThan", CriteriaBuilder::gt),

        LESS("lessThan", CriteriaBuilder::lt),

        GREATER_OR_EQ("greaterThanOrEqualTo", CriteriaBuilder::ge),

        LESS_OR_EQ("lessThanOrEqualTo", CriteriaBuilder::le);

        public final String key;

        public final BinaryExpression<Number> function;

        private Numerics(final String key, final BinaryExpression<Number> function) {
            this.key = key;
            this.function = function;
        }
    }

    /**
     * Unary operators
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Unaries {

        IS_NULL("isNull", CriteriaBuilder::isNull),

        NOT_NULL("isNotNull", CriteriaBuilder::isNotNull);

        public final String key;

        public final BiFunction<CriteriaBuilder, Expression<?>, Predicate> function;

        private Unaries(final String key, final BiFunction<CriteriaBuilder, Expression<?>, Predicate> function) {
            this.key = key;
            this.function = function;
        }
    }
}
