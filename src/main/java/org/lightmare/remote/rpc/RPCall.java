package org.lightmare.remote.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.lightmare.remote.rcp.RcpHandler;
import org.lightmare.remote.rcp.decoders.RcpDecoder;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.decoders.RpcEncoder;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

public class RPCall {

	private String host = "localhost";
	private int port = 1195;
	private long timeout = 1000;

	public RPCall() {
	}

	private ClientBootstrap getBootstrap(SimpleChannelHandler handler) {
		final ExecutorService boss = new OrderedMemoryAwareThreadPoolExecutor(
				3, 400000000, 2000000000, 60, TimeUnit.SECONDS,
				new ThreadFactoryUtil("netty-boss-thread", Thread.MAX_PRIORITY));
		final ExecutorService worker = new OrderedMemoryAwareThreadPoolExecutor(
				5, 400000000, 2000000000, 60, TimeUnit.SECONDS,
				new ThreadFactoryUtil("netty-worker-thread",
						(Thread.MAX_PRIORITY - 1)));
		WorkerPool<NioWorker> pool = new NioWorkerPool(worker, 1);
		ClientSocketChannelFactory factory = new NioClientSocketChannelFactory(
				boss, 1, pool);
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.getPipeline().addLast("encoder", new RpcEncoder());
		bootstrap.getPipeline().addLast("decoder", new RcpDecoder());
		bootstrap.getPipeline().addLast("handler", handler);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		if (timeout > 0) {
			bootstrap.setOption("connectTimeoutMillis", timeout);
		}
		return bootstrap;
	}

	public Object call(RpcWrapper wrapper) throws IOException {
		RcpHandler handler = new RcpHandler();
		ClientBootstrap bootstrap = getBootstrap(handler);
		InetSocketAddress address = new InetSocketAddress(host, port);
		final ChannelFuture future = bootstrap.connect(address);
		final Channel channel = future.awaitUninterruptibly().getChannel();

		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return null;
		}
		try {
			ChannelFuture lastWriteFuture = channel.write(wrapper);
			lastWriteFuture.awaitUninterruptibly();
			RcpHandler handlerCl = (RcpHandler) lastWriteFuture
					.awaitUninterruptibly().getChannel().getPipeline()
					.getLast();
			RcpWrapper response = handlerCl.getWrapper();
			Object value = response.getValue();

			if (response.isValid()) {
				return value;
			} else if (value != null) {
				throw new IOException((Exception) value);
			} else {
				return value;
			}

		} finally {
			bootstrap.releaseExternalResources();
		}
	}
}
