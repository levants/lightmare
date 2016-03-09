package org.lightmare.criteria.query.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.lightmare.criteria.query.internal.layers.CriteriaExpressions.Binaries;
import org.lightmare.criteria.query.internal.layers.CriteriaExpressions.BinaryExpression;
import org.lightmare.criteria.query.internal.layers.CriteriaProvider;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} and
 * {@link org.lightmare.criteria.query.internal.CriteriaQueryResolver} for JPA
 * criteria API
 * 
 * @author Levan Tsinadze
 *
 */
public class AbstractCriteriaStream<T> implements CriteriaQueryResolver<T> {

    protected final Class<T> entityType;

    protected final CriteriaProvider provider;

    protected final Map<Class<?>, Root<?>> roots = new HashMap<>();

    protected final List<Predicate> ands = new ArrayList<>();

    protected final List<Predicate> ors = new ArrayList<>();

    protected List<Predicate> current = ands;

    protected final CriteriaQuery<T> sql;

    public AbstractCriteriaStream(final Class<T> entityType, final EntityManager em) {
        this.entityType = entityType;
        this.provider = new CriteriaProvider(em);
        this.sql = provider.getBuilder().createQuery(entityType);
    }

    @Override
    public Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public LayerProvider getLayerProvider() {
        return provider;
    }

    /**
     * Gets or initializes {@link javax.persistence.criteria.Root} for passed
     * {@link Class} entity type
     * 
     * @param type
     * @return {@link javax.persistence.criteria.Root} for passed {@link Class}
     */
    private Root<?> getRoot(Class<?> type) {
        return ObjectUtils.thisOrDefault(roots.get(type), () -> sql.from(type), r -> roots.put(type, r));
    }

    @Override
    public String getAlias() {

        String alias;

        Root<?> root = getRoot(entityType);
        alias = root.getAlias();

        return alias;
    }

    @Override
    public String sql() {
        return sql.toString();
    }

    protected void addToCurrent(Predicate predicate) {

        current.add(predicate);
        if (Objects.equals(current, ors)) {
            current = ands;
        }
    }

    /**
     * Operates on JPA criteria query expression
     * 
     * @param tuple
     * @param function
     */
    private void operateExpression(QueryTuple tuple, BiFunction<CriteriaBuilder, Expression<?>, Predicate> function) {

        String column = provider.getColumnName(tuple);
        Class<?> type = tuple.getEntityType();
        Root<?> root = getRoot(type);
        Expression<?> exp = root.get(column);
        Predicate predicate = function.apply(provider.getBuilder(), exp);
        ObjectUtils.nonNull(predicate, this::addToCurrent);
    }

    /**
     * Operates binary operator
     * 
     * @param tuple
     * @param value
     * @param binary
     */
    protected void operateBinary(QueryTuple tuple, Object value, Binaries binary) {
        BinaryExpression<Object> function = binary.function;
        operateExpression(tuple, (c, e) -> function.apply(c, e, value));
    }
}
