package org.lightmare.cache;

import java.lang.reflect.Method;

/**
 * Container class to cache {@link javax.interceptor.Interceptors} annotation
 * defined data
 * 
 * @author Levan
 * @since >0.0.57-SNAPSHOT
 */
public class InterceptorData {

    private Class<?> BeanClass;

    private Method beanMethod;

    private Class<?> interceptorClass;

    private Method interceptorMethod;

    public Class<?> getBeanClass() {
	return BeanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
	BeanClass = beanClass;
    }

    public Method getBeanMethod() {
	return beanMethod;
    }

    public void setBeanMethod(Method beanMethod) {
	this.beanMethod = beanMethod;
    }

    public Class<?> getInterceptorClass() {
	return interceptorClass;
    }

    public void setInterceptorClass(Class<?> interceptorCLass) {
	this.interceptorClass = interceptorCLass;
    }

    public Method getInterceptorMethod() {
	return interceptorMethod;
    }

    public void setInterceptorMethod(Method interceptorMethod) {
	this.interceptorMethod = interceptorMethod;
    }
}
