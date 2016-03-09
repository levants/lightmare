package org.lightmare.criteria.query.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.lightmare.criteria.query.internal.layers.CriteriaExpressions.Binaries;
import org.lightmare.criteria.query.internal.layers.CriteriaProvider;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for JPA
 * criteria API
 * 
 * @author Levan Tsinadze
 *
 */
public class AbstractEntityCriteriaStream<T> {

    protected final Class<T> entityType;

    protected final CriteriaProvider provider;

    protected final Map<Class<?>, Root<?>> roots = new HashMap<>();

    protected final List<Predicate> ands = new ArrayList<>();

    protected final List<Predicate> ors = new ArrayList<>();

    protected final CriteriaQuery<T> sql;

    public AbstractEntityCriteriaStream(final Class<T> entityType, final EntityManager em) {
        this.entityType = entityType;
        this.provider = new CriteriaProvider(em);
        this.sql = provider.getBuilder().createQuery(entityType);
    }

    protected void and(Predicate predicate) {
        ands.add(predicate);
    }

    protected void or(Predicate predicate) {
        ors.add(predicate);
    }

    public void operate(QueryTuple tuple, String expression, Object value) {

        Binaries binary = Binaries.valueOf(expression);
        String column = provider.getColumnName(tuple);
        Root<?> root = roots.get(tuple.getEntityType());
        Expression<?> exp = root.get(column);
        Predicate predicate = binary.function.apply(provider.getBuilder(), exp, value);
        and(predicate);
    }
}
