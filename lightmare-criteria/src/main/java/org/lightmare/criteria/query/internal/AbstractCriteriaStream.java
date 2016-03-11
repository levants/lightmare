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
