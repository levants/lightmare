package org.lightmare.ejb.interceptors;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.interceptor.InvocationContext;

import org.lightmare.cache.InterceptorData;
import org.lightmare.cache.MetaData;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Handler class to invoke EJB bean method intercepts
 * 
 * @author Levan Tsinadze
 * @since 0.1.3
 */
public class InterceptorHandler {

    // Interceptors for given bean instance
    private final Collection<InterceptorData> interceptorDatas;

    /**
     * Constructor with {@link MetaData} to set {@link InterceptorData}
     * parameters
     * 
     * @param metaData
     */
    public InterceptorHandler(MetaData metaData) {
	this.interceptorDatas = metaData.getInterceptors();
    }

    /**
     * Fills {@link Queue} of methods and targets for specified bean
     * {@link Method} and {@link InterceptorData} object
     * 
     * @param interceptorData
     * @param methods
     * @param targets
     * @throws IOException
     */
    private void fillInterceptor(InterceptorData interceptorData,
	    Queue<Method> methods, Queue<Object> targets) throws IOException {

	Class<?> interceptorClass = interceptorData.getInterceptorClass();
	Object interceptor = ClassUtils.instantiate(interceptorClass);
	Method method = interceptorData.getInterceptorMethod();
	methods.offer(method);
	targets.offer(interceptor);
    }

    /**
     * Fills {@link Queue} of methods and targets for specified bean
     * {@link Method} and {@link InterceptorData}'s collection
     * 
     * @param method
     * @param methods
     * @param targets
     * @throws IOException
     */
    private void fillInterceptors(Method method, Queue<Method> methods,
	    Queue<Object> targets) throws IOException {

	Iterator<InterceptorData> interceptors = interceptorDatas.iterator();
	InterceptorData interceptor;
	boolean valid;
	while (interceptors.hasNext()) {
	    interceptor = interceptors.next();
	    valid = checkInterceptor(interceptor, method);
	    if (valid) {
		fillInterceptor(interceptor, methods, targets);
	    }
	}
    }

    /**
     * Checks if current {@link javax.interceptor.Interceptors} data is valid
     * for specified {@link Method} call
     * 
     * @param interceptor
     * @param method
     * @return <code>boolean</code>
     */
    private boolean checkInterceptor(InterceptorData interceptor, Method method) {

	boolean valid;

	Method beanMethod = interceptor.getBeanMethod();
	if (ObjectUtils.notNull(beanMethod)) {
	    valid = beanMethod.equals(method);
	} else {
	    valid = Boolean.TRUE;
	}

	return valid;
    }

    /**
     * Initializes and invokes {@link InvocationContext} implementation
     * 
     * @param method
     * @param parameters
     * @return Array of {@link Object} parameters for intercepted method
     * @throws IOException
     */
    private Object[] callInterceptorContext(Method method, Object[] parameters)
	    throws IOException {

	Object[] intercepteds;

	Queue<Method> methods = new LinkedList<Method>();
	Queue<Object> targets = new LinkedList<Object>();
	fillInterceptors(method, methods, targets);
	// Initializes invocation context
	InvocationContext context = new InvocationContextImpl(methods, targets,
		parameters);
	try {
	    context.proceed();
	    intercepteds = context.getParameters();
	} catch (Exception ex) {
	    throw new IOException(ex);
	}

	return intercepteds;
    }

    /**
     * Invokes first method from {@link javax.interceptor.Interceptors}
     * annotated data
     * 
     * @param method
     * @param parameters
     * @throws IOException
     */
    public Object[] callInterceptors(Method method, Object[] parameters)
	    throws IOException {

	Object[] intercepteds;

	if (CollectionUtils.valid(interceptorDatas)) {
	    intercepteds = callInterceptorContext(method, parameters);
	} else {
	    intercepteds = parameters;
	}

	return intercepteds;
    }
}
