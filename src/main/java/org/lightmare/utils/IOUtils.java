package org.lightmare.utils;

import java.io.Closeable;
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

    // IO bytes buffer size
    public static final int BUFFER_SIZE = 1024;

    public static final int ZERO_OFFSET = 0;

    /**
     * Checks if passed {@link Closeable} instance is not null and if not calls
     * {@link Closeable#close()} method
     * 
     * @param closeable
     * @throws IOException
     */
    public static void close(Closeable closeable) throws IOException {

	if (ObjectUtils.notNull(closeable)) {
	    closeable.close();
	}
    }

    /**
     * Checks if passed array of {@link Closeable}'s is valid and closes all of
     * them
     * 
     * @param closeables
     * @throws IOException
     */
    public static void closeAll(Closeable... closeables) throws IOException {

	if (CollectionUtils.valid(closeables)) {
	    for (Closeable closeable : closeables) {
		close(closeable);
	    }
	}
    }

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
	    closeAll(in, out);
	}
    }
}
