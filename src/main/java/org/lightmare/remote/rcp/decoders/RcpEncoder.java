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
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.IOException;

import org.lightmare.remote.rcp.wrappers.RcpWrapper;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.serialization.NativeSerializer;

/**
 * Encoder (extends {@link ChannelOutboundHandlerAdapter}) class @see <a
 * href="http://static.netty.io/3.6/guide/">io.netty</a> for serialize
 * {@link RcpWrapper} <a href="io.netty"/>netty></a> RPC server response
 * 
 * @author Levan Tsinadze
 * @since 0.0.21-SNAPSHOT
 */
public class RcpEncoder extends ChannelOutboundHandlerAdapter {

    /**
     * Translates boolean to numeric value
     * 
     * @author Levan Tsinadze
     * @since 0.0.84-SNAPSHOT
     */
    protected static enum BooleanNumber {

	TRUE(1), FALSE(0);

	private final int value;

	private BooleanNumber(int value) {
	    this.value = value;
	}

	public static int getValue(boolean key) {

	    int num;

	    if (key) {
		num = TRUE.value;
	    } else {
		num = FALSE.value;
	    }

	    return num;
	}
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
	    ChannelPromise promise) throws IOException {

	RcpWrapper wrapper = ObjectUtils.cast(msg, RcpWrapper.class);
	boolean valid = wrapper.isValid();
	int validNum = BooleanNumber.getValue(valid);
	Object value = wrapper.getValue();
	byte[] valueBt = NativeSerializer.serialize(value);
	int valueSize = valueBt.length;
	int protSize = (RpcUtils.INT_SIZE + RpcUtils.BYTE_SIZE + valueSize);
	ByteBuf buffer = ctx.alloc().buffer(protSize);
	buffer.writeInt(valueSize);
	buffer.writeByte(validNum);
	buffer.writeBytes(valueBt);

	ctx.write(buffer);
    }
}
