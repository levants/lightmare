package org.lightmare.rest.providers;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Parameter;
import org.lightmare.cache.MetaData;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.rest.utils.ParamBuilder;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of {@link Inflector} for ejb beans appropriate {@link Method}
 * invocation for REST service
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

    private Object[] extraxtParameters(ContainerRequestContext request)
	    throws IOException {

	MediaType mediaType = getMediaType(request);
	ParamBuilder builder = new ParamBuilder.Builder()
		.setMediaType(mediaType).setParameters(parameters)
		.setWorkers(workers).setRequest(request).build();
	List<Object> paramsList = builder.extractParams();
	Object[] params = paramsList.toArray();

	return params;
    }

    private Object[] getParameters(ContainerRequestContext request)
	    throws IOException {

	Object[] params;
	if (ObjectUtils.available(parameters)) {
	    params = extraxtParameters(request);
	} else {
	    params = ObjectUtils.getEmptyArray();
	}

	return params;
    }

    @Override
    public Response apply(ContainerRequestContext data) {

	Response response;
	try {
	    EjbConnector connector = new EjbConnector();
	    InvocationHandler handler = connector.getHandler(metaData);
	    Object bean = connector.connectToBean(metaData);
	    Object[] params = getParameters(data);
	    Object value = handler.invoke(bean, method, params);
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
