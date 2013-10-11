package org.lightmare.remote.rpc.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Decoder class for netty remote procedure call
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer,
	    List<Object> out) throws IOException {

	if (buffer.readableBytes() < RpcUtils.PROTOCOL_SIZE) {
	    buffer.resetReaderIndex();
	    return;
	}

	int beanNameSize = buffer.readInt();
	int methodSize = buffer.readInt();
	int paramTypesSize = buffer.readInt();
	int classSize = buffer.readInt();
	int paramArraySize = buffer.readInt();

	int paramsSize = beanNameSize + methodSize + classSize + paramArraySize;

	if (buffer.readableBytes() < paramsSize) {
	    buffer.resetReaderIndex();
	    return;
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
	String methodName = (String) NativeSerializer.deserialize(methodBt);
	Class<?>[] paramTypes = (Class<?>[]) NativeSerializer
		.deserialize(paramTypesBt);
	Class<?> interfaceClass = (Class<?>) NativeSerializer
		.deserialize(classBt);
	Object[] params = (Object[]) NativeSerializer.deserialize(paramBt);

	wrapper.setBeanName(beanName);
	wrapper.setMethodName(methodName);
	wrapper.setParamTypes(paramTypes);
	wrapper.setInterfaceClass(interfaceClass);
	wrapper.setParams(params);

	out.add(wrapper);
    }
}
