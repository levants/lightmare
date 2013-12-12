package org.lightmare.utils;

import java.io.IOException;

/**
 * Interface which should be implemented for phantom reference
 * 
 * @author levan
 * @see CleanUtils
 */
public interface Cleanable {

    void clean() throws IOException;
}
