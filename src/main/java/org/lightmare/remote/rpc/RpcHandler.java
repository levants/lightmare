package org.lightmare.remote.rpc;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.RpcUtils;

/**
 * Handler @see {@link SimpleChannelHandler} for RPC request
 * 
 * @author levan
 * 
 */
public class RpcHandler extends SimpleChannelHandler {

    private static final Logger LOG = Logger.getLogger(RpcHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev)
	    throws IOException {
	RpcWrapper wrapper = (RpcWrapper) ev.getMessage();
	SocketAddress address = ev.getRemoteAddress();

	RcpWrapper rcp = new RcpWrapper();
	Object value;
	try {
	    value = RpcUtils.callBeanMethod(wrapper);
	    rcp.setValid(Boolean.TRUE);
	} catch (Exception ex) {
	    LOG.error(ex.getMessage(), ex);
	    value = ex;
	}

	rcp.setValue(value);
	ev.getChannel().write(rcp, address);
	try {
	    super.messageReceived(ctx, ev);
	} catch (Exception ex) {
	    throw new IOException(ex);
	}
    }
}
