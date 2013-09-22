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

    // Responses queue
    private BlockingQueue<RcpWrapper> answer;

    /**
     * Implementation for {@link ChannelFutureListener} for remote procedure
     * call
     * 
     * @author levan
     * 
     */
    private static class ResponceListener implements ChannelFutureListener {

	private final BlockingQueue<RcpWrapper> answer;

	private final MessageEvent ev;

	public ResponceListener(final BlockingQueue<RcpWrapper> answer,
		final MessageEvent ev) {

	    this.answer = answer;
	    this.ev = ev;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
	    boolean offered = answer.offer((RcpWrapper) ev.getMessage());
	    assert offered;
	}
    }

    public RcpHandler() {
	answer = new LinkedBlockingQueue<RcpWrapper>();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent ev) {

	ev.getFuture().getChannel().close().awaitUninterruptibly()
		.addListener(new ResponceListener(answer, ev));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ev) {

	ev.getCause().printStackTrace();
	ev.getChannel().close().awaitUninterruptibly();
    }

    public RcpWrapper getWrapper() {

	RcpWrapper responce;
	boolean interrupted = Boolean.TRUE;
	for (;;) {
	    try {
		responce = answer.take();
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
