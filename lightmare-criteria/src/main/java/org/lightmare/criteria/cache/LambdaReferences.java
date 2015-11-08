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
package org.lightmare.criteria.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.lightmare.criteria.utils.StringUtils;

/**
 * References for lambda generated classes to avoid cache overflow
 * 
 * @author Levan Tsinadze
 *
 */
public enum LambdaReferences {

    INSTANCE;

    // Queue of lambda class instances being watched
    private final ReferenceQueue<Class<?>> references = new ReferenceQueue<>();

    // Daemon thread to finalize references objects
    private Thread cleaner;

    // Name of finalize daemon thread
    private static final String REFERENCE_THREAD_NAME = "lambda-finalizer-thread-";

    private static final Logger LOG = Logger.getLogger(LambdaReferences.class);

    /**
     * To get {@link Reference} from {@link ReferenceQueue} and clean resources
     * for finalization
     * 
     * @author Levan Tsinadze
     *
     */
    private class CleanerTask implements Runnable {

	@Override
	public void run() {

	    try {
		Reference<? extends Class<?>> reference = references.remove();
		if (Objects.nonNull(reference)) {
		    reference.clear();
		}
	    } catch (Throwable ex) {
		LOG.error(ex.getMessage(), ex);
	    }
	}
    }

    /**
     * Generates cleaner {@link Thread} name
     * 
     * @param cleaner
     * @return {@link String} thread name
     */
    private static String generateName(Thread cleaner) {

	String threadName;

	Long threadId = cleaner.getId();
	threadName = StringUtils.concat(REFERENCE_THREAD_NAME, threadId);

	return threadName;
    }

    /**
     * Generates and sets cleaner {@link Thread} name
     * 
     * @param cleaner
     */
    private static void setName(Thread cleaner) {

	String threadName = generateName(cleaner);
	cleaner.setName(threadName);
    }

    /**
     * Initializes and starts cleaner thread if it is not initialized yet
     */
    private void initCleaner() {

	if (cleaner == null) {
	    CleanerTask task = new CleanerTask();
	    cleaner = new Thread(task);
	    cleaner.setPriority(Thread.MAX_PRIORITY);
	    setName(cleaner);
	    cleaner.setDaemon(Boolean.TRUE);
	    cleaner.start();
	}
    }

    /**
     * Starts cleaner thread if it is not initialized yet
     */
    private void startCleaner() {

	if (cleaner == null) {
	    synchronized (LambdaReferences.class) {
		initCleaner();
	    }
	}
    }

    /**
     * Adds {@link LambdaReference} instance to be watched for finalization
     * 
     * @param lambdaType
     */
    public void trace(Class<?> lambdaType) {

	startCleaner();
	LambdaReference reference = new LambdaReference(lambdaType, references);
	reference.enqueue();
    }

    /**
     * Adds {@link LambdaReference} instance to be watched for finalization
     * 
     * @param lambda
     */
    public void traceByInstance(Object lambda) {
	trace(lambda.getClass());
    }
}
