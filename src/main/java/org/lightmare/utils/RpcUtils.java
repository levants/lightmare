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
package org.lightmare.utils;

import java.io.IOException;
import java.lang.reflect.Method;

import org.lightmare.ejb.EjbConnector;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Listener class for serialization and de-serialization (both java and json) of
 * objects and call bean {@link Method}s connection to bean remotely
 * 
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RpcUtils {

    // Remote arguments length for client mode
    public static final int RPC_ARGS_LENGTH = 2;

    // Error message
    public static final String RPC_ARGS_ERROR = "Could not resolve host and port arguments";

    // Size checks for protocol
    public static final int PROTOCOL_SIZE = 20;

    // Integer data type size
    public static final int INT_SIZE = 4;

    // Byte data type size
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

	Object value;

	RpcWrapper wrapper = new RpcWrapper();
	wrapper.setBeanName(proxy.getClass().getSimpleName());
	wrapper.setMethodName(method.getName());
	wrapper.setParamTypes(method.getParameterTypes());
	wrapper.setInterfaceClass(proxy.getClass());
	wrapper.setParams(arguments);
	value = rpCall.call(wrapper);

	return value;
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
	Method beanMethod = ClassUtils.getDeclaredMethod(beanClass, methodName,
		paramTypes);
	value = ClassUtils.invoke(beanMethod, bean, params);

	return value;
    }
}
