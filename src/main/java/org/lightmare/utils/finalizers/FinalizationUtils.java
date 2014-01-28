package org.lightmare.utils.finalizers;

import java.io.IOException;
import java.lang.ref.PhantomReference;
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

	private void clearReference(PhantomReference<Cleanable> ref) {

	    try {
		ref.clear();
	    } finally {
		phantoms.remove(ref);
	    }
	}

	@Override
	public void run() {

	    while (Boolean.TRUE) {
		try {
		    PhantomReference<Cleanable> ref = ObjectUtils
			    .cast(references.remove());
		    if (ObjectUtils.notNull(ref)) {
			clearReference(ref);
		    }
		} catch (Throwable ex) {
		    LOG.error(ex.getMessage(), ex);
		}
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