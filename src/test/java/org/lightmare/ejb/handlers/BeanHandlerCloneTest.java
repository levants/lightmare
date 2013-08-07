package org.lightmare.ejb.handlers;

import org.junit.Test;
import org.lightmare.bean.LightMareFalseBean;
import org.lightmare.bean.LightMareFalseBeanRemote;
import org.lightmare.cache.MetaData;
import org.lightmare.utils.ObjectUtils;

public class BeanHandlerCloneTest {

    private static final int LIMIT = 100000;

    @Test
    public void handlerClonePerformanceTest() {

	// LightMareFalseBean bean = new LightMareFalseBean();
	MetaData metaData = new MetaData();
	metaData.setBeanClass(LightMareFalseBean.class);
	metaData.setInterfaceClasses(new Class<?>[] { LightMareFalseBeanRemote.class });

	try {
	    BeanHandler handler = new BeanHandler(metaData);
	    long start = 0L;
	    long current = 0L;
	    long time = 0L;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = new BeanHandler(metaData);
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.println(time);

	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = (BeanHandler) handler.clone();
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.println(time);
	    System.out.println("=========");
	    System.out.println(ObjectUtils.notNull(handler));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
