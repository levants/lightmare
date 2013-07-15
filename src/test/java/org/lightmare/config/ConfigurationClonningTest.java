package org.lightmare.config;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.lightmare.utils.ObjectUtils;

public class ConfigurationClonningTest {

    private Configuration config;

    @Before
    public void configure() {

	config = new Configuration();
	config.addDataSourcePath("path");
	config.addDeploymentPath("deploy", Boolean.FALSE);
	config.configure();
    }

    @Test
    public void cloneTest() {

	try {
	    Object cloneObject = (Configuration) config.clone();
	    Assert.assertTrue("clonning is not returns the same class",
		    cloneObject.getClass().equals(Configuration.class));
	    Configuration cloneConfig = (Configuration) cloneObject;
	    System.out.println(ObjectUtils.getFirst(cloneConfig
		    .getDataSourcePath()));
	    System.out.println(ObjectUtils.getFirst(cloneConfig
		    .getDeploymentPath()));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
