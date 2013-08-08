package org.lightmare.ejb.handlers;


public class ResthandlerFactory {

    public static <T> RestHandler<T> get(BeanHandler handler, T bean) {

	RestHandler<T> restHandler = new RestHandler<T>(handler, bean);

	return restHandler;
    }
}
