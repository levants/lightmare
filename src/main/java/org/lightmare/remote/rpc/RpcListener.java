package org.lightmare.remote.rpc;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.lightmare.config.Configuration;
import org.lightmare.remote.rcp.decoders.RcpEncoder;
import org.lightmare.remote.rpc.decoders.RpcDecoder;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

/**
 * Registers and starts RPC server @see <a href="netty.io"/>netty.io</a>
 * 
 * @author levan
 * 
 */
public class RpcListener {

    /**
     * Boss pool for Netty network
     */
    private static ExecutorService boss;
    /**
     * Worker pool for Netty server
     */
    private static ExecutorService worker;

    /**
     * {@link NioWorkerPool} for Netty server
     */
    private static WorkerPool<NioWorker> workerPool;

    /**
     * {@link ChannelGroup} "info-channels" for only info requests
     */
    public static ChannelGroup channelGroup = new DefaultChannelGroup(
	    "info-channels");
    private static Channel channel;
    private static ServerSocketChannelFactory factory;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final Logger LOG = Logger.getLogger(RpcListener.class);

    /**
     * Set boss and worker thread pools size from configuration
     */
    private static void setNettyPools(Configuration config) {

	Integer bossCount;
	Integer workerCount;
	boss = new OrderedMemoryAwareThreadPoolExecutor(
		(bossCount = config.getIntValue("boss_pool_size")) != null ? bossCount
			: 1, 400000000, 2000000000, 60, TimeUnit.SECONDS,
		new ThreadFactoryUtil("netty-boss-thread", Thread.MAX_PRIORITY));
	worker = new OrderedMemoryAwareThreadPoolExecutor(
		(workerCount = config.getIntValue("worker_pool_size")) != null ? workerCount
			: RUNTIME.availableProcessors() * 3, 400000000,
		2000000000, 60, TimeUnit.SECONDS, new ThreadFactoryUtil(
			"netty-worker-thread", (Thread.MAX_PRIORITY - 1)));
	workerPool = new NioWorkerPool(worker, workerCount);
    }

    /**
     * Starts server
     * 
     */
    public static void startServer(Configuration config) {

	setNettyPools(config);
	factory = new NioServerSocketChannelFactory(boss, workerPool);
	ServerBootstrap bootstrap = new ServerBootstrap(factory);
	bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

	    @Override
	    public ChannelPipeline getPipeline() throws Exception {
		return Channels.pipeline(new RcpEncoder(), new RpcDecoder(),
			new RpcHandler());
	    }
	});
	bootstrap.setOption("tcpNoDelay", Boolean.TRUE);
	bootstrap.setOption("child.keepAlive", Boolean.TRUE);
	bootstrap.setOption("backlog", 500);
	bootstrap.setOption("connectTimeoutMillis",
		config.getIntValue(Configuration.CONNECTION_TIMEOUT));
	try {
	    channel = bootstrap.bind(new InetSocketAddress(Inet4Address
		    .getByName(config.getStringValue("listening_ip")), config
		    .getIntValue("listening_port")));
	    channelGroup.add(channel);
	    LOG.info(channel.getLocalAddress());
	} catch (UnknownHostException ex) {
	    LOG.error(ex.getMessage(), ex);
	}
    }
}
