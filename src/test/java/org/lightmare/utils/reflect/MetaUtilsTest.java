package org.lightmare.utils.reflect;

import org.junit.Test;

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
    }
}
