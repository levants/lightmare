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
     * @param context
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
