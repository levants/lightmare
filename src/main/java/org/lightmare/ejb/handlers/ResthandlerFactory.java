package org.lightmare.ejb.handlers;

/**
 * Factory class to create a {@link RestHandler} instance
 * 
 * @author levan
 * 
 */
public class ResthandlerFactory {

    /**
     * Creates {@link RestHandler} instance
     * 
     * @param handler
     * @param bean
     * @return {@link RestHandler}
     */
    public static <T> RestHandler<T> get(BeanHandler handler, T bean) {

	RestHandler<T> restHandler = new RestHandler<T>(handler, bean);

	return restHandler;
    }
}
