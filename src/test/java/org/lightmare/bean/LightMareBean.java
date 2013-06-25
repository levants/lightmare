package org.lightmare.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.lightmare.entities.Person;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Path("lightmare")
@Produces("application/json;charset=utf-8")
@Consumes("application/json;charset=utf-8")
public class LightMareBean implements LightMareBeanRemote {

    @PersistenceContext(unitName = "testUnit", name = "persistence/em")
    private EntityManager em;

    @Override
    @GET
    @Path("list")
    public List<Person> getPersons(@QueryParam("last") String lastName,
	    @QueryParam("first") String firstName) {
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
    @GET
    @Produces("application/json;charset=utf-8")
    @Consumes("application/json;charset=utf-8")
    public Person getPerson(@QueryParam("personId") Integer personId) {
	return em.find(Person.class, personId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PUT
    public void addPerson(@QueryParam("person") Person person) {
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
