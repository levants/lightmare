package org.lightmare.ejb.handlers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.lightmare.remote.rpc.RPCall;
import org.lightmare.utils.RpcUtils;

/**
 * Local handler class for remote client call
 * 
 * @author levan
 * @since 0.0.26-SNAPSHOT
 */
public class BeanLocalHandler implements InvocationHandler {

    // Instance of RPC caller
    private RPCall rpCall;

    protected BeanLocalHandler(RPCall rpCall) {
	this.rpCall = rpCall;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {

	Object value = RpcUtils.callRemoteMethod(proxy, method, args, rpCall);

	return value;
    }

}
