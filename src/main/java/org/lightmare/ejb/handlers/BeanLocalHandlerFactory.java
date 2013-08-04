package org.lightmare.ejb.handlers;

import org.lightmare.remote.rpc.RPCall;

public class BeanLocalHandlerFactory {

    public static BeanLocalHandler get(Object... rpcArgs) {

	String host = (String) rpcArgs[0];
	int port = (Integer) rpcArgs[1];
	RPCall call = new RPCall(host, port);
	BeanLocalHandler handler = new BeanLocalHandler(call);

	return handler;
    }
}
