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

import org.apache.log4j.Logger;
import org.lightmare.criteria.utils.ObjectUtils;
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
     * Removes {@link java.lang.ref.Reference} from queue
     * {@link java.lang.ref.ReferenceQueue} cache
     * 
     * @return {@link java.lang.ref.Reference} removed from
     *         {@link java.lang.ref.ReferenceQueue}
     */
    private Reference<?> remove() {

        Reference<?> reference;

        try {
            reference = references.remove();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        return reference;
    }

    /**
     * Cleans first {@link java.lang.ref.Reference} element from
     * {@link java.lang.ref.ReferenceQueue} after reclaim
     */
    private void cleaner() {

        try {
            ObjectUtils.ifNotNull(this::remove, Reference::clear);
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Initializes and starts cleaner thread if it is not initialized yet
     */
    private Thread initCleaner() {

        cleaner = new Thread(this::cleaner);

        cleaner.setPriority(Thread.MAX_PRIORITY);
        String threadName = StringUtils.concat(REFERENCE_THREAD_NAME, cleaner.getId());
        cleaner.setName(threadName);
        cleaner.setDaemon(Boolean.TRUE);

        return cleaner;
    }

    /**
     * Starts cleaner thread if it is not initialized yet
     */
    private void startCleaner() {

        if (cleaner == null) {
            synchronized (LambdaReferences.class) {
                ObjectUtils.thisOrDefault(cleaner, this::initCleaner, Thread::start);
            }
        }
    }

    /**
     * Adds {@link org.lightmare.criteria.cache.LambdaReference} instance to be
     * watched for finalization
     * 
     * @param lambdaType
     */
    public void traceByType(Class<?> lambdaType) {

        startCleaner();
        Reference<?> reference = new LambdaReference(lambdaType, references);
        reference.enqueue();
    }

    /**
     * Adds {@link org.lightmare.criteria.cache.LambdaReference} instance to be
     * watched for finalization
     * 
     * @param lambdaType
     */
    public static void trace(Class<?> lambdaType) {
        INSTANCE.traceByType(lambdaType);
    }

    /**
     * Adds {@link org.lightmare.criteria.cache.LambdaReference} instance to be
     * watched for finalization
     * 
     * @param lambda
     */
    public void traceByInstance(Object lambda) {
        traceByType(lambda.getClass());
    }
}
