package org.lightmare.rest.providers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Parameter;
import org.lightmare.cache.MetaData;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.utils.ObjectUtils;

/**
 * {@link Inflector} implementation for ejb beans
 * 
 * @author Levan
 * 
 */
public class RestInflector implements
	Inflector<ContainerRequestContext, Response> {

    private Method method;

    private MetaData metaData;

    private MediaType type;

    private List<Parameter> parameters;

    private static final int PARAMS_DEF_LENGTH = 0;

    private static final int ZERO_AVAILABLE_STREAM = 0;

    @Context
    private MessageBodyWorkers workers;

    private static final Logger LOG = Logger.getLogger(RestInflector.class);

    public RestInflector(Method method, MetaData metaData, MediaType type,
	    List<Parameter> parameters) {

	this.method = method;
	this.metaData = metaData;
	this.type = type;
	this.parameters = parameters;
    }

    private MediaType getMediaType(ContainerRequestContext request) {

	MediaType mediaType = request.getMediaType();
	if (mediaType == null && type == null) {
	    mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
	} else if (mediaType == null && ObjectUtils.notNull(type)) {
	    mediaType = type;
	}

	return mediaType;
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

    private MultivaluedMap<String, String> extractParameters(
	    ContainerRequestContext request) {

	MultivaluedMap<String, String> params = new MultivaluedStringMap();
	MultivaluedMap<String, String> exts;

	UriInfo uriInfo = request.getUriInfo();
	exts = request.getHeaders();
	addAll(exts, params);
	exts = uriInfo.getPathParameters();
	addAll(exts, params);
	exts = uriInfo.getQueryParameters();
	addAll(exts, params);
	Map<String, Cookie> cookies = request.getCookies();
	if (ObjectUtils.available(cookies)) {
	    for (Map.Entry<String, Cookie> entry : cookies.entrySet()) {
		params.putSingle(entry.getKey(), entry.getValue().toString());
	    }
	}

	return params;
    }

    private boolean check(ContainerRequestContext request) throws IOException {

	return !request.hasEntity()
		&& request.getEntityStream().available() == ZERO_AVAILABLE_STREAM;
    }

    private boolean available(InputStream entityStream,
	    MessageBodyReader<?> reader, Parameter parameter,
	    MediaType mediaType) {

	return ObjectUtils.notNullAll(reader, entityStream)
		&& reader.isReadable(parameter.getRawType(),
			parameter.getType(), parameter.getAnnotations(),
			mediaType);
    }

    private MessageBodyReader<?> getReader(Parameter parameter,
	    MediaType mediaType) {

	MessageBodyReader<?> reader = workers.getMessageBodyReader(
		parameter.getRawType(), parameter.getType(),
		parameter.getAnnotations(), mediaType);

	return reader;
    }

    protected InputStream getEntityStream(Parameter parameter,
	    MultivaluedMap<String, String> params, MediaType mediaType) {

	List<String> paramValues = params.get(parameter.getSourceName());
	String value;
	InputStream entityStream;
	if (ObjectUtils.available(paramValues)) {
	    if (paramValues.size() == 1) {
		value = paramValues.get(0);
	    } else {
		Entity<List<String>> entity = Entity.entity(paramValues,
			mediaType);
		value = entity.toString();
	    }
	    entityStream = new ByteArrayInputStream(value.getBytes());
	} else {
	    entityStream = null;
	}

	return entityStream;
    }

    private InputStream getEntityStream(boolean check,
	    ContainerRequestContext request, Parameter parameter,
	    MultivaluedMap<String, String> params, MediaType mediaType) {

	InputStream entityStream;
	if (check) {
	    entityStream = getEntityStream(parameter, params, mediaType);
	} else {
	    entityStream = request.getEntityStream();
	}

	return entityStream;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object extractParam(MessageBodyReader<?> reader,
	    Parameter parameter, MediaType mediaType, InputStream entityStream,
	    MultivaluedMap<String, String> httpHeaders, boolean check)
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

    private void fillParamList(Object param, List<Object> paramsList) {

	if (ObjectUtils.notNull(param)) {
	    paramsList.add(param);
	}
    }

    private List<Object> extractParams(ContainerRequestContext request)
	    throws IOException {

	List<Object> paramsList = new ArrayList<Object>();
	boolean check = check(request);
	boolean valid;
	MessageBodyReader<?> reader;
	MediaType mediaType;
	Object param;
	MultivaluedMap<String, String> httpHeaders = request.getHeaders();
	mediaType = getMediaType(request);
	MultivaluedMap<String, String> uriParams = extractParameters(request);
	InputStream entityStream;
	for (Parameter parameter : parameters) {

	    reader = getReader(parameter, mediaType);

	    entityStream = getEntityStream(check, request, parameter,
		    uriParams, mediaType);
	    valid = available(entityStream, reader, parameter, mediaType);
	    if (valid) {
		param = extractParam(reader, parameter, mediaType,
			entityStream, httpHeaders, check);

		fillParamList(param, paramsList);
	    }
	}

	return paramsList;
    }

    private Object[] getEmptyArray() {

	return new Object[PARAMS_DEF_LENGTH];
    }

    private Object[] getParameters(ContainerRequestContext request)
	    throws IOException {

	Object[] params;
	if (ObjectUtils.available(parameters)) {
	    List<Object> paramsList = extractParams(request);
	    params = paramsList.toArray();
	} else {
	    params = getEmptyArray();
	}

	return params;
    }

    @Override
    public Response apply(ContainerRequestContext data) {

	Response response;
	Object value = null;
	try {
	    EjbConnector connector = new EjbConnector();
	    InvocationHandler handler = connector.getHandler(metaData);
	    Object bean = connector.connectToBean(metaData);
	    Object[] params = getParameters(data);
	    value = handler.invoke(bean, method, params);
	    response = Response.ok(value).build();
	} catch (Throwable ex) {
	    LOG.error(ex.getMessage(), ex);
	    WebApplicationException webEx = new WebApplicationException(ex);
	    webEx.fillInStackTrace();
	    response = webEx.getResponse();
	}

	return response;
    }
}
