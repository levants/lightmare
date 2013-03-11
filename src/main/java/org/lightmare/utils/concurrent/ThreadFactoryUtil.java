package org.lightmare.utils.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * Implementation of {@link ThreadFactory} for
 * {@link java.util.concurrent.ExecutorService} thread pooling
 * 
 * @author levan
 * 
 */
public class ThreadFactoryUtil implements ThreadFactory {

    private String name;

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
	if (priority != null) {
	    thread.setPriority(priority);
	}
	thread.setName(String.format("%s - %s", name, thread.getId()));
	return thread;
    }

}
