package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;

/**
 * Generates group by query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface GroupExpression<T> {

    /**
     * Gets instant field by type
     * 
     * @param field
     * @return {@link QueryStream} for field type
     */
    <F> QueryStream<F> groupByOne(EntityField<T, F> field);

    <F> QueryStream<Object[]> groupBy(EntityField<T, F> field);

    <F1, F2> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F1> field2);

    <F1, F2, F3> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3);

    <F1, F2, F3, F4> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4);

    <F1, F2, F3, F4, F5> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5);

    <F1, F2, F3, F4, F5, F6> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5, EntityField<T, F6> field6);

    <F1, F2, F3, F4, F5, F6, F7> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5, EntityField<T, F6> field6,
            EntityField<T, F7> field7);

    <F1, F2, F3, F4, F5, F6, F7, F8> QueryStream<Object[]> groupBy(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5, EntityField<T, F6> field6,
            EntityField<T, F7> field7, EntityField<T, F8> field8);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11, EntityField<T, F12> field12);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13> QueryStream<Object[]> groupBy(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11, EntityField<T, F12> field12,
            EntityField<T, F13> field13);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14> QueryStream<Object[]> groupBy(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13, EntityField<T, F14> field14);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15> QueryStream<Object[]> groupBy(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13, EntityField<T, F14> field14,
            EntityField<T, F15> field15);
}
