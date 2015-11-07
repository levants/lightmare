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

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.internal.QueryStream;

/**
 * Implementation of {@link SelectStatements} to generate SELECT for instant
 * fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class AbstractSelectStatements<T> extends AbstractResultStream<T> {

    protected AbstractSelectStatements(EntityManager em, Class<T> entityType, String alias) {
	super(em, entityType, alias);
    }

    @SafeVarargs
    private final QueryStream<Object[]> selectAll(EntityField<T, ?>... fields) {

	SelectStream<T> stream;

	oppSelect(fields);
	stream = new SelectStream<>(this);

	return stream;
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field) {
	return selectAll(field);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2) {
	return selectAll(field1, field2);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3) {
	return selectAll(field1, field2, field3);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4) {
	return selectAll(field1, field2, field3, field4);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5) {
	return selectAll(field1, field2, field3, field4, field5);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6) {
	return selectAll(field1, field2, field3, field4, field5, field6);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14,
	    EntityField<T, ?> field15) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18,
	    EntityField<T, ?> field19) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22,
	    EntityField<T, ?> field23) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25, field26);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26,
	    EntityField<T, ?> field27) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25, field26, field27);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
	    EntityField<T, ?> field28) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25, field26, field27, field28);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
	    EntityField<T, ?> field28, EntityField<T, ?> field29) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25, field26, field27, field28, field29);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
	    EntityField<T, ?> field28, EntityField<T, ?> field29, EntityField<T, ?> field30) {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25, field26, field27, field28, field29, field30);
    }
}
