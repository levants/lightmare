package org.lightmare.criteria.queries;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.lightmare.criteria.entities.Person;
import org.lightmare.criteria.entities.Phone;
import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.QueryStream;

public class SubQueryTest extends QueryTest {

    @Test
    public void subQueryTest() {

	EntityManager em = emf.createEntityManager();
	try {
	    // ============= Query construction ============== //
	    QueryStream<Person> stream = QueryProvider.select(em, Person.class).where().in(Person::getLastName,
		    Phone.class,
		    c -> c.where().eq(Phone::getPhoneNumber, "100100").select(Phone::getPhoneNumber).toList());
	    String sql = stream.sql();
	    System.out.println(sql);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }
}
