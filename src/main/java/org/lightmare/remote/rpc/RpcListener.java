package org.lightmare.remote.rpc;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.remote.rcp.decoders.RcpEncoder;
import org.lightmare.remote.rpc.decoders.RpcDecoder;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;

/**
 * Registers and starts RPC server @see <a href="netty.io"/>netty.io</a>
 * 
 * @author levan
 * @since 0.0.22-SNAPSHOT
 */
public class RpcListener {

    /**
     * Boss pool for Netty network
     */
    private static EventLoopGroup boss;
    /**
     * Worker pool for Netty server
     */
    private static EventLoopGroup worker;

    private static final Logger LOG = Logger.getLogger(RpcListener.class);

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
		"netty-boss-thread", Thread.MAX_PRIORITY));
	worker = new NioEventLoopGroup(workerCount, new ThreadFactoryUtil(
		"netty-worker-thread", (Thread.MAX_PRIORITY - 1)));
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
		config.getIntValue(ConfigKeys.CONNECTION_TIMEOUT.key));
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
