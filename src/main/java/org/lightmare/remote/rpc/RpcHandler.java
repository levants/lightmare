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
package org.lightmare.remote.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;

/**
 * Handler @see {@link ChannelInboundHandlerAdapter} for RPC request
 * 
 * @author Levan Tsinadze
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
