package org.lightmare.remote.rpc;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.Listener;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

public class RpcHandler extends SimpleChannelHandler {

	private static Logger LOG = Logger.getLogger(RpcHandler.class);

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev)
			throws Exception {
		RpcWrapper wrapper = (RpcWrapper) ev.getMessage();
		SocketAddress address = ev.getRemoteAddress();

		RcpWrapper rcp = new RcpWrapper();
		Object value;
		try {
			value = Listener.callBeanMethod(wrapper);
			rcp.setValid(true);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			value = ex;
		}

		rcp.setValue(value);
		ev.getChannel().write(rcp, address);
		super.messageReceived(ctx, ev);
	}
}
