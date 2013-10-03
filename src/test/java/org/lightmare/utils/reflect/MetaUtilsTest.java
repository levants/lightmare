package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;

import org.junit.Assert;
import org.junit.Test;
import org.lightmare.bean.LightMareBean;

public class MetaUtilsTest {

    @Test
    public void testDefaultValues() {

	System.out.println(MetaUtils.getDefault(byte.class));
	System.out.println(MetaUtils.getDefault(boolean.class));
	System.out.println(MetaUtils.getDefault(char.class));
	System.out.println(MetaUtils.getDefault(short.class));
	System.out.println(MetaUtils.getDefault(int.class));
	System.out.println(MetaUtils.getDefault(long.class));
	System.out.println(MetaUtils.getDefault(float.class));
	System.out.println(MetaUtils.getDefault(double.class));
    }

    public void testAccessibleObjectInstances() {

	try {
	    AccessibleObject acc1 = MetaUtils.getDeclaredField(
		    LightMareBean.class, "em");
	    AccessibleObject acc2 = MetaUtils.getDeclaredField(
		    LightMareBean.class, "em");

	    Assert.assertTrue("AccessibleObject instances are not the same",
		    acc1.equals(acc2));
	    System.out.format("%s %s", acc1, acc2);

	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
