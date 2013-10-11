package org.lightmare.remote.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;

/**
 * Handler @see {@link SimpleChannelHandler} for RPC request
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RpcHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = Logger.getLogger(RpcHandler.class);

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg)
	    throws IOException {

	RpcWrapper wrapper = ObjectUtils.cast(msg, RpcWrapper.class);

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

	final ChannelFuture future = ctx.writeAndFlush(rcp);

	future.addListener(new ChannelFutureListener() {
	    @Override
	    public void operationComplete(ChannelFuture lisFuture) {

		try {
		    assert (future == lisFuture);
		} finally {
		    ctx.close();
		}
	    }
	});
    }
}
