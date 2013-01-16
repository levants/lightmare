package org.lightmare;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.lightmare.remote.Listener;

public class SerializationTest {

	@Test
	public void nullTest() {

		try {
			byte[] nullBt = Listener.serialize(null);
			System.out.println(Arrays.toString(nullBt));
			Object value = Listener.deserialize(nullBt);
			System.out.println(value);

			Assert.assertNull("null serializetion is not valid");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
