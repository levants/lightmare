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
}
