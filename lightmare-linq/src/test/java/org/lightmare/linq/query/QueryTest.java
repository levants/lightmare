package org.lightmare.linq.query;

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

    private QueryStream<Person> createStream(final EntityManager em) throws IOException {

	QueryStream<Person> stream = QueryStream.select(em, Person.class);

	Person entity = new Person();
	stream.setWerbose(Boolean.TRUE);
	stream.where().moreOrEq(entity::getBirthDate, getDateValue()).and();
	stream.like(entity::getLastName, "lname");
	stream.and().like(entity::getFirstName, "fname");
	stream.or().eq(entity::getPersonalNo, personalNo);

	return stream;
    }

    @Test
    public void supplierTest() {

	try {
	    System.out.println();
	    System.out.println("===========================");
	    EntityManager em = emf.createEntityManager();
	    QueryStream<Person> stream = createStream(em);
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
    public void toListTest() {

	try {
	    EntityManager em = emf.createEntityManager();
	    Person entity = new Person();
	    Date date = getDateValue();

	    List<Person> persons = QueryStream.select(em, Person.class).where().eq(entity::getPersonalNo, personalNo)
		    .and().like(entity::getLastName, "fname").and().startsWith(entity::getFirstName, "lname").or()
		    .moreOrEq(entity::getBirthDate, date).toList();

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
	supplierTest();
	supplierTest();
    }
}
