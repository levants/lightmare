package org.lightmare.utils.concurrent;

import java.util.concurrent.ThreadFactory;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Implementation of {@link ThreadFactory} for
 * {@link java.util.concurrent.ExecutorService} thread pooling
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class ThreadFactoryUtil implements ThreadFactory {

    // Thread name
    private String name;

    // Thread priority
    private Integer priority;

    public ThreadFactoryUtil(String name) {
	this.name = name;
    }

    public ThreadFactoryUtil(String name, Integer priority) {
	this(name);
	this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable runnable) {

	Thread thread = new Thread(runnable);

	if (ObjectUtils.notNull(priority)) {
	    thread.setPriority(priority);
	}

	String threadName = StringUtils.concat(name, StringUtils.HYPHEN,
		thread.getId());
	thread.setName(threadName);

	return thread;
    }
}
