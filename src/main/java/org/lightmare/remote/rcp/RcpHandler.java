package org.lightmare.remote.rcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

import antlr.debug.MessageEvent;

/**
 * Handler @see {@link SimpleChannelHandler} for RPC response
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RcpHandler extends ChannelInboundHandlerAdapter {

    // Responses queue
    private BlockingQueue<RcpWrapper> answer;

    /**
     * Implementation for {@link ChannelFutureListener} for remote procedure
     * call
     * 
     * @author levan
     * 
     */
    private static class ResponseListener implements ChannelFutureListener {

	private final BlockingQueue<RcpWrapper> answer;

	private final MessageEvent ev;

	public ResponseListener(final BlockingQueue<RcpWrapper> answer,
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
		.addListener(new ResponseListener(answer, ev));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

	cause.printStackTrace();
	ctx.close();
    }

    /**
     * Gets {@link RpcWrapper} after waiting
     * 
     * @return {@link RpcWrapper}
     */
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
