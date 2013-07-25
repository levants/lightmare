package org.lightmare.remote.rpc.decoders;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Encoder class for netty remote procedure call
 * 
 * @author levan
 * 
 */
public class RpcEncoder extends SimpleChannelHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent ev)
	    throws IOException {

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

	ChannelBuffer buffer = ChannelBuffers.buffer(paramsSize);

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
