package org.lightmare.ejb.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightmare.bean.LightMareFalseBean;
import org.lightmare.bean.LightMareFalseBeanRemote;
import org.lightmare.cache.MetaData;
import org.lightmare.unitorder.RunOrder;
import org.lightmare.unitorder.SortedRunner;
import org.lightmare.utils.ObjectUtils;

@RunWith(SortedRunner.class)
public class BeanHandlerCloneTest {

    private static final int LIMIT = 1000000;

    private MetaData metaData;

    @Before
    public void configure() {

	metaData = new MetaData();
	metaData.setBeanClass(LightMareFalseBean.class);
	metaData.setInterfaceClasses(new Class<?>[] { LightMareFalseBeanRemote.class });
    }

    @Test
    @RunOrder(1)
    public void handlerClonePerformanceTest() {

	try {
	    BeanHandler handler = new BeanHandler(metaData);
	    long start = 0L;
	    long current = 0L;
	    long time = 0L;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = (BeanHandler) handler.clone();
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.format("cloning %s\n", time);
	    System.out.println("=========");
	    System.out.println(ObjectUtils.notNull(handler));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(2)
    public void newPerformanceTest() {

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
	System.out.format("new %s\n", time);
	System.out.println("=========");
	System.out.println(ObjectUtils.notNull(handler));
    }

    @Test
    @RunOrder(3)
    public void newPerformanceTest2() {

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
	System.out.format("new2 %s\n", time);
	System.out.println("=========");
	System.out.println(ObjectUtils.notNull(handler));
    }

    @Test
    @RunOrder(4)
    public void handlerClonePerformanceTest2() {

	try {
	    LightMareFalseBean bean = new LightMareFalseBean();
	    BeanHandler handler = new BeanHandler(metaData);
	    long start = 0L;
	    long current = 0L;
	    long time = 0L;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = BeanHandlerFactory.get(metaData, bean);
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.format("cloning2 %s\n", time);
	    System.out.println("=========");
	    System.out.println(ObjectUtils.notNull(handler));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    @RunOrder(5)
    public void handlerCloneNewPerformanceTest() {

	try {
	    System.out.println("=================");
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
	    System.out.format("new %s\n", time);

	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = (BeanHandler) handler.clone();
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.format("cloning %s\n", time);

	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = (BeanHandler) handler.clone();
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.format("cloning %s\n", time);

	    start = System.currentTimeMillis();
	    for (int i = 0; i < LIMIT; i++) {

		handler = new BeanHandler(metaData);
	    }
	    current = System.currentTimeMillis();
	    time = current - start;
	    System.out.format("new %s\n", time);

	    System.out.println("=========");
	    System.out.println(ObjectUtils.notNull(handler));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
