package org.lightmare;

import org.junit.Test;
import org.lightmare.bean.LightMareBean;
import org.lightmare.rest.RestPublisher;

public class PublishTest {

	@Test
	public void publisherTest() {

		RestPublisher restPublisher = new RestPublisher();
		String publish = restPublisher.publish(LightMareBean.class);
		System.out.println(publish);
	}
}
