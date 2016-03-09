package org.lightmare.criteria.query.internal.layers;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.internal.layers.LayerProvider} for
 * criteria API
 * 
 * @author Levan Tsinadze
 *
 */
public class CriteriaProvider extends JpaLayerExpressions implements LayerProvider {

    private final EntityManager em;

    private final CriteriaBuilder builder;

    public CriteriaProvider(final EntityManager em) {
        this.em = em;
        this.builder = em.getCriteriaBuilder();
    }

    public CriteriaBuilder getBuilder() {
        return builder;
    }

    @Override
    public <T> QueryLayer<T> query(Object sql, Class<T> type) {
        CriteriaQuery<T> query = ObjectUtils.cast(sql);
        ObjectUtils.nonNull(type, query::from);
        return new CriteriaQueryLayer<>(em, query);
    }

    @Override
    public QueryLayer<?> query(Object sql) {
        return query(sql, null);
    }

    @Override
    public String getTableName(Class<?> type) {
        return ObjectUtils.ifNull(() -> type.getAnnotation(Entity.class), c -> type.getName(),
                c -> StringUtils.thisOrDefault(c.name(), type::getName));
    }

    @Override
    public String getColumnName(QueryTuple tuple) {
        return tuple.getFieldName();
    }

    @Override
    public String getSelectType(String alias) {
        return null;
    }

    @Override
    public String getCountType(String alias) {
        return null;
    }

    @Override
    public String alias() {
        return null;
    }

    @Override
    public void close() {
        em.close();
    }
}
