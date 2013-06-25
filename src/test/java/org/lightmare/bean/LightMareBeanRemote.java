package org.lightmare.bean;

import java.util.List;

import org.lightmare.entities.Person;

public interface LightMareBeanRemote {

    List<Person> getPersons(String lastName, String firstName);

    Person getPerson(Integer personId);

    void addPerson(Person person);

    void editPerson(Person person);

    boolean isCommon(Person person1, Person person2);

    boolean isCommon(String last1, String last2);
}
