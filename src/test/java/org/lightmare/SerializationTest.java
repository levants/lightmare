package org.lightmare;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.lightmare.utils.RpcUtils;

public class SerializationTest {

	@Test
	public void nullTest() {

		try {
			byte[] nullBt = RpcUtils.serialize(null);
			System.out.println(Arrays.toString(nullBt));
			Object value = RpcUtils.deserialize(nullBt);
			System.out.println(value);

			Assert.assertNull("null serializetion is not valid", value);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
