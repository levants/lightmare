package org.lightmare.rest.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.model.Parameter;
import org.lightmare.utils.ObjectUtils;

/**
 * Utility class to extract parameters from REST request
 * 
 * @author levan
 * 
 */
public class RequestUtils {

    private static final int EMPRTY_LENGTH = 0;

    public static Object[] getEmptyArray() {

	return new Object[EMPRTY_LENGTH];
    }

    public static InputStream textToStream(String text) {

	return new ByteArrayInputStream(text.getBytes());
    }

    public static List<InputStream> textsToStreams(Collection<String> params) {

	List<InputStream> streams = new ArrayList<InputStream>();
	if (ObjectUtils.available(params)) {
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
