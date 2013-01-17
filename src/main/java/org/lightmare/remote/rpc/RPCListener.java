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
public class RPCListener {

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
	 * {@link Configuration} container class for server
	 */
	public static final Configuration configuration = new Configuration();

	/**
	 * {@link ChannelGroup} "info-channels" for only info requests
	 */
	public static ChannelGroup channelGroup = new DefaultChannelGroup(
			"info-channels");
	private static Channel channel;
	private static ServerSocketChannelFactory factory;
	private static final Runtime runtime = Runtime.getRuntime();
	private static Logger logger = Logger.getLogger(RPCListener.class);

	/**
	 * Set boss and worker thread pools size from configuration
	 */
	private static void setNettyPools() {
		Integer bossCount;
		Integer workerCount;
		boss = new OrderedMemoryAwareThreadPoolExecutor(
				(bossCount = configuration.getIntValue("boss_pool_size")) != null ? bossCount
						: 1, 400000000, 2000000000, 60, TimeUnit.SECONDS,
				new ThreadFactoryUtil("netty-boss-thread", Thread.MAX_PRIORITY));
		worker = new OrderedMemoryAwareThreadPoolExecutor(
				(workerCount = configuration.getIntValue("worker_pool_size")) != null ? workerCount
						: runtime.availableProcessors() * 3, 400000000,
				2000000000, 60, TimeUnit.SECONDS, new ThreadFactoryUtil(
						"netty-worker-thread", (Thread.MAX_PRIORITY - 1)));
		workerPool = new NioWorkerPool(worker, workerCount);
	}

	/**
	 * Starts server
	 * 
	 * @throws Exception
	 */
	public static void startServer() throws Exception {
		setNettyPools();
		factory = new NioServerSocketChannelFactory(boss, workerPool);
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new RcpEncoder(), new RpcDecoder(),
						new RpcHandler());
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("backlog", 500);
		bootstrap.setOption("connectTimeoutMillis", 10000);
		try {
			channel = bootstrap.bind(new InetSocketAddress(Inet4Address
					.getByName(configuration.getStringValue("listening_ip")),
					configuration.getIntValue("listening_port")));
			channelGroup.add(channel);
			logger.info(channel.getLocalAddress());
		} catch (UnknownHostException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
