package org.lightmare.remote.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.remote.rcp.RcpHandler;
import org.lightmare.remote.rcp.decoders.RcpDecoder;
import org.lightmare.remote.rpc.decoders.RpcEncoder;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

/**
 * Client class to produce remote procedure call
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RPCall {

    private String host;

    private int port;

    private RcpHandler handler;

    private static int timeout;

    private static int workerPoolSize;

    private static EventLoopGroup worker;
    
    private static final int ZERO_TIMEOUT = 0;

    public RPCall(String host, int port) {
	this.host = host;
	this.port = port;
    }

    public static void configure(Configuration config) {

	if (worker == null) {

	    workerPoolSize = config.getIntValue(ConfigKeys.WORKER_POOL.key);

	    timeout = config.getIntValue(ConfigKeys.CONNECTION_TIMEOUT.key);

	    worker = new NioEventLoopGroup(workerPoolSize,
		    new ThreadFactoryUtil("netty-worker-thread",
			    (Thread.MAX_PRIORITY - 1)));
	}
    }

    private Bootstrap getBootstrap() {

	Bootstrap bootstrap = new Bootstrap();
	bootstrap.group(worker);
	bootstrap.channel(NioSocketChannel.class);
	bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

	if (timeout > ZERO_TIMEOUT) {
	    bootstrap.option(ChannelOption.SO_TIMEOUT, timeout);
	}

	handler = new RcpHandler();

	bootstrap.handler(new ChannelInitializer<SocketChannel>() {

	    @Override
	    public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new RpcEncoder(), new RcpDecoder(),
			handler);
	    }
	});

	return bootstrap;
    }

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
