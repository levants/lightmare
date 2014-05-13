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
package org.lightmare.utils.finalizers;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Utility class to store {@link PhantomReference} implementations for cleaning
 * unused resources after garbage collection
 * 
 * @author Levan Tsinadze
 * @since 0.0.85-SNAPSHOT
 */
public enum FinalizationUtils {

    // Singleton instance for finalization
    INSTANCE;

    // Collection of watched objects
    private final Set<PhantomReference<Cleanable>> phantoms = new HashSet<PhantomReference<Cleanable>>();

    // Queue of Cleanable instances being watched
    private final ReferenceQueue<Cleanable> references = new ReferenceQueue<Cleanable>();

    // Daemon thread to finalize references objects
    private Thread cleaner;

    // Name of finalize daemon thread
    private static final String REFERENCE_THREAD_NAME = "custom-finalizer-thread-";

    private static final Logger LOG = Logger.getLogger(FinalizationUtils.class);

    /**
     * Constructor which should be called only within this class
     */
    private FinalizationUtils() {
    }

    /**
     * Implementation of Runnable for cleaner thread
     * 
     * @author Levan Tsinadze
     * @since 0.1.0-SNAPSHOT
     */
    private class CleanerTask implements Runnable {

	/**
	 * Clears passed {@link Cleanable} reference
	 * 
	 * @param reference
	 */
	private void clearReference(Reference<? extends Cleanable> reference) {

	    try {
		reference.clear();
	    } finally {
		phantoms.remove(reference);
	    }
	}

	/**
	 * Gets {@link Reference} from {@link ReferenceQueue} and cleans
	 * resources for finalization
	 */
	private void cleanReference() {

	    try {
		Reference<? extends Cleanable> reference = references.remove();
		if (ObjectUtils.notNull(reference)) {
		    clearReference(reference);
		}
	    } catch (Throwable ex) {
		LOG.error(ex.getMessage(), ex);
	    }
	}

	@Override
	public void run() {

	    while (Boolean.TRUE) {
		cleanReference();
	    }
	}
    }

    /**
     * Extension of {@link PhantomReference} for cleaning unused resources after
     * garbage collection
     * 
     * @author Levan Tsinadze
     * @since 0.0.85-SNAPSHOT
     */
    public static class FinReference extends PhantomReference<Cleanable> {

	private Cleanable referent;

	public FinReference(Cleanable referent, ReferenceQueue<Cleanable> queue) {
	    super(referent, queue);
	    this.referent = referent;
	}

	@Override
	public void clear() {

	    try {
		if (ObjectUtils.notNull(referent)) {
		    referent.clean();
		}
	    } catch (IOException ex) {
		LOG.error(ex.getMessage(), ex);
	    } finally {
		referent = null;
		super.clear();
	    }
	}
    }

    /**
     * Initializes and starts cleaner thread if it is not initialized yet
     */
    private void initCleaner() {

	if (cleaner == null) {
	    CleanerTask task = new CleanerTask();
	    cleaner = new Thread(task);
	    cleaner.setPriority(Thread.MAX_PRIORITY);
	    cleaner.setName(StringUtils.concat(REFERENCE_THREAD_NAME,
		    cleaner.getId()));
	    cleaner.setDaemon(Boolean.TRUE);
	    cleaner.start();
	}
    }

    /**
     * Adds {@link Cleanable} instance to be watched for finalization
     * 
     * @param context
     */
    public void trace(Cleanable context) {

	if (cleaner == null) {
	    synchronized (FinalizationUtils.class) {
		initCleaner();
	    }
	}

	FinReference reference = new FinReference(context, references);
	reference.enqueue();
	phantoms.add(reference);
    }

    /**
     * Initializes {@link FinalizationUtils} and adds {@link Cleanable} instance
     * to be trace for finalization
     * 
     * @param cleanable
     */
    public static void add(Cleanable cleanable) {
	INSTANCE.trace(cleanable);
    }
}
