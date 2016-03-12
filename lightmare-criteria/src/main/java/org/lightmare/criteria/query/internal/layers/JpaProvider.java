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
package org.lightmare.criteria.query.internal.layers;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.layers.QueryLayer;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation for
 * {@link org.lightmare.criteria.query.layers.LayerProvider} JPA
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
    public <T> QueryLayer<T> query(Object sql, Class<T> type) {
        return new JpaQueryLayer<>(em, sql.toString(), type);
    }

    @Override
    public QueryLayer<?> query(Object sql) {
        return new JpaQueryLayer<>(em, sql.toString());
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
        return alias;
    }

    @Override
    public String getCountType(String alias) {
        return alias;
    }

    @Override
    public String alias() {
        return JpaQueryStream.DEFAULT_ALIAS;
    }

    @Override
    public void close() {
        em.close();
    }
}
