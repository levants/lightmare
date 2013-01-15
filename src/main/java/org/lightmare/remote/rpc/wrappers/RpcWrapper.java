package org.lightmare.remote.rpc.wrappers;

import java.lang.reflect.Method;

public class RpcWrapper {

	private String beanName;

	private Method beanMethod;

	private Class<?> interfaceClass;

	private Object[] params;

	public RpcWrapper() {
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
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
