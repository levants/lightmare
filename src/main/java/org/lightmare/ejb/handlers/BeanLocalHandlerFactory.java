package org.lightmare.ejb.handlers;

import org.lightmare.remote.rpc.RPCall;

/**
 * Factory class to initialize {@link BeanLocalHandler} class instance
 * 
 * @author Levan Tsinadze
 * @since 0.0.66-SNAPSHOT
 */
public class BeanLocalHandlerFactory {

    /**
     * Initializes {@link RPCall} class instance by passed RPC arguments
     * 
     * @param rpcArgs
     * @return {@link RPCall}
     */
    private static RPCall createRPCall(Object... rpcArgs) {

	RPCall call;

	String host = (String) rpcArgs[0];
	int port = (Integer) rpcArgs[1];
	call = new RPCall(host, port);

	return call;
    }

    /**
     * Creates {@link BeanLocalHandler} instance with host and port arguments
     * 
     * @param rpcArgs
     * @return {@link BeanLocalHandler}
     */
    public static BeanLocalHandler get(Object... rpcArgs) {

	BeanLocalHandler handler;

	RPCall call = createRPCall(rpcArgs);
	handler = new BeanLocalHandler(call);

	return handler;
    }
}
