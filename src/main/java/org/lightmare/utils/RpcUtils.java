package org.lightmare.utils;

import java.io.IOException;
import java.lang.reflect.Method;

import org.lightmare.ejb.EjbConnector;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Listener class for serialization and de-serialization (both java and json) of
 * objects and call bean {@link Method}s connection to bean remotely
 * 
 * @author Levan
 * 
 */
public class RpcUtils {

    // Remote arguments length for client mode
    public static final int RPC_ARGS_LENGTH = 2;

    public static final String RPC_ARGS_ERROR = "Could not resolve host and port arguments";

    public static final int PROTOCOL_SIZE = 20;

    public static final int INT_SIZE = 4;

    public static final int BYTE_SIZE = 1;

    /**
     * Calls remote method for java RPC api
     * 
     * @param proxy
     * @param method
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object callRemoteMethod(Object proxy, Method method,
	    Object[] arguments, RPCall rpCall) throws IOException {

	RpcWrapper wrapper = new RpcWrapper();
	wrapper.setBeanName(proxy.getClass().getSimpleName());
	wrapper.setMethodName(method.getName());
	wrapper.setParamTypes(method.getParameterTypes());
	wrapper.setInterfaceClass(proxy.getClass());
	wrapper.setParams(arguments);

	return rpCall.call(wrapper);
    }

    /**
     * Calls {@link javax.ejb.Stateless} bean method by {@link RcpWrapper} for
     * java RPC calls
     * 
     * @param wrapper
     * @return {@link Object}
     * @throws IOException
     */
    public static Object callBeanMethod(RpcWrapper wrapper) throws IOException {
	
Object value;

	String beanName = wrapper.getBeanName();
	String methodName = wrapper.getMethodName();
	Class<?>[] paramTypes = wrapper.getParamTypes();
	Class<?> interfaceClass = wrapper.getInterfaceClass();
	Object[] params = wrapper.getParams();

	Object bean = new EjbConnector()
		.connectToBean(beanName, interfaceClass);
	Class<?> beanClass = bean.getClass();
	Method beanMethod = MetaUtils.getDeclaredMethod(beanClass, methodName,
		paramTypes);
	 value= MetaUtils.invoke(beanMethod, bean, params);

	return value;
    }
}
