package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.QueryConsumer;

/**
 * Provider class for sub query types
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type
 */
public interface SubQuery<T> {

    /**
     * Query for ALL clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter
     */
    public static final class All<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private All(final Class<T> type, final QueryConsumer<T> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        public Class<T> getType() {
            return type;
        }

        public QueryConsumer<T> getConsumer() {
            return consumer;
        }
    }

    /**
     * Query for ANY clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter
     */
    public static final class Any<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private Any(final Class<T> type, final QueryConsumer<T> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        public Class<T> getType() {
            return type;
        }

        public QueryConsumer<T> getConsumer() {
            return consumer;
        }
    }

    /**
     * Query for SOME clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter
     */
    public static final class Some<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private Some(final Class<T> type, final QueryConsumer<T> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        public Class<T> getType() {
            return type;
        }

        public QueryConsumer<T> getConsumer() {
            return consumer;
        }
    }

    /**
     * Provides sub query for ANY clause
     * 
     * @param type
     * @param consumer
     * @return {@link All} all sub query stream
     */
    static <S> All<S> all(Class<S> type, QueryConsumer<S> consumer) {
        return new All<S>(type, consumer);
    }

    /**
     * Provides sub query for ALL clause
     * 
     * @param type
     * @param consumer
     * @return {@link Any} all sub query stream
     */
    static <S> Any<S> any(Class<S> type, QueryConsumer<S> consumer) {
        return new Any<S>(type, consumer);
    }

    /**
     * Provides sub query for SOME clause
     * 
     * @param type
     * @param consumer
     * @return {@link Some} all sub query stream
     */
    static <S> Some<S> some(Class<S> type, QueryConsumer<S> consumer) {
        return new Some<S>(type, consumer);
    }
}
