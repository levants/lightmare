package org.lightmare.utils;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.lightmare.utils.io.serialization.NativeSerializer;

public class SerializationTest {

    @Test
    public void nullTest() {

	try {
	    byte[] nullBt = NativeSerializer.serialize(null);
	    System.out.println(Arrays.toString(nullBt));
	    Object value = NativeSerializer.deserialize(nullBt);
	    System.out.println(value);

	    Assert.assertNull("null serializetion is not valid", value);

	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
