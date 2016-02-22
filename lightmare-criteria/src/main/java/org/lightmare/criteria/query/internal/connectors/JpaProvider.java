package org.lightmare.criteria.query.internal.connectors;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation for
 * {@link org.lightmare.criteria.query.internal.connectors.LayerProvider} JPA
 * queries
 * 
 * @author Levan Tsinadze
 *
 */
public class JpaProvider implements LayerProvider {

    private final EntityManager em;

    public JpaProvider(final EntityManager em) {
        this.em = em;
    }

    @Override
    public <T> QueryLayer<T> query(String sql, Class<T> type) {
        return new JpaQueryLayer<>(em, sql, type);
    }

    @Override
    public QueryLayer<?> query(String sql) {
        return new JpaQueryLayer<>(em, sql);
    }

    @Override
    public String getTableName(Class<?> type) {
        return ObjectUtils.ifNull(() -> type.getAnnotation(Entity.class), c -> type.getName(),
                c -> StringUtils.thisOrDefault(c.name(), type::getName));
    }

    @Override
    public String getSelectType(String alias) {
        return alias;
    }

    @Override
    public String getCountType(String alias) {
        return alias;
    }

    @Override
    public void close() {
        em.close();
    }
}
