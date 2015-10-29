package org.lightmare.linq.visibqueries;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.linq.entities.Person;

public class QueryTest {

    private final String personalNo = "10100100100";

    private static EntityManagerFactory emf;

    private static void persistPersons() {

	Person person = new Person();
	person.setPersonalNo("10100100100");
	person.setLastName("lname");
	person.setFirstName("fname");
	person.setBirthDate(new Date());
	person.setMiddName("mname");
	EntityManager em = emf.createEntityManager();
	EntityTransaction transaction = em.getTransaction();
	transaction.begin();
	try {
	    em.persist(person);
	    transaction.commit();
	} catch (Throwable ex) {
	    transaction.rollback();
	}

    }

    @BeforeClass
    public static void config() {

	try {
	    emf = Persistence.createEntityManagerFactory("testUnit");
	    persistPersons();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    private Date getDateValue() {

	Date date;

	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.YEAR, -100);
	date = calendar.getTime();

	return date;
    }

    private QueryStream<Person> createGetterStream(final EntityManager em) throws IOException {

	QueryStream<Person> stream = QueryProvider.select(em, Person.class);

	Person entity = new Person();
	stream.setWerbose(Boolean.TRUE);
	stream.where().moreOrEq(entity::getBirthDate, getDateValue()).and();
	stream.like(entity::getLastName, "lname");
	stream.and().like(entity::getFirstName, "fname");
	stream.or().eq(entity::getPersonalNo, personalNo);

	return stream;
    }

    private QueryStream<Person> createSetterStream(final EntityManager em) throws IOException {

	QueryStream<Person> stream = QueryProvider.select(em, Person.class);

	stream.setWerbose(Boolean.TRUE);
	stream.where().moreOrEq(c -> c.getBirthDate(), getDateValue()).and();
	stream.like(Person::getLastName, "lname");
	stream.and().like(Person::getFirstName, "fname");
	stream.or().eq(Person::getPersonalNo, personalNo);

	return stream;
    }

    @Test
    public void supplierGetterTest() {

	try {
	    System.out.println();
	    System.out.println("===========================");
	    EntityManager em = emf.createEntityManager();
	    QueryStream<Person> stream = createGetterStream(em);
	    System.out.println("===========JPA-QL==========");
	    System.out.println();
	    System.out.println(stream.sql());
	    System.out.println("===========================");
	    System.out.println();
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void supplierEntityTest() {

	try {
	    System.out.println();
	    System.out.println("===========================");
	    EntityManager em = emf.createEntityManager();
	    QueryStream<Person> stream = createSetterStream(em);
	    System.out.println("===========JPA-QL==========");
	    System.out.println();
	    System.out.println(stream.sql());
	    System.out.println("===========================");
	    System.out.println();
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void toListByGetterTest() {

	try {
	    EntityManager em = emf.createEntityManager();
	    Person entity = new Person();
	    Date date = getDateValue();
	    // ============= Query construction ============== //
	    List<Person> persons = QueryProvider.select(em, Person.class).where().eq(entity::getPersonalNo, personalNo)
		    .and().like(entity::getLastName, "fname").and().startsWith(entity::getFirstName, "lname").or()
		    .moreOrEq(entity::getBirthDate, date).toList();
	    // =============================================//
	    System.out.println();
	    System.out.println("----------------");
	    System.out.println();
	    persons.forEach(System.out::println);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void toListByEntityTest() {

	try {
	    EntityManager em = emf.createEntityManager();
	    Date date = getDateValue();
	    // ============= Query construction ============== //
	    List<Person> persons = QueryProvider.select(em, Person.class).where().eq(Person::getPersonalNo, personalNo)
		    .and().like(Person::getLastName, "fname").and().startsWith(Person::getFirstName, "lname").or()
		    .moreOrEq(Person::getBirthDate, date).toList();
	    // =============================================//
	    System.out.println();
	    System.out.println("----------------");
	    System.out.println();
	    persons.forEach(System.out::println);
	} catch (Throwable ex) {
	    ex.printStackTrace();
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
