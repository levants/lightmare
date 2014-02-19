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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.remote.rcp.decoders.RcpEncoder;
import org.lightmare.remote.rpc.decoders.RpcDecoder;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

/**
 * Registers and starts RPC server @see <a href="netty.io"/>netty.io</a>
 * 
 * @author Levan Tsinadze
 * @since 0.0.22-SNAPSHOT
 */
public class RpcListener {

    // Boss pool for Netty network
    private static EventLoopGroup boss;

    // Worker pool for Netty server
    private static EventLoopGroup worker;

    private static final String BOSS_THREAD_NAME = "netty-boss-thread";

    private static final String WORKER_THREAD_NAME = "netty-boss-thread";

    private static final int WORKER_THEAD_PRIORITY = Thread.MAX_PRIORITY - 1;

    private static final Logger LOG = Logger.getLogger(RpcListener.class);

    /**
     * Implementation of {@link ChannelInitializer} on {@link SocketChannel} for
     * RPC service
     * 
     * @author Levan Tsinadze
     * 
     */
    protected static class ChannelInitializerImpl extends
	    ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) throws Exception {

	    RcpEncoder rcpEncoder = new RcpEncoder();
	    RpcDecoder rpcDecoder = new RpcDecoder();
	    RpcHandler rpcHandler = new RpcHandler();
	    ch.pipeline().addLast(rcpEncoder, rpcDecoder, rpcHandler);
	}
    }

    /**
     * Set boss and worker thread pools size from configuration
     */
    private static void setNettyPools(Configuration config) {

	Integer bossCount = config.getIntValue(ConfigKeys.BOSS_POOL.key);
	if (bossCount == null) {
	    bossCount = ConfigKeys.BOSS_POOL.getValue();
	}

	Integer workerCount = config.getIntValue(ConfigKeys.WORKER_POOL.key);
	if (workerCount == null) {
	    workerCount = ConfigKeys.WORKER_POOL.getValue();
	}

	boss = new NioEventLoopGroup(bossCount, new ThreadFactoryUtil(
		BOSS_THREAD_NAME, Thread.MAX_PRIORITY));
	worker = new NioEventLoopGroup(workerCount, new ThreadFactoryUtil(
		WORKER_THREAD_NAME, WORKER_THEAD_PRIORITY));
    }

    /**
     * Starts RPC server
     * 
     */
    public static void startServer(Configuration config) {

	setNettyPools(config);

	try {
	    ServerBootstrap bootstrap = new ServerBootstrap();
	    bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
		    .childHandler(new ChannelInitializerImpl());

	    bootstrap.option(ChannelOption.SO_BACKLOG, 500);
	    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
	    bootstrap.childOption(ChannelOption.SO_TIMEOUT,
		    config.getIntValue(ConfigKeys.CONNECTION_TIMEOUT.key));

	    InetSocketAddress address = new InetSocketAddress(
		    Inet4Address.getByName(config
			    .getStringValue("listening_ip")),
		    config.getIntValue("listening_port"));
	    ChannelFuture future = bootstrap.bind(address).sync();
	    LOG.info(future);
	} catch (UnknownHostException ex) {
	    LOG.error(ex.getMessage(), ex);
	} catch (InterruptedException ex) {
	    LOG.error(ex.getMessage(), ex);
	}
    }
}
