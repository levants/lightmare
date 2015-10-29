package org.lightmare.criteria.queries;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;

public class QueryTest {

    private static final String PERSONAL_NO = "10100100100";

    private static EntityManagerFactory emf;

    private static Date defaultDate = new Date();

    private static Date getDateValue(int before) {

	Date date;

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(defaultDate);
	calendar.add(Calendar.YEAR, -before);
	date = calendar.getTime();

	return date;
    }

    private static Date getDateValue() {
	return getDateValue(100);
    }

    private static void provideDataBase() {

	Person person1 = new Person();
	person1.setPersonalNo(PERSONAL_NO);
	person1.setLastName("lname1");
	person1.setFirstName("fname1");
	person1.setBirthDate(getDateValue(80));
	person1.setMiddName("mname1");

	Person person2 = new Person();
	person2.setPersonalNo("10100100111");
	person2.setLastName("lname2");
	person2.setFirstName("fname2");
	person2.setBirthDate(getDateValue(90));
	person2.setMiddName("mname2");

	Person person3 = new Person();
	person3.setPersonalNo("10100101111");
	person3.setLastName("lname3");
	person3.setFirstName("fname3");
	person3.setBirthDate(getDateValue(95));
	person3.setMiddName("mname3");

	EntityManager em = emf.createEntityManager();
	EntityTransaction transaction = em.getTransaction();
	transaction.begin();
	try {
	    em.persist(person1);
	    em.persist(person2);
	    em.persist(person3);
	    transaction.commit();
	} catch (Throwable ex) {
	    transaction.rollback();
	}

    }

    @BeforeClass
    public static void config() {

	try {
	    emf = Persistence.createEntityManagerFactory("testUnit");
	    provideDataBase();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    private QueryStream<Person> createGetterStream(final EntityManager em) throws IOException {

	QueryStream<Person> stream = QueryProvider.select(em, Person.class);

	Person entity = new Person();
	stream.where().moreOrEq(entity::getBirthDate, getDateValue()).and();
	stream.like(entity::getLastName, "lname");
	stream.and().like(entity::getFirstName, "fname");
	stream.or().eq(entity::getPersonalNo, PERSONAL_NO);

	return stream;
    }

    private QueryStream<Person> createSetterStream(final EntityManager em) throws IOException {

	QueryStream<Person> stream = QueryProvider.select(em, Person.class);

	stream.where().moreOrEq(c -> c.getBirthDate(), getDateValue()).and();
	stream.like(Person::getLastName, "lname");
	stream.and().like(Person::getFirstName, "fname");
	stream.or().eq(Person::getPersonalNo, PERSONAL_NO);

	return stream;
    }

    @Test
    public void supplierGetterTest() {

	try {
	    System.out.println();
	    System.out.println("==========Getter===========");
	    EntityManager em = emf.createEntityManager();
	    QueryStream<Person> stream = createGetterStream(em);
	    System.out.println("===========JPA-QL==========");
	    System.out.println();
	    System.out.println(stream.sql());
	    System.out.println("===========Getter==========");
	    System.out.println();
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void supplierEntityTest() {

	try {
	    System.out.println();
	    System.out.println("==========Entity============");
	    EntityManager em = emf.createEntityManager();
	    QueryStream<Person> stream = createSetterStream(em);
	    System.out.println("===========JPA-QL==========");
	    System.out.println();
	    System.out.println(stream.sql());
	    System.out.println("===========Entity==========");
	    System.out.println();
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void toListByGetterTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    Person entity = new Person();
	    Date date = getDateValue();
	    // ============= Query construction ============== //
	    List<Person> persons = QueryProvider.select(em, Person.class).where().eq(entity::getPersonalNo, PERSONAL_NO)
		    .and().like(entity::getLastName, "fname").and().startsWith(entity::getFirstName, "lname").or()
		    .moreOrEq(entity::getBirthDate, date).toList();
	    // =============================================//
	    System.out.println();
	    System.out.println("------Getter------");
	    System.out.println();
	    persons.forEach(System.out::println);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    public void toListByEntityTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    Date date = getDateValue();
	    // ============= Query construction ============== //
	    List<Person> persons = QueryProvider.select(em, Person.class).where().eq(Person::getPersonalNo, PERSONAL_NO)
		    .and().like(Person::getLastName, "fname").and().startsWith(Person::getFirstName, "lname").or()
		    .moreOrEq(Person::getBirthDate, date).toList();
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
    public void updateSetOneByEntityTest() {

	EntityManager em = emf.createEntityManager();
	EntityTransaction transaction = em.getTransaction();
	try {
	    Date date = getDateValue();
	    transaction.begin();
	    // ============= Query construction ============== //
	    int rows = QueryProvider.update(em, Person.class).set(Person::getMiddName, "middName").where()
		    .eq(Person::getPersonalNo, PERSONAL_NO).and().like(Person::getLastName, "fname").and().openBracket()
		    .startsWith(Person::getFirstName, "lname").or().moreOrEq(Person::getBirthDate, date).closeBracket()
		    .execute();
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
    public void updateSetMultiByEntityTest() {

	EntityManager em = emf.createEntityManager();
	EntityTransaction transaction = em.getTransaction();
	try {
	    Date newBirthDate = getDateValue(10);
	    Date date = getDateValue();
	    transaction.begin();
	    // ============= Query construction ============== //
	    int rows = QueryProvider.update(em, Person.class).set(Person::getMiddName, "newMiddName")
		    .set(Person::getBirthDate, newBirthDate).where().eq(Person::getPersonalNo, PERSONAL_NO).and()
		    .like(Person::getLastName, "fname").and().openBracket().startsWith(Person::getFirstName, "lname")
		    .or().moreOrEq(Person::getBirthDate, date).closeBracket().execute();
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
    public void cacheTest() {

	supplierGetterTest();
	supplierGetterTest();

	supplierEntityTest();
	supplierEntityTest();
    }
}
