package org.lightmare.rest.providers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.log4j.Logger;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ContainerRequest;
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

    @Context
    private MessageBodyWorkers workers;

    /**
     * Error status for exception handling
     * 
     * @author Levan
     * 
     */
    protected static class ErrorStatusType implements StatusType {

	private String message;

	public ErrorStatusType(String message) {
	    this.message = message;
	}

	public int getStatusCode() {
	    return Status.ACCEPTED.getStatusCode();
	}

	@Override
	public String getReasonPhrase() {
	    return message;
	}

	@Override
	public Family getFamily() {
	    return Family.SERVER_ERROR;
	}
    }

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object[] getParameters(ContainerRequestContext data)
	    throws IOException {

	Object[] params;
	ContainerRequest request = (ContainerRequest) data;
	if (ObjectUtils.available(parameters)) {
	    List<Object> paramsList = new ArrayList<Object>();
	    MessageBodyReader<?> reader;
	    Class<?> rawType;
	    Type type;
	    Annotation[] annotations;
	    MediaType mediaType;
	    Object param;
	    MultivaluedMap<String, String> httpHeaders = request.getHeaders();
	    mediaType = getMediaType(request);
	    for (Parameter parameter : parameters) {
		type = parameter.getType();
		rawType = parameter.getRawType();
		annotations = parameter.getAnnotations();
		reader = workers.getMessageBodyReader(rawType, type,
			annotations, mediaType);
		if (ObjectUtils.notNull(reader)
			&& reader.isReadable(rawType, type, annotations,
				mediaType)) {
		    param = reader.readFrom((Class) rawType, type, annotations,
			    mediaType, httpHeaders, request.getEntityStream());

		    if (ObjectUtils.notNull(param)) {
			paramsList.add(param);
		    }
		}
	    }
	    params = paramsList.toArray();
	} else {
	    params = new Object[PARAMS_DEF_LENGTH];
	}

	return params;
    }

    @Override
    public Response apply(ContainerRequestContext data) {

	ResponseBuilder responseBuilder;
	Object value = null;
	try {
	    EjbConnector connector = new EjbConnector();
	    InvocationHandler handler = connector.getHandler(metaData);
	    Object bean = connector.connectToBean(metaData);
	    Object[] params = getParameters(data);

	    value = handler.invoke(bean, method, params);
	    responseBuilder = Response.ok(value);
	} catch (Throwable ex) {
	    LOG.error(ex.getMessage(), ex);
	    responseBuilder = Response.status(
		    new ErrorStatusType(ex.getMessage())).entity(ex.toString());
	}

	return responseBuilder.build();
    }
}
