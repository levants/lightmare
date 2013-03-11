package org.lightmare.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.lightmare.entities.Person;

@Stateless
public class LightMareBean implements LightMareBeanRemote {

    @PersistenceContext(unitName = "testUnit", name = "persistence/em")
    private EntityManager em;

    @Override
    public List<Person> getPersons(String lastName, String firstName) {
	return em
		.createQuery(
			"select c from Person as c where c.lastName like :lastName and c.firstName like :firstName\t\n",
			Person.class)
		.setParameter("lastName",
			new StringBuilder(lastName).append("%").toString())
		.setParameter("firstName",
			new StringBuilder(firstName).append("%").toString())
		.getResultList();
    }

    @Override
    public Person getPerson(Integer personId) {
	return em.find(Person.class, personId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addPerson(Person person) {
	em.persist(person);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void editPerson(Person person) {
	merge(person);
    }

    private void merge(Person person) {
	em.merge(person);
    }

}
