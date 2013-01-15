package org.lightmare.remote.rpc.decoders;

import java.lang.reflect.Method;

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

		byte[][] paramsBt = new byte[params.length][];

		int firstSize = beanNameBt.length + beanMethodBt.length
				+ interfaceClassBt.length + params.length;
	}
}
