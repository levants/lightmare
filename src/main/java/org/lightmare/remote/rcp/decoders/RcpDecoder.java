package org.lightmare.remote.rcp.decoders;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.lightmare.remote.Listener;

public class RcpDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {

		if (buffer.readableBytes() < Listener.INT_SIZE) {
			buffer.resetReaderIndex();
			return null;
		}

		int dataSize = buffer.readInt();

		if (buffer.readableBytes() < dataSize) {
			buffer.resetReaderIndex();
			return null;
		}

		byte[] data = new byte[dataSize];

		Object value = Listener.deserialize(data);

		return value;
	}

}
