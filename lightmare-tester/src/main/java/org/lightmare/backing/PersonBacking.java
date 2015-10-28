package org.lightmare.backing;

import java.io.IOException;
import java.util.List;

import org.lightmare.bean.LightMareBeanRemote;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.entities.Person;
import org.lightmare.utils.collections.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersonBacking {

    private static ObjectMapper MAPPER = new ObjectMapper();

    public void addPerson(Person person) throws IOException {

	EjbConnector connector = new EjbConnector();
	LightMareBeanRemote bean = connector.connectToBean("LightMareBean",
		LightMareBeanRemote.class);
	bean.addPerson(person);
    }

    public String getPersons() throws IOException {

	String personsJson;

	EjbConnector connector = new EjbConnector();
	LightMareBeanRemote bean = connector.connectToBean("LightMareBean",
		LightMareBeanRemote.class);
	List<Person> persons = bean.getPersons("last", "first");
	if (CollectionUtils.valid(persons)) {
	    personsJson = MAPPER.writeValueAsString(persons);
	} else {
	    personsJson = "[]";
	}

	return personsJson;
    }
}
