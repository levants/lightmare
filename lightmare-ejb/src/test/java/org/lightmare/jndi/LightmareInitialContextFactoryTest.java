package org.lightmare.jndi;

import org.junit.Assert;
import org.junit.Test;

public class LightmareInitialContextFactoryTest {

    private static final String TRUE_VALUE = "true";

    @Test
    public void booleanValueTest() {

	String trueValue = Boolean.TRUE.toString();
	Assert.assertTrue("String values not matches",
		TRUE_VALUE.equals(trueValue));

	System.out.format("%s %s", TRUE_VALUE, trueValue);
    }
}
