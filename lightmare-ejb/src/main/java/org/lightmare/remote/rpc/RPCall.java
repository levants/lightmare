/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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

import java.io.IOException;

import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.remote.rcp.RcpHandler;
import org.lightmare.remote.rcp.decoders.RcpDecoder;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.decoders.RpcEncoder;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Client class to produce remote procedure call
 * 
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RPCall {

    private String host;

    private int port;

    private RcpHandler handler;

    private static int timeout;

    private static int workerPoolSize;

    private static EventLoopGroup worker;

    private static final int ONE_PRIORITY = 1;

    private static final int ZERO_TIMEOUT = 0;

    /**
     * Implementation of {@link ChannelInitializer} on {@link SocketChannel} for
     * RPC service client
     * 
     * @author Levan Tsinadze
     * 
     */
    protected static class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {

        private RcpHandler handler;

        public ChannelInitializerImpl(RcpHandler handler) {
            this.handler = handler;
        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {

            RpcEncoder rpcEncoder = new RpcEncoder();
            RcpDecoder rcpDecoder = new RcpDecoder();
            ch.pipeline().addLast(rpcEncoder, rcpDecoder, handler);
        }
    }

    public RPCall(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Configures RPC service client
     * 
     * @param config
     */
    public static void configure(Configuration config) {

        if (worker == null) {
            workerPoolSize = config.getIntValue(ConfigKeys.WORKER_POOL.key);
            timeout = config.getIntValue(ConfigKeys.CONNECTION_TIMEOUT.key);
            worker = new NioEventLoopGroup(workerPoolSize,
                    new ThreadFactoryUtil("netty-worker-thread", (Thread.MAX_PRIORITY - ONE_PRIORITY)));
        }
    }

    /**
     * Prepares {@link Bootstrap} for RPC service client connection
     * 
     * @return {@link Bootstrap}
     */
    private Bootstrap getBootstrap() {

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        if (timeout > ZERO_TIMEOUT) {
            bootstrap.option(ChannelOption.SO_TIMEOUT, timeout);
        }

        handler = new RcpHandler();
        bootstrap.handler(new ChannelInitializerImpl(handler));

        return bootstrap;
    }

    /**
     * Calls RPC service for passed {@link RcpWrapper} instance
     * 
     * @param wrapper
     * @return {@link Object}
     * @throws IOException
     */
    public Object call(RpcWrapper wrapper) throws IOException {

        Object value;

        try {
            Bootstrap bootstrap = getBootstrap();
            try {
                ChannelFuture future = bootstrap.connect(host, port).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
            value = handler.getWrapper();
        } finally {
            worker.shutdownGracefully();
        }

        return value;
    }
}
