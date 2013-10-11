package org.lightmare.remote.rcp.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.IOException;

import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Encoder (extends {@link SimpleChannelHandler}) class @see <a
 * href="http://static.netty.io/3.6/guide/">io.netty</a> for serialize
 * {@link RcpWrapper} <a href="io.netty"/>netty></a> RPC server response
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RcpEncoder extends ChannelOutboundHandlerAdapter {

    /**
     * Translates boolean to numeric value
     * 
     * @author levan
     * @since 0.0.84-SNAPSHOT
     */
    protected static enum BooleanNumber {

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
	    ChannelPromise promise) throws IOException {

	RcpWrapper wrapper = ObjectUtils.cast(msg, RcpWrapper.class);
	boolean valid = wrapper.isValid();

	Object value = wrapper.getValue();

	byte[] valueBt = NativeSerializer.serialize(value);
	int valueSize = valueBt.length;

	int protSize = RpcUtils.INT_SIZE + RpcUtils.BYTE_SIZE + valueSize;

	ByteBuf buffer = ctx.alloc().buffer(protSize);

	buffer.writeInt(valueSize);
	buffer.writeByte(valid ? 1 : 0);
	buffer.writeBytes(valueBt);

	ctx.write(buffer);
    }
}
