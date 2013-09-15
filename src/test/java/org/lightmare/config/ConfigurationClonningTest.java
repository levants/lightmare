package org.lightmare.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lightmare.utils.CollectionUtils;

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
	    System.out.println(CollectionUtils.getFirst(cloneConfig
		    .getDataSourcePath()));
	    System.out.println(CollectionUtils.getFirst(cloneConfig
		    .getDeploymentPath()));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
