package org.lightmare.utils.finalizers;

import java.io.IOException;

/**
 * Interface which should be implemented for phantom reference to close unused
 * resources
 * 
 * @author Levan Tsinadze
 * @since 0.0.85-SNAPSHOT
 * @see FinalizationUtils
 */
public interface Cleanable {

    /**
     * Should be implemented as resources cleaner after object was collected
     * instead of override {@link Object}'s finalize method
     * 
     * @throws IOException
     */
    void clean() throws IOException;
}
