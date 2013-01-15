package org.lightmare.remote.rpc;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.Listener;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

public class RpcHandler extends SimpleChannelHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev)
			throws Exception {
		RpcWrapper wrapper = (RpcWrapper) ev.getMessage();
		Object value = Listener.callBeanMethod(wrapper);
	}
}
