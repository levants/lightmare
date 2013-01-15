package org.lightmare.remote.rpc.decoders;

import java.lang.reflect.Method;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
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

		int paramArraySize = params.length;

		byte[][] paramsBt = new byte[paramArraySize][];

		int paramsSize = 0;
		for (int i = 0; i < paramArraySize; i++) {
			paramsBt[i] = Listener.serialize(params[i]);
			paramsSize += paramsBt[i].length;
		}

		paramsSize += Listener.INT_SIZE * paramArraySize
				+ Listener.PROTOCOL_SIZE;

		ChannelBuffer buffer = ChannelBuffers.buffer(paramsSize);

	}
}
