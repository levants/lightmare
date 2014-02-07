/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.IOException;

import org.lightmare.remote.rpc.wrappers.RpcWrapper;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Encoder class for Netty remote procedure call
 * 
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RpcEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
	    ChannelPromise promise) throws IOException {

	RpcWrapper wrapper = ObjectUtils.cast(msg, RpcWrapper.class);

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

	ctx.write(buffer);
    }
}
