package org.lightmare.remote.rcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class RcpHandler extends SimpleChannelHandler {

	private BlockingQueue<Object> answer;

	public RcpHandler() {
		answer = new LinkedBlockingQueue<Object>();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent ev) {
		ev.getFuture().getChannel().close().awaitUninterruptibly()
				.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future)
							throws Exception {
						boolean offered = answer.offer(ev.getMessage());
						assert offered;
					}
				});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ev) {
		ev.getCause().printStackTrace();
		ev.getChannel().close().awaitUninterruptibly();
	}

	public Object getWrapper() {
		boolean interrupted = false;
		for (;;) {
			try {
				Object responce = answer.take();
				if (interrupted) {
					Thread.currentThread().interrupt();
				}
				return responce;
			} catch (InterruptedException ex) {
				interrupted = true;
			}
		}
	}
}
