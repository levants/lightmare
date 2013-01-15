package org.lightmare.remote.rpc.decoders;

import java.lang.reflect.Method;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.lightmare.remote.Listener;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;

public class RpcDecoder extends FrameDecoder {


	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {

		if (buffer.readableBytes() < Listener.PROTOCOL_SIZE) {
			buffer.resetReaderIndex();
			return null;
		}

		int beanNameSize = buffer.readInt();
		int methodSize = buffer.readInt();
		int classSize = buffer.readInt();
		int paramArraySize = buffer.readInt();

		int paramsSize = Listener.INT_SIZE * paramArraySize + paramArraySize
				+ methodSize + classSize + paramArraySize;

		if (buffer.readableBytes() < paramsSize) {
			buffer.resetReaderIndex();
			return null;
		}

		byte[] beanNameBt = new byte[beanNameSize];
		buffer.readBytes(beanNameBt);

		byte[] methodBt = new byte[methodSize];
		buffer.readBytes(methodBt);

		byte[] classBt = new byte[classSize];
		buffer.readBytes(classBt);

		int paramSize;
		byte[] parameterBytes = new byte[paramArraySize];
		Object[] params = new Object[paramArraySize];
		for (int i = 0; i < paramArraySize; i++) {
			paramSize = buffer.readInt();
			parameterBytes = new byte[paramSize];
			buffer.readBytes(parameterBytes);

			params[i] = Listener.deserialize(parameterBytes);
		}

		RpcWrapper wrapper = new RpcWrapper();

		String beanName = new String(beanNameBt);
		Method beanMethod = (Method) Listener.deserialize(methodBt);
		Class<?> interfaceClass = (Class<?>) Listener.deserialize(classBt);

		wrapper.setBeanName(beanName);
		wrapper.setBeanMethod(beanMethod);
		wrapper.setInterfaceClass(interfaceClass);
		wrapper.setParams(params);

		return wrapper;
	}
}
