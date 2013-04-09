package org.lightmare.remote.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
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
import org.lightmare.config.Configuration;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.remote.rcp.RcpHandler;
import org.lightmare.remote.rcp.decoders.RcpDecoder;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.remote.rpc.decoders.RpcEncoder;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

/**
 * Client class to produce remote procedure call
 * 
 * @author levan
 * 
 */
public class RPCall {

    private String host;

    private int port;

    private static long timeout;

    private static int bossPoolSize;

    private static int workerPoolSize;

    private static ExecutorService boss;

    private static ExecutorService worker;

    private static final Logger LOG = Logger.getLogger(RPCall.class);

    private RPCall() {
	configure();
    }

    public RPCall(String host, int port) {
	this();
	this.host = host;
	this.port = port;
    }

    private static void configure() {
	if (boss == null || worker == null) {

	    bossPoolSize = MetaCreator.CONFIG
		    .getIntValue(Configuration.BOSS_POOL);

	    workerPoolSize = MetaCreator.CONFIG
		    .getIntValue(Configuration.WORKER_POOL);

	    timeout = MetaCreator.CONFIG
		    .getLongValue(Configuration.CONNECTION_TIMEOUT);

	    boss = new OrderedMemoryAwareThreadPoolExecutor(bossPoolSize,
		    400000000, 2000000000, 60, TimeUnit.SECONDS,
		    new ThreadFactoryUtil("netty-boss-thread",
			    Thread.MAX_PRIORITY));
	    worker = new OrderedMemoryAwareThreadPoolExecutor(workerPoolSize,
		    400000000, 2000000000, 60, TimeUnit.SECONDS,
		    new ThreadFactoryUtil("netty-worker-thread",
			    (Thread.MAX_PRIORITY - 1)));
	}
    }

    private ClientBootstrap getBootstrap(SimpleChannelHandler handler) {

	WorkerPool<NioWorker> pool = new NioWorkerPool(worker, 1);
	ClientSocketChannelFactory factory = new NioClientSocketChannelFactory(
		boss, 1, pool);
	ClientBootstrap bootstrap = new ClientBootstrap(factory);
	bootstrap.getPipeline().addLast("encoder", new RpcEncoder());
	bootstrap.getPipeline().addLast("decoder", new RcpDecoder());
	bootstrap.getPipeline().addLast("handler", handler);
	bootstrap.setOption("tcpNoDelay", Boolean.TRUE);
	bootstrap.setOption("keepAlive", Boolean.TRUE);
	if (timeout > 0) {
	    bootstrap.setOption("connectTimeoutMillis", timeout);
	}
	return bootstrap;
    }

    public Object call(RpcWrapper wrapper) throws IOException {
	RcpHandler handler = new RcpHandler();
	ClientBootstrap bootstrap = getBootstrap(handler);
	SocketAddress address = new InetSocketAddress(host, port);
	final ChannelFuture future = bootstrap.connect(address);

	try {
	    if (!future.await(timeout, TimeUnit.MILLISECONDS)) {
		LOG.info("Trying to read data from server");
		future.awaitUninterruptibly();
	    }
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	}

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
	    } else if (ObjectUtils.notNull(value)) {
		throw new IOException((Exception) value);
	    } else {
		return value;
	    }

	} finally {
	    bootstrap.releaseExternalResources();
	}
    }
}
