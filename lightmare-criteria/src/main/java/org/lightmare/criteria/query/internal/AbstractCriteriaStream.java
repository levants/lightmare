package org.lightmare.criteria.query.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.internal.layers.CriteriaExpressions.Binaries;
import org.lightmare.criteria.query.internal.layers.CriteriaExpressions.ParamFunction;
import org.lightmare.criteria.query.internal.layers.CriteriaExpressions.Unaries;
import org.lightmare.criteria.query.internal.layers.CriteriaProvider;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for JPA
 * criteria API
 * 
 * @author Levan Tsinadze
 *
 */
public class AbstractCriteriaStream<T> implements QueryResolver<T> {

    protected final Class<T> entityType;

    protected final CriteriaProvider provider;

    protected final Map<Class<?>, Root<?>> roots = new HashMap<>();

    protected final List<Predicate> ands = new ArrayList<>();

    protected final List<Predicate> ors = new ArrayList<>();

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

    protected void and(Predicate predicate) {
        ands.add(predicate);
    }

    protected void or(Predicate predicate) {
        ors.add(predicate);
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
        ObjectUtils.nonNull(predicate, this::and);
    }

    @Override
    public void operate(QueryTuple tuple, String expression) {
        Unaries unary = Unaries.valueOf(expression);
        ObjectUtils.nonNull(unary, u -> operateExpression(tuple, u.function));
    }

    /**
     * Operates binary operator
     * 
     * @param tuple
     * @param value
     * @param binary
     */
    private void operateBinary(QueryTuple tuple, Object value, Binaries binary) {
        ParamFunction function = binary.function;
        operateExpression(tuple, (c, e) -> function.apply(c, e, value));
    }

    @Override
    public void operate(QueryTuple tuple, String expression, Object value) {
        Binaries binary = Binaries.valueOf(expression);
        ObjectUtils.nonNull(binary, b -> operateBinary(tuple, value, b));
    }
}
