package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.runorder.RunOrder;
import org.lightmare.criteria.runorder.SortedRunner;

@RunWith(SortedRunner.class)
public class SubQueryTest extends QueryTest {

    @RunOrder(100)
    @Test
    public void subQueryTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().subQuery(Phone.class,
		    c -> c.where().equals(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber).toList());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(100.1)
    @Test
    public void subQueryCallTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().subQuery(Phone.class,
		    c -> c.where().equals(Phone::getPhoneNumber, "100100").and()
			    .equals(Phone::getOperatorId, Person::getPersonId).select(Phone::getPhoneNumber));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(100.2)
    @Test
    public void subQuerySelectTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().in(Person::getLastName,
		    Phone.class,
		    c -> c.where().equals(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber).toList());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(100.5)
    @Test
    public void subQueryExistsGetTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().exists(Phone.class,
		    c -> c.where().equals(Phone::getPhoneNumber, "100100").get());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(100.51)
    @Test
    public void subQueryExistsCallTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().exists(Phone.class,
		    c -> c.where().equals(Phone::getPhoneNumber, "100100").and().moreOrEquals(Phone::getOperatorId,
			    Person::getPersonId));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(100.6)
    @Test
    public void subQueryExistsListTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().exists(Phone.class,
		    c -> c.where().equals(Phone::getPhoneNumber, "100100").toList());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(101)
    @Test
    public void subQueryCountTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().in(Person::getLastName,
		    Phone.class, c -> c.where().equals(Phone::getPhoneNumber, "100100").count());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(101.1)
    @Test
    public void subQueryCountWithParentTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().in(Person::getLastName,
		    Phone.class, c -> c.where().equals(Phone::getPhoneNumber, "100100").and()
			    .lessOrEquals(Phone::getOperatorId, Person::getPersonId).count());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }

    @RunOrder(101.2)
    @Test
    public void subQueryCountWithParentInTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().in(Person::getLastName,
		    Phone.class, c -> c.where().equals(Phone::getPhoneNumber, "100100").and()
			    .in(Phone::getOperatorId, Person::getIdentifiers).count());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }
}
