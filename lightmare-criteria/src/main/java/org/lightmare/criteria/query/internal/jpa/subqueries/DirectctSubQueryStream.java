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
package org.lightmare.criteria.query.internal.jpa.subqueries;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.internal.EntityQueryStream;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            entity type parameter for generated (sub) query
 * @param <T>
 *            entity type parameter for generated query
 */
abstract class DirectctSubQueryStream<S, T> extends EntityQueryStream<S> implements SubQueryStream<S, T> {

    protected DirectctSubQueryStream(EntityManager em, Class<S> entityType, String alias) {
        super(em, entityType, alias);
    }

    // ================= entity QL methods ===================================//

    @Override
    public <F> SubQueryStream<S, T> operate(EntityField<S, F> field, String operator) {
        super.operate(field, operator);
        return this;
    }

    @Override
    public <F> SubQueryStream<S, T> operate(EntityField<S, F> field, F value, String operator) {
        super.operate(field, value, operator);
        return this;
    }

    @Override
    public <F> SubQueryStream<S, T> operateCollection(EntityField<S, F> field, Collection<F> values, String operator) {
        super.oppCollection(field, values, operator);
        return this;
    }

    // ========================= Entity self method composers ===============//

    @Override
    public <F> SubQueryStream<S, T> operateFl(EntityField<S, F> field1, EntityField<S, F> field2, String operator) {
        super.operateFl(field1, field2, operator);
        return this;
    }

    @Override
    public <F> SubQueryStream<S, T> operateCollectionFl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2,
            String operator) {
        appendOperator();
        oppCollectionField(field1, field2, operator);

        return this;
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> SubQueryStream<S, T> embedded(EntityField<S, F> field, SubQueryConsumer<F, S> consumer) {
        super.embedded(field, consumer);
        return this;
    }

    // =========================sub=queries==================================//

    @Override
    public <K> SubQueryStream<S, T> subQuery(Class<K> subType, SubQueryConsumer<K, S> consumer) {
        super.subQuery(subType, consumer);
        return this;
    }

    @Override
    public <F, K> SubQueryStream<S, T> in(EntityField<S, F> field, Class<K> subType, SubQueryConsumer<K, S> consumer) {
        super.in(field, subType, consumer);
        return this;
    }

    @Override
    public <F, K> SubQueryStream<S, T> notIn(EntityField<S, F> field, Class<K> subType,
            SubQueryConsumer<K, S> consumer) {
        super.notIn(field, subType, consumer);
        return this;
    }

    @Override
    public <F, K> SubQueryStream<S, T> exists(Class<K> subType, SubQueryConsumer<K, S> consumer) {
        super.exists(subType, consumer);
        return this;
    }

    @Override
    public <F, K> SubQueryStream<S, T> notExists(Class<K> subType, SubQueryConsumer<K, S> consumer) {
        super.notExists(subType, consumer);
        return this;
    }

    // =========================sub=queries==================================//

    // =========================order=by=====================================//

    @Override
    public <F> SubQueryStream<S, T> orderBy(EntityField<S, F> field) {
        super.orderBy(field);
        return this;
    }

    @Override
    public <F> SubQueryStream<S, T> orderByDesc(EntityField<S, F> field) {
        super.orderByDesc(field);
        return this;
    }

    // ======================================================================//

    // ================= entity QL Filters ===================================//

    @Override
    public SubQueryStream<S, T> where() {
        super.where();
        return this;
    }

    @Override
    public SubQueryStream<S, T> brackets(QueryConsumer<S> consumer) {
        super.brackets(consumer);
        return this;
    }

    // ================= entity QL Filters ==================================//

    // ================= methods for custom QL ==============================//

    @Override
    public SubQueryStream<S, T> appendPrefix(Object clause) {
        super.appendPrefix(clause);
        return this;
    }

    @Override
    public SubQueryStream<S, T> appendBody(Object clause) {
        super.appendBody(clause);
        return this;
    }
}
