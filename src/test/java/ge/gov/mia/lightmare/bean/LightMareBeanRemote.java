package ge.gov.mia.lightmare.bean;

import ge.gov.mia.lightmare.entities.Person;

import java.util.List;

public interface LightMareBeanRemote {

	List<Person> getPersons(String lastName, String firstName);

	Person getPerson(Integer personId);

	void addPerson(Person person);

	void editPerson(Person person);
}
