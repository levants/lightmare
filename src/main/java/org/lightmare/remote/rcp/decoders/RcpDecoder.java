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
package org.lightmare.remote.rcp.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Decoder (extends {@link ByteToMessageDecoder}) class @see <a
 * href="http://static.netty.io/3.6/guide/">io.netty</a> for response on <a
 * href="io.netty"/>netty></a> RCP returns {@link RcpWrapper}
 * 
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RcpDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer,
	    List<Object> out) throws IOException {

	if (buffer.readableBytes() < RpcUtils.INT_SIZE + RpcUtils.BYTE_SIZE) {
	    buffer.resetReaderIndex();
	    return;
	}

	boolean valid = buffer.readByte() > 0;
	int dataSize = buffer.readInt();

	if (buffer.readableBytes() < dataSize) {
	    buffer.resetReaderIndex();
	    return;
	}

	byte[] data = new byte[dataSize];
	Object value = NativeSerializer.deserialize(data);
	RcpWrapper rcp = new RcpWrapper();
	rcp.setValid(valid);
	rcp.setValue(value);

	out.add(rcp);
    }
}
