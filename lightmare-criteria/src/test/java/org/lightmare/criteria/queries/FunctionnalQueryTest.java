package org.lightmare.criteria.queries;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.orm.links.Trimspec;
import org.lightmare.criteria.query.providers.jpa.JpaQueryProvider;
import org.lightmare.criteria.query.providers.jpa.JpaQueryStream;
import org.lightmare.criteria.runorder.RunOrder;

public class FunctionnalQueryTest extends GroupByQueryTest {

    @Test
    @RunOrder(500)
    public void functionToObjectQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname")
                    .gtParam(c -> c.abs(Phone::getOperatorId), 100);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(500.5)
    public void functionToFunctionQueryEqTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname")
                    .eqParam(c -> c.abs(Phone::getOperatorId), 100);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
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
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname")
                    .gtFunction(c -> c.abs(Person::getFunctionalId),
                            s -> s.sum(Person::getPersonId, Person::getComparatorId));
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(502)
    public void functionDateQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname")
                    .gtFunction(c -> c.abs(Person::getFunctionalId),
                            s -> s.sum(Person::getPersonId, Person::getComparatorId))
                    .gtParam(c -> c.currentDate(), new Date());
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(502)
    public void functionDateColumnTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname")
                    .gtFunction(c -> c.abs(Person::getFunctionalId),
                            s -> s.sum(Person::getPersonId, Person::getComparatorId))
                    .gtColumn(c -> c.currentDate(), Person::getBirthDate)
                    .leColumn(c -> c.abs(Person::getPersonId), Person::getPersonId);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(503)
    public void functionTextColumnTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname%")
                    .gtFunction(c -> c.trim(Person::getPersonalNo),
                            s -> s.concat(Person::getLastName, Person::getFirstName))
                    .gtColumn(c -> c.currentDate(), Person::getBirthDate)
                    .leColumn(c -> c.abs(Person::getPersonId), Person::getPersonId);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(503)
    public void functionTextTrimColumnTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname%")
                    .gtFunction(c -> c.trim(Person::getPersonalNo), s -> s.trim(Person::getEscape, Person::getLastName))
                    .gtColumn(c -> c.currentDate(), Person::getBirthDate)
                    .leColumn(c -> c.abs(Person::getPersonId), Person::getPersonId);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
            Person person = stream.firstOrDefault(new Person());
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println(person);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(504)
    public void functionTextTrimValueColumnTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname%")
                    .gtFunction(c -> c.trim(Person::getPersonalNo),
                            s -> s.trim(Person::getEscape, Trimspec.LEADING, Person::getLastName))
                    .gtColumn(c -> c.currentDate(), Person::getBirthDate)
                    .leColumn(c -> c.abs(Person::getPersonId), Person::getPersonId);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
            Person person = stream.firstOrDefault(new Person());
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println(person);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(505)
    public void functionTextTrimValueTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .gt(Person::getPersonId, 100L).like(Person::getLastName, "lname%")
                    .gtFunction(c -> c.trim(Person::getPersonalNo),
                            s -> s.trim('l', Trimspec.LEADING, Person::getLastName))
                    .gtColumn(c -> c.currentDate(), Person::getBirthDate)
                    .leColumn(c -> c.abs(Person::getPersonId), Person::getPersonId);
            String sql = stream.sql();
            System.out.println("===========JPA-QL==========");
            System.out.println(sql);
            Person person = stream.firstOrDefault(new Person());
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println(person);
        } finally {
            em.close();
        }
    }
}
