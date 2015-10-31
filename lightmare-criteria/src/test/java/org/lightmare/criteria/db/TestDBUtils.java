package org.lightmare.criteria.db;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.lightmare.criteria.entities.Person;

public class TestDBUtils {

    private static final String PERSONAL_NO1 = "10100100100";

    private static final String PERSONAL_NO2 = "10100100111";

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

    private static void provideDataBase() {

	Person person1 = new Person();
	person1.setPersonalNo(PERSONAL_NO1);
	person1.setLastName("lname1");
	person1.setFirstName("fname1");
	person1.setBirthDate(getDateValue(80));
	person1.setMiddName("mname1");

	Person person2 = new Person();
	person2.setPersonalNo(PERSONAL_NO2);
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

    public static EntityManagerFactory create() {

	emf = Persistence.createEntityManagerFactory("testUnit");
	provideDataBase();

	return emf;
    }
}
