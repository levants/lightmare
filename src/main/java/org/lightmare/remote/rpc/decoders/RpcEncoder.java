package org.lightmare.remote.rpc.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.IOException;
import java.nio.channels.Channels;

import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Encoder class for netty remote procedure call
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RpcEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
	    ChannelPromise promise) throws IOException {

	RpcWrapper wrapper = (RpcWrapper) ev.getMessage();

	String beanName = wrapper.getBeanName();
	String methodName = wrapper.getMethodName();
	Class<?>[] paramTypes = wrapper.getParamTypes();
	Class<?> interfaceClass = wrapper.getInterfaceClass();
	Object[] params = wrapper.getParams();

	byte[] beanNameBt = beanName.getBytes("UTF8");
	byte[] beanMethodBt = NativeSerializer.serialize(methodName);
	byte[] paramTypesBt = NativeSerializer.serialize(paramTypes);
	byte[] interfaceClassBt = NativeSerializer.serialize(interfaceClass);
	byte[] paramBt = NativeSerializer.serialize(params);

	int paramsSize = RpcUtils.PROTOCOL_SIZE + beanNameBt.length
		+ beanMethodBt.length + paramTypesBt.length
		+ interfaceClassBt.length + paramBt.length;

	ByteBuf buffer = ctx.alloc().buffer(paramsSize);

	buffer.writeInt(beanNameBt.length);
	buffer.writeInt(beanMethodBt.length);
	buffer.writeInt(paramTypesBt.length);
	buffer.writeInt(interfaceClassBt.length);
	buffer.writeInt(paramBt.length);

	buffer.writeBytes(beanNameBt);
	buffer.writeBytes(beanMethodBt);
	buffer.writeBytes(paramTypesBt);
	buffer.writeBytes(interfaceClassBt);
	buffer.writeBytes(paramBt);

	ChannelFuture future = ev.getFuture();
	Channels.write(ctx, future, buffer);
    }
}
