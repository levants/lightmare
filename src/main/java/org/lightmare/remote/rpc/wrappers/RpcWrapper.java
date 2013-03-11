package org.lightmare.remote.rpc.wrappers;

/**
 * RPC request wrapper class for serialization
 * 
 * @author levan
 * 
 */
public class RpcWrapper {

    private String beanName;

    private String methodName;

    private Class<?>[] paramTypes;

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

    public String getMethodName() {
	return methodName;
    }

    public void setMethodName(String methodName) {
	this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
	return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
	this.paramTypes = paramTypes;
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
