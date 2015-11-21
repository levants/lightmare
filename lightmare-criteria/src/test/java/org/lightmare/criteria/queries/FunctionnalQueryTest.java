package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.runorder.RunOrder;

public class FunctionnalQueryTest extends GroupByQueryTest {

    @Test
    @RunOrder(500)
    public void functionToObjectQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().gt(Person::getPersonId, 100L)
                    .like(Person::getLastName, "lname").gtParam(c -> c.abs(Phone::getOperatorId), 100);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(501)
    public void functionToFunctionQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().gt(Person::getPersonId, 100L)
                    .like(Person::getLastName, "lname").gtFunction(c -> c.abs(Person::getFunctionalId),
                            s -> s.sum(Person::getPersonId, Person::getComparatorId));
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
