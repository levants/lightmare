package org.lightmare.utils.finalizers;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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

    private static final Logger LOG = Logger.getLogger(CleanUtils.class);

    static {
	Thread referenceThread = new Thread() {
	    public void run() {

		while (Boolean.TRUE) {
		    try {
			PhantomReference<Cleanable> ref = ObjectUtils
				.cast(REFERENCE_QUEUE.remove());
			if (ObjectUtils.notNull(ref)) {
			    ref.clear();
			    PHANTOMS.remove(ref);
			}
		    } catch (Throwable ex) {
			LOG.error(ex.getMessage(), ex);
		    }
		}
	    }
	};
	referenceThread.setName(StringUtils.concat(REFERENCE_THREAD_NAME,
		referenceThread.getId()));
	referenceThread.setDaemon(Boolean.TRUE);
	referenceThread.start();
    }

    /**
     * Extension of {@link PhantomReference} for cleaning unused resources after
     * garbage collection
     * 
     * @author Levan Tsinadze
     * @since 0.0.85-SNAPSHOT
     */
    public static class FinReference extends PhantomReference<Cleanable> {

	private ReferenceQueue<Cleanable> queue;

	private Cleanable referent;

	public FinReference(Cleanable referent, ReferenceQueue<Cleanable> queue) {
	    super(referent, queue);
	    this.queue = queue;
	    this.referent = referent;
	}

	@Override
	public void clear() {

	    try {
		Reference<Cleanable> reference = ObjectUtils.cast(queue
			.remove());
		if (ObjectUtils.notNull(reference)) {
		    Cleanable cleanable = reference.get();
		    if (ObjectUtils.notNull(cleanable)) {
			cleanable.clean();
		    }
		}

		if (ObjectUtils.notNull(referent)) {
		    referent.clean();
		    referent = null;
		}
	    } catch (InterruptedException ex) {
		LOG.error(ex.getMessage(), ex);
	    } catch (IOException ex) {
		LOG.error(ex.getMessage(), ex);
	    }

	    super.clear();
	}
    }

    public static void add(Cleanable context) {

	FinReference reference = new FinReference(context, REFERENCE_QUEUE);
	reference.enqueue();
	PHANTOMS.add(reference);
    }
}
