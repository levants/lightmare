package org.lightmare.criteria.queries;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class QueryTest extends TestEnviromentConfig {

    @Test
    @RunOrder(1)
    public void supplierEntityTest() {

        EntityManager em = emf.createEntityManager();
        try {
            System.out.println();
            System.out.println("==========Entity============");
            QueryStream<Person> stream = createQueryStream(em);
            System.out.println("===========JPA-QL==========");
            System.out.println();
            System.out.println(stream.sql());
            System.out.println("===========Entity==========");
            System.out.println();
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(2)
    public void toListByEntityTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            List<Person> persons = QueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname").and()
                    .startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date).and()
                    .in(Person::getPersonId, Arrays.asList(IDENTIFIERS)).toList();
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            persons.forEach(System.out::println);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(2.3)
    public void toListBySelectTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            List<Object[]> persons = QueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname").and()
                    .brackets(stream -> stream.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate,
                            date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS))
                    .select(Person::getPersonalNo, Person::getFirstName, Person::getLastName).toList();
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            persons.forEach(c -> System.out.println(Arrays.toString(c)));
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(2.5)
    public void countByEntityTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            Long count = QueryProvider.select(em, Person.class).where().equal(Person::getPersonalNo, PERSONAL_NO1).and()
                    .like(Person::getLastName, "lname").and().startsWith(Person::getFirstName, "fname").or()
                    .ge(Person::getBirthDate, date).and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS)).count();
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            System.out.format("Counted %s rows in database by query\n", count);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(3)
    public void updateSetOneByEntityTest() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            Date date = getDateValue();
            transaction.begin();
            // ============= Query construction ============== //
            int rows = QueryProvider.update(em, Person.class).set(Person::getMiddName, "middName").where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname").and()
                    .openBracket().startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date)
                    .closeBracket().execute();
            // =============================================//
            transaction.commit();
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            System.out.format("updated %s rows\n", rows);
            Assert.assertEquals("No expected row number was updated", rows, 1);
        } catch (Throwable ex) {
            transaction.rollback();
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(4)
    public void updateSetMultiByEntityTest() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            Date newBirthDate = getDateValue(10);
            Date date = getDateValue();
            transaction.begin();
            // ============= Query construction ============== //
            int rows = QueryProvider.update(em, Person.class).set(Person::getMiddName, "newMiddName")
                    .set(Person::getBirthDate, newBirthDate).where().equal(Person::getPersonalNo, PERSONAL_NO1).and()
                    .like(Person::getLastName, "lname").and().openBracket().startsWith(Person::getFirstName, "fname")
                    .or().ge(Person::getBirthDate, date).closeBracket().execute();
            // =============================================//
            transaction.commit();
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            System.out.format("updated %s rows\n", rows);
            Assert.assertEquals("No expected row number was updated", rows, 1);
        } catch (Throwable ex) {
            transaction.rollback();
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(5)
    public void deleteByEntityTest() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // ============= Query construction ============== //
            QueryStream<Person> stream = QueryProvider.delete(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO2).and().like(Person::getLastName, "lname").and()
                    .startsWith(Person::getFirstName, "fname");
            int rows = stream.execute();
            // =============================================//
            transaction.commit();
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            System.out.format("deleted %s rows\n", rows);
            Assert.assertEquals("No expected row number was updated", rows, 1);
        } catch (Throwable ex) {
            transaction.rollback();
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(6)
    public void cacheTest() {
        supplierEntityTest();
        supplierEntityTest();
    }

    @Test
    @RunOrder(7)
    public void selfQueryTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
                    .equalCl(Person::getPersonalNo, Person::getAddrress)
                    .likeCl(Person::getFirstName, Person::getFullName).and().startsWith(Person::getLastName, "lname");
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            System.out.println(stream.sql());
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(8)
    public void selfQueryResultTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            Person person = QueryProvider.select(em, Person.class).where()
                    .equalCl(Person::getPersonalNo, Person::getAddrress).and()
                    .likeCl(Person::getLastName, Person::getFullName).and().startsWith(Person::getLastName, "lname")
                    .firstOrDefault(new Person());
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
}
