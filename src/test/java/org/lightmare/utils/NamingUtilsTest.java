package org.lightmare.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;

public class NamingUtilsTest {

    @Test
    public void contextCloseTest() {

	String name = "test_name";
	String value = "test_value";

	try {

	    InitialContext contextBind = new InitialContext();
	    contextBind.bind(name, value);
	    contextBind.close();

	    InitialContext contextGet = new InitialContext();
	    Object gotten = contextGet.lookup(name);

	    System.out.println(gotten);

	} catch (NamingException ex) {
	    ex.printStackTrace();
	}

    }
}
