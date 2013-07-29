package org.lightmare.rest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.model.Parameter;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Translates REST request parameters to java objects
 * 
 * @author levan
 * 
 */
public class ParamBuilder {

    private MediaType mediaType;

    private List<Parameter> parameters;

    private ContainerRequestContext request;

    private MultivaluedMap<String, String> httpHeaders;

    private MultivaluedMap<String, String> uriParams;

    private MessageBodyWorkers workers;

    private MessageBodyReader<?> reader;

    private boolean check;

    private List<Object> paramsList;

    private static final int ZERO_AVAILABLE_STREAM = 0;

    private static final int PARAM_VALUES_INDEX = 0;

    private static final int PARAM_VALIES_LENGTH = 1;

    private ParamBuilder() {

    }

    public ParamBuilder(MediaType mediaType, List<Parameter> parameters,
	    MessageBodyWorkers workers, ContainerRequestContext request) {
	this.mediaType = mediaType;
	this.parameters = parameters;
	this.workers = workers;
	this.request = request;
    }

    /**
     * Creates error message if one of the necessary fields in null
     * 
     * @return String
     * @throws IOException
     */
    private String errorOnBuild() throws IOException {

	String errorPrefix = "Could not initialize ";
	String errorClass = this.getClass().getName();
	String errorReasonPrefix = " caouse";
	String errorReasonSuffix = "is null";

	String errorMessageBody;
	if (mediaType == null) {
	    errorMessageBody = "mediaType";
	} else if (parameters == null) {
	    errorMessageBody = "parameters";
	} else if (workers == null) {
	    errorMessageBody = "workers";
	} else if (request == null) {
	    errorMessageBody = "request";
	} else {
	    throw new IOException("Could not find null value");
	}

	String errorMessage = new StringBuilder().append(errorPrefix)
		.append(errorClass).append(errorReasonPrefix)
		.append(errorMessageBody).append(errorReasonSuffix).toString();

	return errorMessage;
    }

    /**
     * Check if one of the necessary fields in null and if it is throws
     * {@link IOException} with generated error message
     * 
     * @return <code>boolean</code>
     * @throws IOException
     */
    private boolean checkOnBuild() throws IOException {

	boolean valid = ObjectUtils.notNullAll(mediaType, parameters, workers,
		request);

	if (ObjectUtils.notTrue(valid)) {
	    String errorMessage = errorOnBuild();
	    throw new IOException(errorMessage);
	}
	return valid;
    }

    private boolean check() throws IOException {

	return ObjectUtils.notTrue(request.hasEntity())
		&& request.getEntityStream().available() == ZERO_AVAILABLE_STREAM;
    }

    private void copyAll(MultivaluedMap<String, String> from,
	    MultivaluedMap<String, String> to) {

	for (Map.Entry<String, List<String>> entry : from.entrySet()) {

	    to.addAll(entry.getKey(), entry.getValue());
	}
    }

    private void addAll(MultivaluedMap<String, String> from,
	    MultivaluedMap<String, String> to) {

	if (ObjectUtils.available(from)) {
	    copyAll(from, to);
	}
    }

    /**
     * Extracts all parameters from {@link ContainerRequestContext} object
     * 
     * @param request
     * @return {@link MultivaluedMap}<String, String>
     */
    private MultivaluedMap<String, String> extractParameters(
	    ContainerRequestContext request) {

	MultivaluedMap<String, String> params = new MultivaluedStringMap();
	MultivaluedMap<String, String> exts;
	boolean decode = Boolean.TRUE;
	UriInfo uriInfo = request.getUriInfo();
	exts = request.getHeaders();
	addAll(exts, params);
	exts = uriInfo.getPathParameters(decode);
	addAll(exts, params);
	exts = uriInfo.getQueryParameters(decode);
	addAll(exts, params);
	Map<String, Cookie> cookies = request.getCookies();
	if (ObjectUtils.available(cookies)) {
	    for (Map.Entry<String, Cookie> entry : cookies.entrySet()) {
		params.putSingle(entry.getKey(), entry.getValue().toString());
	    }
	}

	return params;
    }

    private Object getEntityStream(Parameter parameter) {

	List<String> paramValues = uriParams.get(parameter.getSourceName());
	String value;
	Object stream;
	if (ObjectUtils.available(paramValues)) {
	    if (paramValues.size() == PARAM_VALIES_LENGTH) {
		value = paramValues.get(PARAM_VALUES_INDEX);
		stream = RequestUtils.textToStream(value);
	    } else {
		stream = RequestUtils.textsToStreams(paramValues);
	    }
	} else {
	    stream = null;
	}

	return stream;
    }

    private Object getEntityStream(ContainerRequestContext request,
	    Parameter parameter) {

	Object entityStream;
	if (check) {
	    entityStream = getEntityStream(parameter);
	} else {
	    entityStream = request.getEntityStream();
	}

	return entityStream;
    }

    private boolean available(InputStream entityStream, Parameter parameter) {

	return ObjectUtils.notNullAll(reader, entityStream)
		&& reader.isReadable(parameter.getRawType(),
			parameter.getType(), parameter.getAnnotations(),
			mediaType);
    }

    /**
     * Extracts parameter from passed {@link InputStream} (writes appropriate
     * value to {@link Object} instance)
     * 
     * @param parameter
     * @param entityStream
     * @return {@link Object}
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object extractParam(Parameter parameter, InputStream entityStream)
	    throws IOException {

	Object param;
	try {
	    param = reader.readFrom((Class) parameter.getRawType(),
		    parameter.getType(), parameter.getAnnotations(), mediaType,
		    httpHeaders, entityStream);

	} finally {

	    if (check) {
		ObjectUtils.close(entityStream);
	    }
	}

	return param;
    }

    private void addParam(Object param) {

	if (ObjectUtils.notNull(param)) {
	    paramsList.add(param);
	}
    }

    /**
     * Initializes and adds null parameter to {@link List} of parameters
     * 
     * @param parameter
     */
    private void addNullParam(Parameter parameter) {

	Class<?> paramType = parameter.getRawType();
	Object nullParam;
	if (paramType.isPrimitive()) {
	    nullParam = MetaUtils.getDefault(paramType);
	} else {
	    nullParam = null;
	}
	paramsList.add(nullParam);
    }

    /**
     * Reads from {@link InputStream} to java {@link Object} instance
     * 
     * @param entityStream
     * @param parameter
     * @throws IOException
     */
    private void readFromStream(InputStream entityStream, Parameter parameter)
	    throws IOException {

	boolean valid = available(entityStream, parameter);
	if (valid) {
	    Object param = extractParam(parameter, entityStream);

	    addParam(param);
	}
    }

    /**
     * Reads from {@link InputStream} or {@link List} of {@link InputStream} in
     * case that parameters are multi valued
     * 
     * @param stream
     * @param parameter
     * @throws IOException
     */
    private void fillParamList(Object stream, Parameter parameter)
	    throws IOException {

	InputStream entityStream;

	if (stream instanceof InputStream) {
	    entityStream = (InputStream) stream;
	    readFromStream(entityStream, parameter);
	} else if (stream instanceof List) {
	    @SuppressWarnings("unchecked")
	    Iterator<InputStream> streams = ((List<InputStream>) stream)
		    .iterator();
	    while (streams.hasNext()) {
		entityStream = streams.next();
		readFromStream(entityStream, parameter);
	    }
	}
    }

    /**
     * Extracts parameters from {@link ContainerRequestContext} instance
     * 
     * @return {@link List}<Object>
     * @throws IOException
     */
    public List<Object> extractParams() throws IOException {

	paramsList = new ArrayList<Object>();
	check = check();
	httpHeaders = request.getHeaders();
	uriParams = extractParameters(request);
	Object stream;
	for (Parameter parameter : parameters) {

	    reader = RequestUtils.getReader(workers, parameter, mediaType);

	    stream = getEntityStream(request, parameter);
	    if (ObjectUtils.notNull(stream)) {
		fillParamList(stream, parameter);
	    } else {
		addNullParam(parameter);
	    }
	}

	return paramsList;
    }

    public static final class Builder {

	private ParamBuilder target;

	public Builder() {
	    target = new ParamBuilder();
	}

	/**
	 * Adds {@link MediaType} necessary parameter
	 * 
	 * @param mediaType
	 * @return {@link Builder}
	 */
	public ParamBuilder.Builder setMediaType(MediaType mediaType) {
	    target.mediaType = mediaType;

	    return this;
	}

	/**
	 * Adds {@link List}<Parameter> necessary parameter
	 * 
	 * @param parameters
	 * @return {@link Builder}
	 */
	public ParamBuilder.Builder setParameters(List<Parameter> parameters) {
	    target.parameters = parameters;

	    return this;
	}

	/**
	 * Adds {@link MessageBodyWorkers} necessary parameter
	 * 
	 * @param workers
	 * @return {@link Builder}
	 */
	public ParamBuilder.Builder setWorkers(MessageBodyWorkers workers) {
	    target.workers = workers;

	    return this;
	}

	/**
	 * Adds {@link ContainerRequestContext} necessary parameter
	 * 
	 * @param request
	 * @return {@link Builder}
	 */
	public ParamBuilder.Builder setRequest(ContainerRequestContext request) {
	    target.request = request;

	    return this;
	}

	public ParamBuilder build() throws IOException {

	    // TODO Check if there is a another way to create ParamBuilder
	    // instance or

	    // checks all parameters not to be null
	    target.checkOnBuild();

	    return target;
	}
    }
}
