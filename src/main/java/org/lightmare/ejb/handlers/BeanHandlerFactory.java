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
     * Sets bean instance and calls {@link BeanHandler#configure()} method
     * 
     * @param handler
     * @param bean
     * @throws IOException
     */
    private static void configure(final BeanHandler handler, final Object bean)
	    throws IOException {

	handler.setBean(bean);
	handler.configure();
    }

    private static BeanHandler cloneHandler(BeanHandler handler)
	    throws IOException {

	BeanHandler cloneHandler;
	try {
	    cloneHandler = (BeanHandler) handler.clone();
	} catch (CloneNotSupportedException ex) {
	    throw new IOException(ex);
	}

	return cloneHandler;
    }

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

	BeanHandler cloneHandler = cloneHandler(handler);
	configure(cloneHandler, bean);

	return cloneHandler;
    }
}
