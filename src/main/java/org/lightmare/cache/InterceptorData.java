/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.cache;

import java.lang.reflect.Method;

/**
 * Container class to cache {@link javax.interceptor.Interceptors} annotation
 * defined data
 * 
 * @author Levan Tsinadze
 * @since 0.0.57-SNAPSHOT
 */
public class InterceptorData {

    // Annotated EJB bean class
    private Class<?> BeanClass;

    // Annotated EJB bean method
    private Method beanMethod;

    // Interceptor implementation class
    private Class<?> interceptorClass;

    // Method which should be called at interception time
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
