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
package org.lightmare.ejb.handlers;

import java.lang.reflect.Method;

/**
 * Handler class to call EJB bean methods for REST services
 *
 * @author Levan Tsinadze
 * @since 0.0.69
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
