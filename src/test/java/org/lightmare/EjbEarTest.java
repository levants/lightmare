package org.lightmare;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.bean.LightMareBeanRemote;
import org.lightmare.bean.LightMareFalseBeanRemote;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.entities.Person;
import org.lightmare.jndi.JndiManager;
import org.lightmare.unitorder.RunOrder;
import org.lightmare.unitorder.SortedRunner;

@RunWith(SortedRunner.class)
public class EjbEarTest {

    private static LightMareBeanRemote bean;

    private static Person personToAdd;

    private static Person personToEdit;

    private static Person personToIdGenerator;

    private static Person personToIdGeneratorNew;

    private static Scanner scanner;

    @BeforeClass
    public static void start() {

	try {
	    InputStream stream = EjbTest.class.getResourceAsStream("path");
	    if (stream != null) {
		scanner = new Scanner(stream);
	    }
	    DBCreator creator;
	    String path;
	    if (scanner != null && scanner.hasNextLine()) {
		path = scanner.nextLine().trim();
		creator = JpaTest.getDBCreator(path, "./ds/standalone.xml",
			"testUnit", null);
	    } else {
		creator = JpaTest
			.getDBCreator(EarFileReaderTest.EAR_PATH,
				"./ds/standalone.xml", "testUnit", null/* "persistence/emf" */);

	    }
	    personToAdd = creator.createPersonToAdd();
	    personToEdit = creator.createPersonToEdit();
	    personToIdGenerator = creator.personForIdGenerator();
	    personToIdGeneratorNew = creator.personForIdGeneratorNew();

	    EjbConnector connector = new EjbConnector();
	    bean = connector.connectToBean("LightMareBean",
		    LightMareBeanRemote.class);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public void getThreadId() {
	System.out.println("=======thread id============");
	System.out.println(Thread.currentThread().getId());
	System.out.println("============================");
    }

    @Test
    @RunOrder(1)
    public void addPersonTest() {
	try {
	    bean.addPerson(personToAdd);
	    getThreadId();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(2)
    public void editPersonTest() {
	try {
	    personToEdit.setPersonId(1000);
	    Person check = bean.getPerson(1000);
	    Assert.assertNull("such person alredy exists", check);
	    bean.editPerson(personToEdit);
	    check = bean.getPerson(1000);
	    Assert.assertNotNull("could not insert person", check);
	    getThreadId();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(3)
    public void setIdPersonTest() {
	try {
	    personToIdGenerator.setPersonId(1001);
	    Person check = bean.getPerson(1001);
	    Assert.assertNull("such person alredy exists", check);
	    bean.editPerson(personToIdGenerator);
	    check = bean.getPerson(1001);
	    Assert.assertNotNull("could not insert person", check);
	    bean.addPerson(personToIdGeneratorNew);
	    System.out.println(personToIdGeneratorNew.getPersonId());
	    getThreadId();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(4)
    public void getPersonTest() {
	try {
	    Person person = bean.getPerson(1000);
	    Assert.assertNotNull("could not fing person", person);
	    getThreadId();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(5)
    public void getPersonsTest() {
	try {
	    List<Person> persons = bean.getPersons("last", "first");
	    Assert.assertTrue("could not select persons", !persons.isEmpty());
	    getThreadId();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(6)
    public void getFalseBeanTest() {
	try {
	    EjbConnector connector = new EjbConnector();
	    LightMareFalseBeanRemote falseBean = connector.connectToBean(
		    "LightMareFalseBean", LightMareFalseBeanRemote.class);
	    boolean check = falseBean.isFalse();
	    Assert.assertTrue(check);
	    getThreadId();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Ignore
    @Test
    @RunOrder(7)
    public void getUserTransactionTest() {
	try {
	    InitialContext context = new InitialContext();
	    UserTransaction transaction = (UserTransaction) context
		    .lookup(JndiManager.USER_TRANSACTION_NAME);
	    Assert.assertNotNull(
		    "Could not find UserTransaction by jndi lookup",
		    transaction);
	    System.out
		    .format("\nRetrived UserTransaction object name is %s \n====================\n",
			    transaction);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @AfterClass
    public static void end() {
	int i = 0;
	while (i < 2) {
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException ex) {
		ex.printStackTrace();
	    }
	    i++;
	}
	MetaCreator.close();
    }
}
