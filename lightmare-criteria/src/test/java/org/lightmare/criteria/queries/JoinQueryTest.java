package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.internal.QueryStream;
import org.lightmare.criteria.runorder.RunOrder;

public class JoinQueryTest extends SubQueryTest {

    @Test
    @RunOrder(200)
    public void innerJoinTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
		    .like(Person::getLastName, "lname")
		    .join(Person::getPhones, c -> c.equals(Phone::getPhoneNumber, "100100"));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(201)
    public void leftJoinWhereTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
		    .like(Person::getLastName, "lname")
		    .leftJoin(Person::getPhones, c -> c.equals(Phone::getPhoneNumber, Person::getPersonalNo));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(201.05)
    public void leftJoinTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).leftJoin(Person::getPhones,
		    c -> c.equals(Phone::getPhoneNumber, Person::getPersonalNo));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(201.1)
    public void leftJoinInWhereTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).leftJoin(Person::getPhones,
		    c -> c.where().equals(Phone::getPhoneNumber, Person::getPersonalNo));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(201.2)
    public void emptyConditionalLeftJoinTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).leftJoin(Person::getPhones, c -> {
	    });
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(201.3)
    public void unconditionalLeftJoinTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).leftJoin(Person::getPhones);
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(201.4)
    public void parentAndUnconditionalLeftJoinTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).leftJoin(Person::getPhones).where()
		    .like(Person::getLastName, "lname");
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(202)
    public void fetchJoinTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
		    .like(Person::getLastName, "lname")
		    .fetchJoin(Person::getPhones, c -> c.equals(Phone::getPhoneNumber, "100100").and()
			    .moreOrEquals(Phone::getOperatorId, Person::getPersonId));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }

    @Test
    @RunOrder(203)
    public void fetchJoinNotEqualsTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where()
		    .notEquals(Person::getLastName, "lname").and().like(Person::getAddrress, "address")
		    .fetchJoin(Person::getPhones,
			    c -> c.equals(Phone::getPhoneNumber, "100100")
				    .notEquals(Phone::getOperatorId, Person::getPersonId).or()
				    .equals(Phone::getOperatorId, Phone::getPhoneId));
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	} finally {
	    em.close();
	}
    }
}
