package org.lightmare.remote.rcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;

/**
 * Handler @see {@link SimpleChannelHandler} for RPC response
 * 
 * @author levan
 * 
 */
public class RcpHandler extends SimpleChannelHandler {

    private BlockingQueue<RcpWrapper> answer;

    public RcpHandler() {
	answer = new LinkedBlockingQueue<RcpWrapper>();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent ev) {

	ev.getFuture().getChannel().close().awaitUninterruptibly()
		.addListener(new ChannelFutureListener() {
		    public void operationComplete(ChannelFuture future)
			    throws Exception {
			boolean offered = answer.offer((RcpWrapper) ev
				.getMessage());
			assert offered;
		    }
		});
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ev) {

	ev.getCause().printStackTrace();
	ev.getChannel().close().awaitUninterruptibly();
    }

    public RcpWrapper getWrapper() {

	boolean interrupted = Boolean.TRUE;
	for (;;) {
	    try {
		RcpWrapper responce = answer.take();
		if (interrupted) {
		    Thread.currentThread().interrupt();
		}
		
		return responce;
	    } catch (InterruptedException ex) {
		interrupted = Boolean.FALSE;
	    }
	}
    }
}
