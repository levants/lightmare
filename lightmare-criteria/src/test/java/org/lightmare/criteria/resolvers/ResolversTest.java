package org.lightmare.criteria.resolvers;

import java.util.Date;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.lambda.LambdaUtils;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.tuples.QueryTuple;

public class ResolversTest {

    @Test
    @RunOrder(0)
    public void resolverTest() {

        try {
            EntityField<Person, Date> field1 = Person::getBirthDate;
            EntityField<Person, Date> field2 = c -> c.getBirthDate();
            QueryTuple tuple1 = LambdaUtils.getOrInit(field1);
            QueryTuple tuple2 = LambdaUtils.getOrInit(field2);
            System.out.format("tuple1 - %s tuple2 - %s\n", tuple1, tuple2);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
