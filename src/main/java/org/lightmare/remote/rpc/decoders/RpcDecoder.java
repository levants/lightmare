package org.lightmare.remote.rpc.decoders;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.RpcUtils;

/**
 * Decoder class for netty remote procedure call
 * 
 * @author levan
 * 
 */
public class RpcDecoder extends FrameDecoder {

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel,
	    ChannelBuffer buffer) throws IOException {

	if (buffer.readableBytes() < RpcUtils.PROTOCOL_SIZE) {
	    buffer.resetReaderIndex();
	    return null;
	}

	int beanNameSize = buffer.readInt();
	int methodSize = buffer.readInt();
	int paramTypesSize = buffer.readInt();
	int classSize = buffer.readInt();
	int paramArraySize = buffer.readInt();

	int paramsSize = beanNameSize + methodSize + classSize + paramArraySize;

	if (buffer.readableBytes() < paramsSize) {
	    buffer.resetReaderIndex();
	    return null;
	}

	byte[] beanNameBt = new byte[beanNameSize];
	buffer.readBytes(beanNameBt);

	byte[] methodBt = new byte[methodSize];
	buffer.readBytes(methodBt);

	byte[] paramTypesBt = new byte[paramTypesSize];
	buffer.readBytes(paramTypesBt);

	byte[] classBt = new byte[classSize];
	buffer.readBytes(classBt);

	byte[] paramBt = new byte[paramArraySize];
	buffer.readBytes(paramBt);

	RpcWrapper wrapper = new RpcWrapper();

	String beanName = new String(beanNameBt);
	String methodName = (String) RpcUtils.deserialize(methodBt);
	Class<?>[] paramTypes = (Class<?>[]) RpcUtils.deserialize(paramTypesBt);
	Class<?> interfaceClass = (Class<?>) RpcUtils.deserialize(classBt);
	Object[] params = (Object[]) RpcUtils.deserialize(paramBt);

	wrapper.setBeanName(beanName);
	wrapper.setMethodName(methodName);
	wrapper.setParamTypes(paramTypes);
	wrapper.setInterfaceClass(interfaceClass);
	wrapper.setParams(params);

	return wrapper;
    }
}
