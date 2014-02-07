/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.remote.rcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.ObjectUtils;

import antlr.debug.MessageEvent;

/**
 * Handler @see {@link ChannelInboundHandlerAdapter} for RPC response
 * 
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RcpHandler extends ChannelInboundHandlerAdapter {

    // Responses queue
    private BlockingQueue<RcpWrapper> answer;

    /**
     * Implementation for {@link ChannelFutureListener} for remote procedure
     * call
     * 
     * @author Levan Tsinadze
     * 
     */
    protected static class ResponseListener implements ChannelFutureListener {

	private final BlockingQueue<RcpWrapper> answer;

	private final MessageEvent ev;

	public ResponseListener(final BlockingQueue<RcpWrapper> answer,
		final MessageEvent ev) {

	    this.answer = answer;
	    this.ev = ev;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
	    boolean offered = answer.offer((RcpWrapper) ev.getSource());
	    assert offered;
	}
    }

    public RcpHandler() {
	answer = new LinkedBlockingQueue<RcpWrapper>();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

	try {
	    RcpWrapper wrapper = ObjectUtils.cast(msg, RcpWrapper.class);
	    boolean offered = answer.offer(wrapper);
	    assert offered;
	} finally {
	    ctx.close();
	}
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
