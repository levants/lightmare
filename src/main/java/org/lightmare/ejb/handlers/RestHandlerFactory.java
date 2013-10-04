package org.lightmare.ejb.handlers;

/**
 * Factory class to create a {@link RestHandler} instance for REST services
 * 
 * @author levan
 * 
 */
public class RestHandlerFactory {

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
