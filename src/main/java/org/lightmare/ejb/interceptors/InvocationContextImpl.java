package org.lightmare.ejb.interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.interceptor.InvocationContext;

import org.lightmare.utils.reflect.MetaUtils;

/**
 * Implementation of {@link InvocationContext} for lightmare ejb interceptors
 * 
 * @author Levan
 * 
 */
public class InvocationContextImpl implements InvocationContext {

    private Queue<Method> methods = new LinkedList<Method>();

    private Object[] parameters;

    private Queue<Object> targets = new LinkedList<Object>();

    private Map<String, Object> contextData = new HashMap<String, Object>();

    public InvocationContextImpl(Queue<Method> methods, Object[] parameters,
	    Queue<Object> targets, Map<String, Object> contextData) {
	this.methods = methods;
	this.parameters = parameters;
	this.targets = targets;
	this.contextData = contextData;
    }

    public void addMethod(Method method) {
	methods.offer(method);
    }

    public void addTarget(Object target) {
	targets.offer(target);
    }

    public void putContextData(String key, Object value) {
	contextData.put(key, value);
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
    public Object proceed() throws Exception {

	Method method = methods.poll();
	Object target = targets.poll();
	Object value = MetaUtils.invoke(method, target, parameters);

	return value;
    }
}
