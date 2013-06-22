package org.lightmare.rest.providers;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.process.Inflector;
import org.lightmare.cache.MetaData;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.utils.reflect.MetaUtils;

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

    public RestInflector(Method method, MetaData metaData, MediaType type) {

	this.method = method;
	this.metaData = metaData;
	this.type = type;
    }

    @Override
    public Response apply(ContainerRequestContext data) {

	ResponseBuilder responseBuilder;
	Object value = null;
	try {
	    Object bean = new EjbConnector().connectToBean(metaData);
	    value = MetaUtils.invoke(method, bean);

	    if (type == null) {
		responseBuilder = Response.ok(value);
	    } else {
		responseBuilder = Response.ok(value, type);
	    }

	} catch (IOException ex) {
	    LOG.error(ex.getMessage(), ex);
	    responseBuilder = Response.status(new ErrorStatusType(ex
		    .getMessage()));
	}

	return responseBuilder.build();
    }
}
