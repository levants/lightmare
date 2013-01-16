package org.lightmare.remote.rpc.decoders;

import java.lang.reflect.Method;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.lightmare.remote.Listener;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

public class RpcEncoder extends SimpleChannelHandler {

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent ev)
			throws Exception {

		RpcWrapper wrapper = (RpcWrapper) ev.getMessage();

		String beanName = wrapper.getBeanName();
		Method beanMethod = wrapper.getBeanMethod();
		Class<?> interfaceClass = wrapper.getInterfaceClass();
		Object[] params = wrapper.getParams();

		byte[] beanNameBt = beanName.getBytes("UTF8");
		byte[] beanMethodBt = Listener.serialize(beanMethod);
		byte[] interfaceClassBt = Listener.serialize(interfaceClass);
		byte[] paramBt = Listener.serialize(params);

		int paramsSize = Listener.PROTOCOL_SIZE + beanNameBt.length
				+ beanMethodBt.length + interfaceClassBt.length
				+ paramBt.length;

		ChannelBuffer buffer = ChannelBuffers.buffer(paramsSize);

		buffer.writeInt(beanNameBt.length);
		buffer.writeInt(beanMethodBt.length);
		buffer.writeInt(interfaceClassBt.length);
		buffer.writeInt(paramBt.length);

		buffer.writeBytes(beanNameBt);
		buffer.writeBytes(beanMethodBt);
		buffer.writeBytes(interfaceClassBt);
		buffer.writeBytes(paramBt);

		ChannelFuture future = ev.getFuture();
		Channels.write(ctx, future, buffer);

	}
}
