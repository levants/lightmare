package org.lightmare.rest;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.entities.Person;
import org.lightmare.rest.providers.JacksonFXmlFeature;
import org.lightmare.rest.providers.ObjectMapperProvider;
import org.lightmare.utils.PersonUtils;

/**
 * REST client class
 * 
 * @author levan
 * 
 */
@Ignore
public class RestClientTest {

    private static final String REST_URL = "http://localhost:8080/rest/lightmare/";

    private static final String REST_URL_LIST = "list";

    private static final String QUERY_PERSON_ID = "personId";

    private static final String QUERY_LIST_LAST = "last";

    private static final String QUERY_LIST_FIRST = "first";

    public static void put() {

	try {
	    Person person = PersonUtils.createPersonToAdd();
	    ClientConfig config = new ClientConfig();
	    config.register(ObjectMapperProvider.class);
	    config.register(JacksonFXmlFeature.class);
	    Client client = ClientBuilder.newClient(config);
	    WebTarget webTarget = client.target(REST_URL);
	    Invocation.Builder builder = webTarget
		    .request(MediaType.APPLICATION_JSON_TYPE);
	    Invocation invocation = builder.buildPut(Entity.entity(person,
		    MediaType.APPLICATION_JSON_TYPE));
	    Response response = invocation.invoke();
	    System.out.println(response.getStatus());
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void get() {

	try {
	    ClientConfig config = new ClientConfig();
	    config.register(ObjectMapperProvider.class);
	    config.register(JacksonFXmlFeature.class);
	    Client client = ClientBuilder.newClient(config);
	    WebTarget webTarget = client.target(REST_URL).queryParam(
		    QUERY_PERSON_ID, 1);

	    Invocation.Builder builder = webTarget
		    .request(MediaType.APPLICATION_JSON_TYPE);
	    Invocation invocation = builder.buildGet();
	    Response response = invocation.invoke();
	    Person person = response.readEntity(Person.class);
	    System.out.println(response.getStatus());
	    System.out.println(person);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void getList() {

	try {
	    ClientConfig config = new ClientConfig();
	    config.register(ObjectMapperProvider.class);
	    config.register(JacksonFXmlFeature.class);
	    Client client = ClientBuilder.newClient(config);
	    WebTarget webTarget = client.target(REST_URL).path(REST_URL_LIST)
		    .queryParam(QUERY_LIST_LAST, "last")
		    .queryParam(QUERY_LIST_FIRST, "first");
	    Invocation.Builder builder = webTarget
		    .request(MediaType.APPLICATION_JSON_TYPE);
	    Invocation invocation = builder.buildGet();
	    Response response = invocation.invoke();
	    @SuppressWarnings("unchecked")
	    List<Person> persons = (List<Person>) response
		    .readEntity(List.class);
	    System.out.println(response.getStatus());
	    System.out.println(persons);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void test() {

	RestClient.getList();
	RestClient.get();
	RestClient.put();
    }
}
