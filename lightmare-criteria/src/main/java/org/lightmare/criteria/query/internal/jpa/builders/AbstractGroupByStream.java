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
package org.lightmare.criteria.query.internal.jpa.builders;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.query.QueryStream;

/**
 * Abstract utility class for GROUP BY processing
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractGroupByStream<T> extends AbstractSelectStatements<T> {

    protected SelectStream<T, Object[]> selectStream;

    protected AbstractGroupByStream(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    @Override
    public void having(HavingConsumer<T> consumer) {

    }

    /**
     * Processes select method call for all arguments
     * 
     * @param fields
     * @return {@link QueryStream} for select method
     */
    @SafeVarargs
    private final QueryStream<Object[]> groupByAll(Serializable... fields) {

        oppGroups(fields);
        selectStream = new SelectStream<>(this, Object[].class);

        return selectStream;
    }

    @Override
    public <F> QueryStream<F> groupByOne(EntityField<T, F> field) {

        SelectStream<T, F> stream;

        oppSelect(field);
        Class<F> fieldType = getFieldType(field);
        stream = new SelectStream<>(this, fieldType);

        return stream;
    }

    @Override
    public <F> QueryStream<Object[]> groupBy(EntityField<T, F> field) {
        return groupByAll(field);
    }

    @Override
    public <F1, F2> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F1> field2) {
        return groupByAll(field1, field2);
    }

    @Override
    public <F1, F2, F3> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3) {
        return groupByAll(field1, field2, field3);
    }

    @Override
    public <F1, F2, F3, F4> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4) {
        return groupByAll(field1, field2, field3, field4);
    }

    @Override
    public <F1, F2, F3, F4, F5> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5) {
        return groupByAll(field1, field2, field3, field4, field5);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6) {
        return groupByAll(field1, field2, field3, field4, field5, field6);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11, EntityField<T, F12> field12) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                field12);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13> QueryStream<Object[]> groupBy(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                field12, field13);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14> QueryStream<Object[]> groupBy(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13, EntityField<T, F14> field14) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                field12, field13, field14);
    }

    @Override
    public <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15> QueryStream<Object[]> groupBy(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13, EntityField<T, F14> field14,
            EntityField<T, F15> field15) {
        return groupByAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                field12, field13, field14, field15);
    }
}
