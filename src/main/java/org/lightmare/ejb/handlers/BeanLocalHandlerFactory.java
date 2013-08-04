package org.lightmare.ejb.handlers;

import org.lightmare.remote.rpc.RPCall;

public class BeanLocalHandlerFactory {

    private static RPCall getRPCall(Object... rpcArgs) {

	String host = (String) rpcArgs[0];
	int port = (Integer) rpcArgs[1];
	RPCall call = new RPCall(host, port);

	return call;
    }

    public static BeanLocalHandler get(Object... rpcArgs) {

	RPCall call = getRPCall(rpcArgs);
	BeanLocalHandler handler = new BeanLocalHandler(call);

	return handler;
    }
}
