package org.lightmare.criteria.queries;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.db.TestDBUtils;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class QueryTest {

    private static final String PERSONAL_NO1 = "10100100100";

    private static final String PERSONAL_NO2 = "10100100111";

    protected static EntityManagerFactory emf;

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

    @BeforeClass
    public static void config() {

	try {
	    emf = TestDBUtils.create();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    private QueryStream<Person> createSetterStream(final EntityManager em) throws IOException {

	QueryStream<Person> stream = QueryProvider.select(em, Person.class);

	stream.where().moreOrEq(c -> c.getBirthDate(), getDateValue()).and();
	stream.like(Person::getLastName, "lname");
	stream.and().like(Person::getFirstName, "fname");
	stream.or().eq(Person::getPersonalNo, PERSONAL_NO1);
	stream.or().equals(Person::getInfo, null);
	stream.orderByDesc(Person::getLastName).orderBy(Person::getBirthDate);
	stream.orderBy(Person::getPersonId);
	stream.in(Person::getPersonId, Arrays.asList(1L, 2L, 3L, 4L, 5L));

	return stream;
    }

    @Test
    @RunOrder(1)
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
    @RunOrder(2)
    public void toListByEntityTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    Date date = getDateValue();
	    // ============= Query construction ============== //
	    List<Person> persons = QueryProvider.select(em, Person.class).where()
		    .eq(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname").and()
		    .startsWith(Person::getFirstName, "fname").or().moreOrEq(Person::getBirthDate, date).and()
		    .in(Person::getPersonId, Arrays.asList(1L, 2L, 3L, 4L, 5L)).toList();
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
		    .eq(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname").and()
		    .startsWith(Person::getFirstName, "fname").or().moreOrEq(Person::getBirthDate, date).and()
		    .in(Person::getPersonId, Arrays.asList(1L, 2L, 3L, 4L, 5L))
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
	    Long count = QueryProvider.select(em, Person.class).where().eq(Person::getPersonalNo, PERSONAL_NO1).and()
		    .like(Person::getLastName, "lname").and().startsWith(Person::getFirstName, "fname").or()
		    .moreOrEq(Person::getBirthDate, date).and()
		    .in(Person::getPersonId, Arrays.asList(1L, 2L, 3L, 4L, 5L)).count();
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
		    .eq(Person::getPersonalNo, PERSONAL_NO1).and().like(Person::getLastName, "lname").and()
		    .openBracket().startsWith(Person::getFirstName, "fname").or().moreOrEq(Person::getBirthDate, date)
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
		    .set(Person::getBirthDate, newBirthDate).where().eq(Person::getPersonalNo, PERSONAL_NO1).and()
		    .like(Person::getLastName, "lname").and().openBracket().startsWith(Person::getFirstName, "fname")
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
    @RunOrder(5)
    public void deleteByEntityTest() {

	EntityManager em = emf.createEntityManager();
	EntityTransaction transaction = em.getTransaction();
	try {
	    transaction.begin();
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.delete(em, Person.class).where()
		    .eq(Person::getPersonalNo, PERSONAL_NO2).and().like(Person::getLastName, "lname").and()
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
}
