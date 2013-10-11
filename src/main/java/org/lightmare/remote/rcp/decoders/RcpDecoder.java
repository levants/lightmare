package org.lightmare.remote.rcp.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Decoder (extends {@link FrameDecoder}) class @see <a
 * href="http://static.netty.io/3.6/guide/">io.netty</a> for response on <a
 * href="io.netty"/>netty></a> RCP returns {@link RcpWrapper}
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RcpDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer,
	    List<Object> out) throws IOException {

	if (buffer.readableBytes() < RpcUtils.INT_SIZE + RpcUtils.BYTE_SIZE) {
	    buffer.resetReaderIndex();
	    return;
	}

	boolean valid = buffer.readByte() > 0;
	int dataSize = buffer.readInt();

	if (buffer.readableBytes() < dataSize) {
	    buffer.resetReaderIndex();
	    return;
	}

	byte[] data = new byte[dataSize];
	Object value = NativeSerializer.deserialize(data);
	RcpWrapper rcp = new RcpWrapper();
	rcp.setValid(valid);
	rcp.setValue(value);

	out.add(rcp);
    }
}
