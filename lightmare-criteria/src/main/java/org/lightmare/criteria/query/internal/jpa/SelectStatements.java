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
package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;

/**
 * Interface to generate SELECT for instant fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface SelectStatements<T> {

    QueryStream<Object[]> select(EntityField<T, ?> field);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24, EntityField<T, ?> field25);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
            EntityField<T, ?> field28);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
            EntityField<T, ?> field28, EntityField<T, ?> field29);

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
            EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
            EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
            EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
            EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
            EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
            EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
            EntityField<T, ?> field28, EntityField<T, ?> field29, EntityField<T, ?> field30);
}
