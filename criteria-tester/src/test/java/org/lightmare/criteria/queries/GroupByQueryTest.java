package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.runorder.RunOrder;

public class GroupByQueryTest extends EmbeddedQueryTest {

    @Test
    @RunOrder(400)
    public void groupQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = QueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .count(Person::getPersonalNo, c -> c.groupBy(Person::getLastName, Person::getFirstName));
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
    @RunOrder(401)
    public void groupWithAggregateQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = QueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .max(Person::getPersonId, c -> c.groupBy(Person::getLastName, Person::getFirstName)
                            .having(h -> h.greaterThenOrEqualTo(100)));
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
    @RunOrder(402)
    public void groupWithHavingQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = QueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname")
                    .count(Person::getPersonalNo, c -> c.groupBy(Person::getLastName, Person::getFirstName)
                            .having(h -> h.greaterThenOrEqualTo(100)));
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
