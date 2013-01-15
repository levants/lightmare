package org.lightmare.remote.handlers.decoders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.lightmare.remote.handlers.wrappers.RpcWrapper;

public class RpcDecoder extends FrameDecoder {

	private static final int PROTOCOL_SIZE = 12;

	private static final int INT_SIZE = 4;

	private Object deserialize(byte[] data) throws IOException {

		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		ObjectInputStream objectStream = new ObjectInputStream(stream);
		try {

			Object value = objectStream.readObject();

			return value;

		} catch (ClassNotFoundException ex) {

			throw new IOException(ex);

		} finally {
			stream.close();
			objectStream.close();
		}
	}

	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {

		if (buffer.readableBytes() < PROTOCOL_SIZE) {
			buffer.resetReaderIndex();
			return null;
		}
		int methodSize = buffer.readInt();
		int classSize = buffer.readInt();
		int paramArraySize = buffer.readInt();

		int paramsSize = INT_SIZE * paramArraySize + paramArraySize
				+ methodSize + classSize + paramArraySize;

		if (buffer.readableBytes() < paramsSize) {
			buffer.resetReaderIndex();
			return null;
		}

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

			params[i] = deserialize(parameterBytes);
		}

		RpcWrapper wrapper = new RpcWrapper();

		Method beanMethod = (Method) deserialize(methodBt);
		Class<?> interfaceClass = (Class<?>) deserialize(classBt);

		wrapper.setBeanMethod(beanMethod);
		wrapper.setInterfaceClass(interfaceClass);
		wrapper.setParams(params);

		return wrapper;
	}
}
