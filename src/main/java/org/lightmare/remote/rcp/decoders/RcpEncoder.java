package org.lightmare.remote.rcp.decoders;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.utils.RpcUtils;

/**
 * Encoder (extends {@link SimpleChannelHandler}) class @see <a
 * href="http://static.netty.io/3.6/guide/">io.netty</a> for serialize
 * {@link RcpWrapper} <a href="io.netty"/>netty></a> RPC server response
 * 
 * @author levan
 * 
 */
public class RcpEncoder extends SimpleChannelHandler {

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent ev)
			throws Exception {

		RcpWrapper wrapper = (RcpWrapper) ev.getMessage();
		boolean valid = wrapper.isValid();

		Object value = wrapper.getValue();

		byte[] valueBt = RpcUtils.serialize(value);
		int valueSize = valueBt.length;

		int protSize = RpcUtils.INT_SIZE + RpcUtils.BYTE_SIZE + valueSize;

		ChannelBuffer buffer = ChannelBuffers.buffer(protSize);

		buffer.writeInt(valueSize);
		buffer.writeByte(valid ? 1 : 0);
		buffer.writeBytes(valueBt);

		ChannelFuture future = ev.getFuture();
		Channels.write(ctx, future, buffer);
	}
}
