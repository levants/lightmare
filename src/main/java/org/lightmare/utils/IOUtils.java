/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class to work with streams and other IO operations
 * 
 * @author Levan Tsinadze
 * @since 0.81-SNAPSHOT
 */
public class IOUtils {

    // IO bytes buffer default size
    public static final int BUFFER_SIZE = 1024;

    // Zero bytes offset value
    public static final int ZERO_OFFSET = 0;

    // Value of not available InputStream check
    public static final int ZERO_AVAILABLE_STREAM = 0;

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

    /**
     * Checks if passed {@link java.io.InputStream} is null or not available
     * 
     * @param stream
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean notAvailable(InputStream stream) throws IOException {
	return ((stream == null) || (stream.available() == ZERO_AVAILABLE_STREAM));
    }

    /**
     * Checks if passed {@link java.io.InputStream} is available
     * 
     * @param stream
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean available(InputStream stream) throws IOException {
	return ObjectUtils.notNull(stream)
		&& stream.available() > ZERO_AVAILABLE_STREAM;
    }

    /**
     * Writes passed {@link InputStream} to associated {@link OutputStream}
     * instance
     * 
     * @param in
     * @param out
     * @throws IOException
     */
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
