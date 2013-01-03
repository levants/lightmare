package ge.gov.mia.lightmare;

import ge.gov.mia.lightmare.bean.LightMareBeanRemote;
import ge.gov.mia.lightmare.bean.LightMareFalseBeanRemote;
import ge.gov.mia.lightmare.ejb.EjbConnector;
import ge.gov.mia.lightmare.ejb.startup.MetaCreator;
import ge.gov.mia.lightmare.entities.Person;
import ge.gov.mia.lightmare.unitorder.RunOrder;
import ge.gov.mia.lightmare.unitorder.SortedRunner;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for lightmare App.
 */
@RunWith(SortedRunner.class)
public class EjbTest {

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
				creator = JpaTest.getDBCreator(path, "testUnit");
			} else {
				creator = JpaTest.getDBCreator("testUnit");

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

	@Test
	@RunOrder(1)
	public void addPersonTest() {
		try {
			bean.addPerson(personToAdd);
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@AfterClass
	public static void end() {
		MetaCreator.closeAllConnections();
	}

}
