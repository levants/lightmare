package org.lightmare.ejb.handlers;

import org.lightmare.remote.rpc.RPCall;

/**
 * Factory class to initialize {@link BeanLocalHandler} class instance
 * 
 * @author Levan
 * 
 */
public class BeanLocalHandlerFactory {

    private static RPCall createRPCall(Object... rpcArgs) {

	String host = (String) rpcArgs[0];
	int port = (Integer) rpcArgs[1];
	RPCall call = new RPCall(host, port);

	return call;
    }

    /**
     * Creates {@link BeanLocalHandler} instance with host and port arguments
     * 
     * @param rpcArgs
     * @return {@link BeanLocalHandler}
     */
    public static BeanLocalHandler get(Object... rpcArgs) {

	RPCall call = createRPCall(rpcArgs);
	BeanLocalHandler handler = new BeanLocalHandler(call);

	return handler;
    }
}
