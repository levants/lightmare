package org.lightmare.utils;

import java.io.IOException;

/**
 * Interface which should be implemented for phantom reference to close unused
 * resouyrces
 * 
 * @author levan
 * @see CleanUtils
 */
public interface Cleanable {

    void clean() throws IOException;
}
