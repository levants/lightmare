package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.lightmare.bean.LightMareBean;
import org.lightmare.utils.ObjectUtils;

public class MetaUtilsTest {

    protected EntityManager em;

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

    @Test
    public void testAccessibleObjectInstances() {

	try {
	    AccessibleObject acc1 = MetaUtils.getDeclaredField(
		    LightMareBean.class, "em");
	    AccessibleObject acc2 = MetaUtils.getDeclaredField(
		    LightMareBean.class, "em");

	    AccessibleObject acc3 = MetaUtils.getDeclaredField(
		    MetaUtilsTest.class, "em");

	    Assert.assertTrue("AccessibleObject instances are not the same",
		    acc1.equals(acc2));
	    System.out.format("%s %s", acc1, acc2);
	    Assert.assertTrue("AccessibleObject instances are not the same",
		    ObjectUtils.notEquals(acc1, acc3));
	    System.out.format("%s %s", acc1, acc3);

	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
