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
package org.lightmare.ejb.interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.ejb.Timer;
import javax.interceptor.InvocationContext;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Implementation of {@link InvocationContext} for EJB intercepter
 * 
 * @author Levan Tsinadze
 * @since 0.0.65-SNAPSHOT
 */
public class InvocationContextImpl implements InvocationContext {

    // Caches methods in order for proceed calls
    private Queue<Method> methods = new LinkedList<Method>();

    // Parameters for intercepted method
    private Object[] parameters;

    // Caches object in order to proceed method calls in chain
    private Queue<Object> targets = new LinkedList<Object>();

    // Cached context data
    private Map<String, Object> contextData = new HashMap<String, Object>();

    // Timer instance
    private Timer timer;

    /**
     * Constructor with {@link Queue} of called {@link Method}s, {@link Queue}
     * of EJB target {@link Object}s and {@link Object} array of called
     * {@link Method}s parameters
     * 
     * @param methods
     * @param targets
     * @param parameters
     */
    public InvocationContextImpl(Queue<Method> methods, Queue<Object> targets,
	    Object[] parameters) {
	this.methods = methods;
	this.targets = targets;
	this.parameters = parameters;
    }

    /**
     * Constructor with {@link Queue} of called {@link Method}s, {@link Queue}
     * of EJB target {@link Object}s, {@link Object} array of called
     * {@link Method}s parameters and {@link Timer} instance
     * 
     * @param methods
     * @param targets
     * @param parameters
     * @param timer
     */
    public InvocationContextImpl(Queue<Method> methods, Queue<Object> targets,
	    Object[] parameters, Timer timer) {
	this(methods, targets, parameters);
	this.timer = timer;
    }

    @Override
    public Object getTarget() {

	Object target = targets.peek();

	return target;
    }

    @Override
    public Method getMethod() {
	return methods.peek();
    }

    @Override
    public Object[] getParameters() {
	return parameters;
    }

    @Override
    public void setParameters(Object[] parameters) {
	this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getContextData() {
	return contextData;
    }

    @Override
    public Object getTimer() {
	// TODO find out usage of this method and write implementation
	return timer;
    }

    @Override
    public Object proceed() throws Exception {

	Object value;

	Method method = methods.poll();
	Object target = targets.poll();
	if (ObjectUtils.notNull(method) && ObjectUtils.notNull(target)) {
	    value = ClassUtils.invokePrivate(method, target, this);
	} else {
	    value = null;
	}

	return value;
    }
}
