package org.lightmare.criteria.queries;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.query.JpaQueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.SelectExpression.Select;
import org.lightmare.criteria.runorder.RunOrder;

public class GroupByQueryTest extends EmbeddedQueryTest {

    @Test
    @RunOrder(400)
    public void groupSelectTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname").count(Person::getPersonalNo,
                            c -> c.groupBy(Select.select().column(Person::getLastName).column(Person::getFirstName))
                                    .having(h -> h.greaterThenOrEqualTo(100).lessThenOrEqualTo(1000).or()
                                            .brackets(b -> b.notBetween(20000, 30000))));
            List<Object[]> results = stream.toList();
            results.forEach(result -> System.out.println(Arrays.asList(result)));
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
    public void groupSelectConsumerTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname").count(Person::getPersonalNo,
                            c -> c.group(s -> s.column(Person::getLastName).column(Person::getFirstName))
                                    .having(h -> h.greaterThenOrEqualTo(100).lessThenOrEqualTo(1000).or()
                                            .brackets(b -> b.notBetween(20000, 30000))));
            List<Object[]> results = stream.toList();
            results.forEach(result -> System.out.println(Arrays.asList(result)));
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
    public void groupSelectOneConsumerTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname").count(Person::getPersonalNo,
                            c -> c.group(s -> s.column(Person::getLastName)).having(h -> h.greaterThenOrEqualTo(100)
                                    .lessThenOrEqualTo(1000).or().brackets(b -> b.notBetween(20000, 30000))));
            List<Object[]> results = stream.toList();
            results.forEach(result -> System.out.println(Arrays.asList(result)));
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
    @RunOrder(403)
    public void groupByTypeTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .like(Person::getLastName, "lname").count(Person::getPersonalNo,
                            c -> c.groupBy(Person::getLastName).having(h -> h.greaterThenOrEqualTo(100)
                                    .lessThenOrEqualTo(1000).or().brackets(b -> b.notBetween(20000, 30000))));
            List<Object[]> results = stream.toList();
            results.forEach(result -> System.out.println(Arrays.asList(result)));
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
