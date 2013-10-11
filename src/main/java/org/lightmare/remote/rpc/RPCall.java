package org.lightmare.remote.rpc;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
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
 * @since 0.0.21-SNAPSHOT
 */
public class RPCall {

    private String host;

    private int port;

    private static long timeout;

    private static int bossPoolSize;

    private static int workerPoolSize;

    private static EventLoopGroup boss;

    private static EventLoopGroup worker;

    private static final Logger LOG = Logger.getLogger(RPCall.class);

    public RPCall(String host, int port) {
	this.host = host;
	this.port = port;
    }

    public static void configure(Configuration config) {

	if (boss == null || worker == null) {

	    bossPoolSize = config.getIntValue(ConfigKeys.BOSS_POOL.key);

	    workerPoolSize = config.getIntValue(ConfigKeys.WORKER_POOL.key);

	    timeout = config.getLongValue(ConfigKeys.CONNECTION_TIMEOUT.key);

	    boss = new NioEventLoopGroup(bossPoolSize, new ThreadFactoryUtil(
		    "netty-boss-thread", Thread.MAX_PRIORITY));
	    worker = new NioEventLoopGroup(workerPoolSize,
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

	Object value;

	EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
	EventLoopGroup workerGroup = new NioEventLoopGroup();
	try {
	} finally {
	    bootstrap.releaseExternalResources();
	}

	return value;
    }
}
