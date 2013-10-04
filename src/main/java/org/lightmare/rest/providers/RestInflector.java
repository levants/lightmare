package org.lightmare.rest.providers;

import java.io.IOException;
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
import org.lightmare.ejb.handlers.RestHandler;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of {@link Inflector} for EJB beans appropriate {@link Method}
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

    /**
     * Gets appropriated bean {@link Class} instance
     * 
     * @return
     */
    public Class<?> getBeanClass() {

	Class<?> beanClass;
	if (metaData == null) {
	    beanClass = null;
	} else {
	    beanClass = metaData.getBeanClass();
	}

	return beanClass;
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

	Object[] params;

	MediaType mediaType = getMediaType(request);
	ParamBuilder builder = new ParamBuilder.Builder()
		.setMediaType(mediaType).setParameters(parameters)
		.setWorkers(workers).setRequest(request).build();
	List<Object> paramsList = builder.extractParams();
	params = paramsList.toArray();

	return params;
    }

    private Object[] getParameters(ContainerRequestContext request)
	    throws IOException {

	Object[] params;

	if (CollectionUtils.valid(parameters)) {
	    params = extraxtParameters(request);
	} else {
	    params = CollectionUtils.EMPTY_ARRAY;
	}

	return params;
    }

    @Override
    public Response apply(ContainerRequestContext data) {

	Response response;

	try {
	    EjbConnector connector = new EjbConnector();
	    RestHandler<?> handler = connector.createRestHandler(metaData);
	    Object[] params = getParameters(data);
	    Object value = handler.invoke(method, params);
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
