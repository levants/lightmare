/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.remote.rpc.decoders;

import java.io.IOException;
import java.util.List;

import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.io.serialization.NativeSerializer;
import org.lightmare.utils.remote.RpcUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Decoder class for Netty remote procedure call
 *
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> out) throws IOException {

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
        Class<?>[] paramTypes = (Class<?>[]) NativeSerializer.deserialize(paramTypesBt);
        Class<?> interfaceClass = (Class<?>) NativeSerializer.deserialize(classBt);
        Object[] params = (Object[]) NativeSerializer.deserialize(paramBt);

        wrapper.setBeanName(beanName);
        wrapper.setMethodName(methodName);
        wrapper.setParamTypes(paramTypes);
        wrapper.setInterfaceClass(interfaceClass);
        wrapper.setParams(params);

        out.add(wrapper);
    }
}
