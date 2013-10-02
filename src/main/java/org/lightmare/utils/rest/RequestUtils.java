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
 * @author levan
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
