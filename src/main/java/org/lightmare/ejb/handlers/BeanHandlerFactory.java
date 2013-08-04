package org.lightmare.ejb.handlers;

import java.io.IOException;

import org.lightmare.cache.MetaData;

/**
 * Factory class to initialize {@link BeanHandler} class
 * 
 * @author Levan
 * 
 */
public class BeanHandlerFactory {

    /**
     * Gets {@link BeanHandler} instance from {@link MetaData} or creates new
     * instance if it is null
     * 
     * @param metaData
     * @param bean
     * @return {@link BeanHandler}
     * @throws IOException
     */
    public static BeanHandler get(MetaData metaData, Object bean)
	    throws IOException {

	BeanHandler handler = metaData.getHandler();
	if (handler == null) {
	    handler = new BeanHandler(metaData);
	    metaData.setHandler(handler);
	}

	handler.setBean(bean);
	handler.configure();

	return handler;
    }
}
