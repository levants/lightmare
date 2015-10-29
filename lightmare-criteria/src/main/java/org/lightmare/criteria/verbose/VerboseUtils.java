package org.lightmare.criteria.verbose;

import java.util.function.Consumer;

/**
 * Logger for debugging and analyze
 * 
 * @author Levan Tsinadze
 *
 */
public class VerboseUtils {

    /**
     * Runs consumer if passed verbose is valid
     * 
     * @param verbose
     * @param consumer
     */
    public static void apply(boolean verbose, Consumer<Boolean> consumer) {

	if (verbose) {
	    consumer.accept(verbose);
	}
    }
}
