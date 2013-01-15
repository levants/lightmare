package org.lightmare.remote.handlers.wrappers;

import java.lang.reflect.Method;

public class RpcWrapper {

	private Method beanMethod;

	private Class<?> interfaceClass;

	private Object[] params;

	public RpcWrapper() {
	}

	public Method getBeanMethod() {
		return beanMethod;
	}

	public void setBeanMethod(Method beanMethod) {
		this.beanMethod = beanMethod;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}
}
