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
package org.lightmare.criteria.query.jpa;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.lambda.EntityField;
import org.lightmare.criteria.query.QueryStream;

/**
 * Implementation of {@link SelectStatements} to generate SELECT for instant
 * fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class AbstractSelectStatements<T extends Serializable> extends AbstractResultStream<T> {

    protected AbstractSelectStatements(EntityManager em, Class<T> entityType, String alias) {
	super(em, entityType, alias);
    }

    @SafeVarargs
    private final QueryStream<Object[]> selectAll(EntityField<T, ?>... fields) throws IOException {

	SelectStream<T> stream;

	oppSelect(fields);
	stream = new SelectStream<>(this);

	return stream;
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field) throws IOException {
	return selectAll(field);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2) throws IOException {
	return selectAll(field1, field2);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3)
	    throws IOException {
	return selectAll(field1, field2, field3);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4) throws IOException {
	return selectAll(field1, field2, field3, field4);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5) throws IOException {
	return selectAll(field1, field2, field3, field4, field5);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7)
		    throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11)
		    throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15)
		    throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19)
		    throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    public QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23)
		    throws IOException {
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
	    EntityField<T, ?> field24) throws IOException {
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
	    EntityField<T, ?> field24, EntityField<T, ?> field25) throws IOException {
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
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26) throws IOException {
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
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27)
		    throws IOException {
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
	    EntityField<T, ?> field28) throws IOException {
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
	    EntityField<T, ?> field28, EntityField<T, ?> field29) throws IOException {
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
	    EntityField<T, ?> field28, EntityField<T, ?> field29, EntityField<T, ?> field30) throws IOException {
	return selectAll(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
		field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
		field23, field24, field25, field26, field27, field28, field29, field30);
    }
}
