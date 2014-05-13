/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.utils.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.model.Parameter;
import org.lightmare.utils.CollectionUtils;

/**
 * Utility class to extract parameters from REST request
 * 
 * @author Levan Tsinadze
 * @since 0.0.74-SNAPSHOT
 */
public class RequestUtils {

    /**
     * Creates {@link InputStream} from passed {@link String} instance
     * 
     * @param text
     * @return {@link InputStream}
     */
    public static InputStream textToStream(String text) {
	return new ByteArrayInputStream(text.getBytes());
    }

    /**
     * Converts {@link Collection} of {@link String}s to {@link List} of
     * {@link InputStream}s for reading appropriate parameters
     * 
     * @param params
     * @return {@link List}<InputStream>
     */
    public static List<InputStream> textsToStreams(Collection<String> params) {

	List<InputStream> streams = new ArrayList<InputStream>();

	if (CollectionUtils.valid(params)) {
	    InputStream stream;
	    for (String param : params) {
		stream = textToStream(param);
		streams.add(stream);
	    }
	}

	return streams;
    }

    /**
     * Gets appropriated {@link MessageBodyReader} for passed
     * {@link MessageBodyWorkers} {@link Parameter} and {@link MediaType}
     * arguments
     * 
     * @param workers
     * @param parameter
     * @param mediaType
     * @return {@link MessageBodyReader}
     */
    public static MessageBodyReader<?> getReader(MessageBodyWorkers workers,
	    Parameter parameter, MediaType mediaType) {

	MessageBodyReader<?> reader = workers.getMessageBodyReader(
		parameter.getRawType(), parameter.getType(),
		parameter.getAnnotations(), mediaType);

	return reader;
    }
}
