package org.lightmare.linq.orm;

import org.junit.Test;
import org.lightmare.linq.entities.Person;

public class EntityUtilsTest {

    @Test
    public void proxyTest() {

	try {
	    Person person = new Person();
	    Object wrapper = EntityUtils.wrap(person);
	    person.getLastName();
	    System.out.println(wrapper);
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }
}
