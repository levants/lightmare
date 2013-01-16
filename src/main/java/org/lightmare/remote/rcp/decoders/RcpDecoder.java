package org.lightmare.remote.rcp.decoders;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.utils.RpcUtils;

public class RcpDecoder extends FrameDecoder {

	@Override
	protected RcpWrapper decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {

		if (buffer.readableBytes() < RpcUtils.INT_SIZE + RpcUtils.BYTE_SIZE) {
			buffer.resetReaderIndex();
			return null;
		}

		boolean valid = buffer.readByte() > 0;
		int dataSize = buffer.readInt();

		if (buffer.readableBytes() < dataSize) {
			buffer.resetReaderIndex();
			return null;
		}

		byte[] data = new byte[dataSize];
		Object value = RpcUtils.deserialize(data);
		RcpWrapper rcp = new RcpWrapper();
		rcp.setValid(valid);
		rcp.setValue(value);

		return rcp;
	}

}
