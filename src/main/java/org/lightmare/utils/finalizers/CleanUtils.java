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
public class CleanUtils {

    private static final Set<PhantomReference<Cleanable>> PHANTOMS = new HashSet<PhantomReference<Cleanable>>();

    private static final ReferenceQueue<Cleanable> REFERENCE_QUEUE = new ReferenceQueue<Cleanable>();

    private static final String REFERENCE_THREAD_NAME = "Finalizer-thread-";

    private static Thread cleaner;

    /**
     * Implementation of Runnable for cleaner thread
     * 
     * @author Levan Tsinadze
     * @since 0.1.0-SNAPSHOT
     */
    private static class CleanerTask implements Runnable {

	private void clearReference(PhantomReference<Cleanable> ref) {

	    try {
		ref.clear();
	    } finally {
		PHANTOMS.remove(ref);
	    }
	}

	@Override
	public void run() {

	    while (Boolean.TRUE) {
		try {
		    PhantomReference<Cleanable> ref = ObjectUtils
			    .cast(REFERENCE_QUEUE.remove());
		    if (ObjectUtils.notNull(ref)) {
			clearReference(ref);
		    }
		} catch (Throwable ex) {
		    LOG.error(ex.getMessage(), ex);
		}
	    }
	}
    }

    private static final Logger LOG = Logger.getLogger(CleanUtils.class);

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
	    }

	    super.clear();
	}
    }

    private static void initCleaner() {

	if (cleaner == null) {
	    cleaner = new Thread(new CleanerTask());
	    cleaner.setName(StringUtils.concat(REFERENCE_THREAD_NAME,
		    cleaner.getId()));
	    cleaner.setDaemon(Boolean.TRUE);
	    cleaner.start();
	}
    }

    public static void add(Cleanable context) {

	if (cleaner == null) {
	    synchronized (CleanUtils.class) {
		initCleaner();
	    }
	}

	FinReference reference = new FinReference(context, REFERENCE_QUEUE);
	reference.enqueue();
	PHANTOMS.add(reference);
    }
}
