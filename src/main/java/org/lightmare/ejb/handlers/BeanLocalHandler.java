package org.lightmare.ejb.handlers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.lightmare.remote.rpc.RPCall;
import org.lightmare.utils.RpcUtils;

public class BeanLocalHandler implements InvocationHandler {

	@SuppressWarnings("unused")
	private RPCall rpCall;

	public BeanLocalHandler(RPCall rpCall) {
		this.rpCall = rpCall;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object value = RpcUtils.callRemoteMethod(proxy, method, args);
		return value;
	}

}
