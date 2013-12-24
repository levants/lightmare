package org.lightmare.utils.finalizers;

import java.io.IOException;

/**
 * Interface which should be implemented for phantom reference to close unused
 * resources
 * 
 * @author Levan Tsinadze
 * @since 0.0.85-SNAPSHOT
 * @see CleanUtils
 */
public interface Cleanable {

    void clean() throws IOException;
}
