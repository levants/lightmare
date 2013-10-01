package org.lightmare.utils.reflect;

import org.junit.Test;

public class MetaUtilsTest {

    @Test
    public void testDefaultValues() {

	System.out.println(MetaUtils.getDefault(Byte.class));
    }
}
