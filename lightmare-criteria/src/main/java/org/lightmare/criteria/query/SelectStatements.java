package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;

import org.lightmare.criteria.lambda.EntityField;

interface SelectStatements<T extends Serializable> {

    QueryStream<Object[]> select(EntityField<T, ?> field) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3)
	    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7)
		    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11)
		    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15)
		    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19)
		    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23)
		    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27)
		    throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
	    EntityField<T, ?> field28) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
	    EntityField<T, ?> field28, EntityField<T, ?> field29) throws IOException;

    QueryStream<Object[]> select(EntityField<T, ?> field1, EntityField<T, ?> field2, EntityField<T, ?> field3,
	    EntityField<T, ?> field4, EntityField<T, ?> field5, EntityField<T, ?> field6, EntityField<T, ?> field7,
	    EntityField<T, ?> field8, EntityField<T, ?> field9, EntityField<T, ?> field10, EntityField<T, ?> field11,
	    EntityField<T, ?> field12, EntityField<T, ?> field13, EntityField<T, ?> field14, EntityField<T, ?> field15,
	    EntityField<T, ?> field16, EntityField<T, ?> field17, EntityField<T, ?> field18, EntityField<T, ?> field19,
	    EntityField<T, ?> field20, EntityField<T, ?> field21, EntityField<T, ?> field22, EntityField<T, ?> field23,
	    EntityField<T, ?> field24, EntityField<T, ?> field25, EntityField<T, ?> field26, EntityField<T, ?> field27,
	    EntityField<T, ?> field28, EntityField<T, ?> field29, EntityField<T, ?> field30) throws IOException;
}
