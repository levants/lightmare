package org.lightmare.criteria.query.internal.jpa.subqueries;

import org.lightmare.criteria.functions.QueryConsumer;

/**
 * Factory class for sub query provider
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public final class SubQueryProvider<T> {

    /**
     * Query for ALL clause
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            entity type parameter
     */
    public static final class AllQueryStream<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private AllQueryStream(final Class<T> type, final QueryConsumer<T> consumer) {
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
    public static final class AnyQueryStream<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private AnyQueryStream(final Class<T> type, final QueryConsumer<T> consumer) {
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
    public static final class SomeQueryStream<T> {

        private final Class<T> type;

        private final QueryConsumer<T> consumer;

        private SomeQueryStream(final Class<T> type, final QueryConsumer<T> consumer) {
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

    private SubQueryProvider() {
    }

    /**
     * Provides sub query for ANY clause
     * 
     * @param type
     * @param consumer
     * @return {@link AllQueryStream} all sub query stream
     */
    public static <S> AllQueryStream<S> all(Class<S> type, QueryConsumer<S> consumer) {
        return new AllQueryStream<S>(type, consumer);
    }

    /**
     * Provides sub query for ALL clause
     * 
     * @param type
     * @param consumer
     * @return {@link AnyQueryStream} all sub query stream
     */
    public static <S> AnyQueryStream<S> any(Class<S> type, QueryConsumer<S> consumer) {
        return new AnyQueryStream<S>(type, consumer);
    }

    /**
     * Provides sub query for SOME clause
     * 
     * @param type
     * @param consumer
     * @return {@link SomeQueryStream} all sub query stream
     */
    public static <S> SomeQueryStream<S> some(Class<S> type, QueryConsumer<S> consumer) {
        return new SomeQueryStream<S>(type, consumer);
    }
}
