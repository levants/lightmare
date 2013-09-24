package org.lightmare.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class to work with streams and other IO operations
 * 
 * @author levan
 * @since 0.81-SNAPSHOT
 */
public class IOUtils {

    // IO bytes size
    public static final int BUFFER_SIZE = 1024;

    public static final int ZERO_OFFSET = 0;

    public static void write(InputStream in, OutputStream out)
	    throws IOException {

	try {
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int len = in.read(buffer);
	    while (ObjectUtils.notEquals(len, ObjectUtils.NOT_EXISTING_INDEX)) {
		out.write(buffer, ZERO_OFFSET, len);
		len = in.read(buffer);
	    }
	} finally {
	    ObjectUtils.closeAll(in, out);
	}
    }
}
