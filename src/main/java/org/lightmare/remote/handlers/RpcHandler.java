package org.lightmare.remote.handlers;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.handlers.wrappers.RpcWrapper;

public class RpcHandler extends SimpleChannelHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev)
			throws Exception {
		RpcWrapper wrapper = (RpcWrapper) ev.getMessage();
	}
}
