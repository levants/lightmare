package org.lightmare.criteria.queries;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.GeneralInfo;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.PersonInfo;
import org.lightmare.criteria.entities.PersonWrapper;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.JpaQueryProvider;
import org.lightmare.criteria.query.JpaQueryStream;
import org.lightmare.criteria.query.internal.orm.SelectExpression.Select;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class QueryTest extends TestEnviromentConfig {

    private static void rollback(EntityTransaction transaction) {

        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    @Test
    @RunOrder(1)
    public void supplierEntityTest() {

        EntityManager em = emf.createEntityManager();
        try {
            System.out.println();
            System.out.println("==========Entity============");
            JpaQueryStream<Person> stream = createQueryStream(em);
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
    @RunOrder(1.5)
    public void toListAllTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            List<Phone> phones = JpaQueryProvider.select(em, Phone.class).toList();
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            phones.forEach(System.out::println);
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
            List<Person> persons = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
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
            List<Object[]> persons = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(stream -> stream.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate,
                            date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS)).selectAll(c -> c
                            .column(Person::getPersonalNo).column(Person::getFirstName).column(Person::getLastName))
                    .toList();
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
    @RunOrder(2.31)
    public void toListBySelectStringTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            JpaQueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(s -> s.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS))
                    .select("select c.personalNo, c.firstName, c.lastName");
            System.out.println(stream.sql());
            List<Object[]> persons = stream.toList();
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
    @RunOrder(2.32)
    public void toListBySelectWrapperTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            JpaQueryStream<PersonWrapper> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(s -> s.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS))
                    .select("select new org.lightmare.criteria.entities.PersonWrapper(c.personalNo, c.lastName, c.firstName)",
                            PersonWrapper.class);
            System.out.println(stream.sql());
            List<PersonWrapper> persons = stream.toList();
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
    @RunOrder(2.33)
    public void toListBySelectWithClassTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            JpaQueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(s -> s.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS)).select(Select.select()
                            .column(Person::getPersonalNo).column(Person::getLastName).column(Person::getFirstName));
            System.out.println(stream.sql());
            List<Object[]> persons = stream.toList();
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
    @RunOrder(2.34)
    public void toListBySelectWrapperTypedTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            JpaQueryStream<PersonWrapper> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(s -> s.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS))
                    .selectType(PersonWrapper.class, Select.select().column(Person::getPersonalNo)
                            .column(Person::getLastName).column(Person::getFirstName));
            System.out.println(stream.sql());
            List<PersonWrapper> persons = stream.toList();
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
    @RunOrder(2.35)
    public void toListBySelectWithConsumerTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            JpaQueryStream<Object[]> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(s -> s.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS)).selectAll(s -> s
                            .column(Person::getPersonalNo).column(Person::getLastName).column(Person::getFirstName));
            System.out.println(stream.sql());
            List<Object[]> persons = stream.toList();
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
    @RunOrder(2.36)
    public void toListBySelectConsumerTypedTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            JpaQueryStream<PersonWrapper> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
                    .brackets(s -> s.startsWith(Person::getFirstName, "fname").or().ge(Person::getBirthDate, date))
                    .and().in(Person::getPersonId, Arrays.asList(IDENTIFIERS)).selectType(PersonWrapper.class, s -> s
                            .column(Person::getPersonalNo).column(Person::getLastName).column(Person::getFirstName));
            System.out.println(stream.sql());
            List<PersonWrapper> persons = stream.toList();
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
    @RunOrder(2.5)
    public void countByEntityTest() {

        EntityManager em = emf.createEntityManager();
        try {
            Date date = getDateValue();
            // ============= Query construction ============== //
            Long count = JpaQueryProvider.select(em, Person.class).where().equal(Person::getPersonalNo, PERSONAL_NO1)
                    .and().like(Person::getLastName, "lname%").and().startsWith(Person::getFirstName, "fname").or()
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
            int rows = JpaQueryProvider.update(em, Person.class).set(Person::getMiddName, "middName").where()
                    .equal(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname%").and()
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
            ex.printStackTrace();
            rollback(transaction);
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
            int rows = JpaQueryProvider.update(em, Person.class).set(Person::getMiddName, "newMiddName")
                    .set(Person::getBirthDate, newBirthDate).where().equal(Person::getPersonalNo, PERSONAL_NO1).and()
                    .like(Person::getLastName, "lname%").and().openBracket().startsWith(Person::getFirstName, "fname")
                    .or().ge(Person::getBirthDate, date).closeBracket().execute();
            // =============================================//
            transaction.commit();
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println();
            System.out.format("updated %s rows\n", rows);
            Assert.assertEquals("No expected row number was updated", rows, 1);
        } catch (Throwable ex) {
            ex.printStackTrace();
            rollback(transaction);
        } finally {
            em.close();
        }
    }

    public static Person initPerson() {

        Person person2 = new Person();

        person2.setPersonalNo(PERSONAL_NO2);
        person2.setLastName("lname2");
        person2.setFirstName("fname2");
        person2.setBirthDate(getDateValue(90));
        person2.setMiddName("mname2");
        person2.setEscape(Character.valueOf('_'));

        PersonInfo info2 = new PersonInfo();
        info2.setCardNumber("200");
        info2.setNote("note2");
        person2.setInfo(info2);

        return person2;
    }

    private void getForUpfate() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO2).and().like(Person::getLastName, "lname%").and()
                    .startsWith(Person::getFirstName, "fname");
            Person person = stream.getFirst();
            transaction.begin();
            if (person == null) {
                Person newPerson = QueryTest.initPerson();
                em.persist(newPerson);
                person = newPerson;
            }
            transaction.commit();
        } catch (Throwable ex) {
            ex.printStackTrace();
            rollback(transaction);
        } finally {
            em.close();
        }
    }

    @Test
    @RunOrder(5)
    public void deleteByEntityTest() {

        getForUpfate();
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.delete(em, Person.class).where()
                    .equal(Person::getPersonalNo, PERSONAL_NO2).and().like(Person::getLastName, "lname%").and()
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
            ex.printStackTrace();
            rollback(transaction);
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
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, Person::getAddrress).equal(Person::getFullName, "fullName")
                    .like(Person::getFirstName, Person::getFullName).and().startsWith(Person::getLastName, "lname");
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
            Person person = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, Person::getAddrress).and()
                    .like(Person::getLastName, GeneralInfo::getFullName).and().startsWith(Person::getLastName, "lname")
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

    @Test
    @RunOrder(9)
    public void queryLikeFieldTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, Person::getAddrress).and()
                    .like(Person::getLastName, GeneralInfo::getFullName, Person::getEscape).and()
                    .startsWith(Person::getLastName, "lname");
            String sql = stream.sql();
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
    @RunOrder(10)
    public void queryLikeValueTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Person> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, Person::getAddrress).and().like(Person::getLastName, "lname%", 'e')
                    .and().startsWith(Person::getLastName, "lname");
            String sql = stream.sql();
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
    @RunOrder(10)
    public void queryMaxTest() {

        EntityManager em = emf.createEntityManager();
        try {
            // ============= Query construction ============== //
            JpaQueryStream<Long> stream = JpaQueryProvider.select(em, Person.class).where()
                    .equal(Person::getPersonalNo, Person::getAddrress).and().like(Person::getLastName, "lname%", 'e')
                    .and().startsWith(Person::getLastName, "lname").max(Person::getPersonId);
            String sql = stream.sql();
            System.out.println(sql);
            Long max = stream.firstOrDefault(Long.valueOf(0));
            // =============================================//
            System.out.println();
            System.out.println("-------Entity----");
            System.out.println(max);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
