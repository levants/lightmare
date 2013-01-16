package org.lightmare.remote.rcp.decoders;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.Listener;

public class RcpEncoder extends SimpleChannelHandler {

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent ev)
			throws Exception {

		Object value = ev.getMessage();

		byte[] valueBt = Listener.serialize(value);
		int valueSize = valueBt.length;

		int protSize = Listener.INT_SIZE + valueSize;

		ChannelBuffer buffer = ChannelBuffers.buffer(protSize);

		buffer.writeInt(valueSize);
		buffer.writeBytes(valueBt);

		ChannelFuture future = ev.getFuture();
		Channels.write(ctx, future, buffer);
	}
}
