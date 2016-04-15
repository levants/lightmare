package org.lightmare.criteria.query.orm.links;

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;

/**
 * Special class to implement ON clause
 * 
 * @author Levan Tsinadze
 *
 * @param <E>
 *            entity type parameter
 * @param <S>
 *            ON clause stream type parameter
 */
public final class On<E, S extends LambdaStream<E, ? super S>> {

    private final QueryConsumer<E, S> on;

    private On(final QueryConsumer<E, S> on) {
        this.on = on;
    }

    public static <E, S extends LambdaStream<E, ? super S>> On<E, S> on(final QueryConsumer<E, S> on) {
        return new On<E, S>(on);
    }

    public QueryConsumer<E, S> getConsumer() {
        return on;
    }
}
