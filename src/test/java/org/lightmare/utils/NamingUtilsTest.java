package org.lightmare.utils;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.jndi.JndiManager;
import org.lightmare.utils.NamingUtils.BeanDescriptor;

public class NamingUtilsTest {

    @Ignore
    @Test
    public void contextCloseTest() {

	String name = "test_name";
	String value = "test_value";

	try {

	    Context contextBind = new JndiManager().getContext();
	    contextBind.bind(name, value);
	    contextBind.close();

	    Context contextGet = new InitialContext();
	    Object gotten = contextGet.lookup(name);

	    System.out.println(gotten);

	} catch (NamingException ex) {
	    ex.printStackTrace();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void parseEjbNameTest() {

	try {
	    String jndiName = "ejb:lightmare//LightmareBean!org.lightmare.beans.LightmareBeanRemote";
	    BeanDescriptor descriptor = NamingUtils.parseEjbJndiName(jndiName);
	    System.out.println(descriptor.getBeanName());
	    System.out.println(descriptor.getInterfaceName());
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
