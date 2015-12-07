package org.lightmare.criteria.tuples;

/**
 * Tuple for entity field (column) resolver
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            type parameter for entity name resolvers
 */
public class ResolverTuple<T> {

    private final String desc;

    private final String name;

    private final T type;

    private ResolverTuple(final String desc, final String name, final T type) {
        this.desc = desc;
        this.name = name;
        this.type = type;
    }

    public static <T> ResolverTuple<T> of(final String desc, final String name, final T type) {
        return new ResolverTuple<T>(desc, name, type);
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public T getType() {
        return type;
    }
}
