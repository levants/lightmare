package org.lightmare.cache;

import java.lang.reflect.Method;

/**
 * Container class to cache {@link javax.interceptor.Interceptors} annotation
 * defined data
 * 
 * @author Levan
 * 
 */
public class InterceptorData {

    private Class<?> BeanClass;

    private Method beanMethod;

    private Class<?> interceptorCLass;

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

    public Class<?> getInterceptorCLass() {
	return interceptorCLass;
    }

    public void setInterceptorCLass(Class<?> interceptorCLass) {
	this.interceptorCLass = interceptorCLass;
    }

    public Method getInterceptorMethod() {
	return interceptorMethod;
    }

    public void setInterceptorMethod(Method interceptorMethod) {
	this.interceptorMethod = interceptorMethod;
    }
}
