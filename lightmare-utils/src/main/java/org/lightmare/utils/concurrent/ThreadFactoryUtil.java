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
package org.lightmare.utils.concurrent;

import java.util.concurrent.ThreadFactory;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Implementation of {@link ThreadFactory} for
 * {@link java.util.concurrent.ExecutorService} thread pooling
 *
 * @author Levan Tsinadze
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

	String threadName = StringUtils.concat(name, StringUtils.HYPHEN, thread.getId());
	thread.setName(threadName);

	return thread;
    }
}
