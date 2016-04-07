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
package org.lightmare.criteria.query.internal.orm.links;

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Provider class for sub query (ALL, ANY, SOME) clauses
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
     * @param <U>
     *            {@link org.lightmare.criteria.query.LambdaStream}
     *            implementation parameter
     */
    static abstract class SubQueryType<T, U extends LambdaStream<T, ? super U>> {

        private final Class<T> type;

        private final QueryConsumer<T, U> consumer;

        private final String operator;

        private SubQueryType(final Class<T> type, final QueryConsumer<T, U> consumer, final String operator) {
            this.type = type;
            this.consumer = consumer;
            this.operator = operator;
        }

        public Class<T> getType() {
            return type;
        }

        public QueryConsumer<T, U> getConsumer() {
            return consumer;
        }

        public String getOperator(String other) {
            return StringUtils.concat(other, operator);
        }
    }

    /**
     * Query for ALL sub query clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     * @param <S>
     *            {@link org.lightmare.criteria.query.LambdaStream}
     *            implementation parameter
     */
    static final class All<T, S extends LambdaStream<T, ? super S>> extends SubQueryType<T, S> {

        private All(final Class<T> type, final QueryConsumer<T, S> consumer) {
            super(type, consumer, Operators.ALL);
        }
    }

    /**
     * Query for ANY sub query clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     * @param <S>
     *            {@link org.lightmare.criteria.query.LambdaStream}
     *            implementation parameter
     */
    static final class Any<T, S extends LambdaStream<T, ? super S>> extends SubQueryType<T, S> {

        private Any(final Class<T> type, final QueryConsumer<T, S> consumer) {
            super(type, consumer, Operators.ANY);
        }
    }

    /**
     * Query for SOME sub query clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter for sub query
     * @param <S>
     *            {@link org.lightmare.criteria.query.LambdaStream}
     *            implementation parameter
     */
    static final class Some<T, S extends LambdaStream<T, ? super S>> extends SubQueryType<T, S> {

        private Some(final Class<T> type, final QueryConsumer<T, S> consumer) {
            super(type, consumer, Operators.SOME);
        }
    }

    /**
     * Provides sub query for ANY clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.links.SubQuery.All}
     *         ALL clause sub query stream
     */
    static <S, Q extends LambdaStream<S, ? super Q>> All<S, Q> all(Class<S> type, QueryConsumer<S, Q> consumer) {
        return new All<S, Q>(type, consumer);
    }

    /**
     * Provides sub query for ALL clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.links.SubQuery.Any}
     *         ANY clause sub query stream
     */
    static <S, Q extends LambdaStream<S, ? super Q>> Any<S, Q> any(Class<S> type, QueryConsumer<S, Q> consumer) {
        return new Any<S, Q>(type, consumer);
    }

    /**
     * Provides sub query for SOME clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.links.SubQuery.Some}
     *         SOME clause sub query stream
     */
    static <S, Q extends LambdaStream<S, ? super Q>> Some<S, Q> some(Class<S> type, QueryConsumer<S, Q> consumer) {
        return new Some<S, Q>(type, consumer);
    }
}
