package org.lightmare.ejb.handlers;

import java.lang.reflect.Method;

/**
 * Handler class to call EJB bean methods for REST services
 * 
 * @author levan
 * 
 */
public class RestHandler<T> {

    // Appropriated bean's handler
    private final BeanHandler handler;

    // EJB bean instance
    private final T bean;

    public RestHandler(BeanHandler handler, T bean) {

	this.handler = handler;
	this.bean = bean;
    }

    /**
     * Invokes passed {@link Method} for bean by {@link BeanHandler} instance
     * 
     * @param method
     * @param args
     * @return {@link Object}
     * @throws Throwable
     */
    public Object invoke(Method method, Object[] args) throws Throwable {

	return handler.invoke(bean, method, args);
    }

}
