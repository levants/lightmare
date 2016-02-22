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

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Provider class for sub query types (ALL, ANY, SOME)
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for sub query
 */
public interface SubQuery<T> {

    /**
     * Abstract class to generate ALL, ANY and SOME sub query clauses
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     */
    static abstract class SubQueryType<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private final String operator;

        private SubQueryType(final Class<T> type, final QueryConsumer<T> consumer, final String operator) {
            this.type = type;
            this.consumer = consumer;
            this.operator = operator;
        }

        public Class<T> getType() {
            return type;
        }

        public QueryConsumer<T> getConsumer() {
            return consumer;
        }

        public String getOperator(String other) {
            return StringUtils.concat(other, operator);
        }
    }

    /**
     * Query for ALL clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     */
    static final class All<T> extends SubQueryType<T> {

        private All(final Class<T> type, final QueryConsumer<T> consumer) {
            super(type, consumer, Operators.ALL);
        }
    }

    /**
     * Query for ANY clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     */
    static final class Any<T> extends SubQueryType<T> {

        private Any(final Class<T> type, final QueryConsumer<T> consumer) {
            super(type, consumer, Operators.ANY);
        }
    }

    /**
     * Query for SOME clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     */
    static final class Some<T> extends SubQueryType<T> {

        private Some(final Class<T> type, final QueryConsumer<T> consumer) {
            super(type, consumer, Operators.SOME);
        }
    }

    /**
     * Provides sub query for ANY clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.jpa.SubQuery.All}
     *         all sub query stream
     */
    static <S> All<S> all(Class<S> type, QueryConsumer<S> consumer) {
        return new All<S>(type, consumer);
    }

    /**
     * Provides sub query for ALL clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.jpa.SubQuery.Any}
     *         all sub query stream
     */
    static <S> Any<S> any(Class<S> type, QueryConsumer<S> consumer) {
        return new Any<S>(type, consumer);
    }

    /**
     * Provides sub query for SOME clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.jpa.SubQuery.Some}
     *         all sub query stream
     */
    static <S> Some<S> some(Class<S> type, QueryConsumer<S> consumer) {
        return new Some<S>(type, consumer);
    }
}
