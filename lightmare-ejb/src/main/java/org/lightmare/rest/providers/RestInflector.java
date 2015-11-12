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
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Implementation of framework defined {@link Inflector} interface for EJB beans
 * appropriate {@link Method} invocation by REST service
 * 
 * @author Levan Tsinadze
 * @since 0.0.50-SNAPSHOT
 * @see Inflector
 */
public class RestInflector implements Inflector<ContainerRequestContext, Response> {

    // Appropriated java Method for REST service
    private Method method;

    // Instance of MetaData to call EJB bean method
    private MetaData metaData;

    // Media type for REST service
    private MediaType type;

    // Parameters of REST service to be translated java Method parameters
    private List<Parameter> parameters;

    // Message body decoder provided and injected by framework
    @Context
    private MessageBodyWorkers workers;

    private static final Logger LOG = Logger.getLogger(RestInflector.class);

    /**
     * Constructor with {@link Method}, {@link MetaData}, {@link MediaType} and
     * {@link List} of {@link Parameter}s for concret REST method call
     * 
     * @param method
     * @param metaData
     * @param type
     * @param parameters
     */
    public RestInflector(Method method, MetaData metaData, MediaType type, List<Parameter> parameters) {
        this.method = method;
        this.metaData = metaData;
        this.type = type;
        this.parameters = parameters;
    }

    /**
     * Gets appropriated bean {@link Class} instance
     * 
     * @return {@link Class} for bean
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

    /**
     * Gets {@link MediaType} for {@link ContainerRequestContext} instance
     * 
     * @param request
     * @return {@link MediaType}
     */
    private MediaType getMediaType(ContainerRequestContext request) {

        MediaType mediaType = request.getMediaType();

        if (mediaType == null && type == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        } else if (mediaType == null && ObjectUtils.notNull(type)) {
            mediaType = type;
        }

        return mediaType;
    }

    /**
     * Extracts parameters as array of {@link Object}s from
     * {@link ContainerRequestContext} to call associated java method
     * 
     * @param request
     * @return {@link Object}[]
     * @throws IOException
     */
    private Object[] extraxtParameters(ContainerRequestContext request) throws IOException {

        Object[] params;

        MediaType mediaType = getMediaType(request);
        ParamBuilder builder = new ParamBuilder.Builder().setMediaType(mediaType).setParameters(parameters)
                .setWorkers(workers).setRequest(request).build();
        List<Object> paramsList = builder.extractParams();
        params = paramsList.toArray();

        return params;
    }

    /**
     * Gets parameters as array of {@link Object} from passed
     * {@link ContainerRequestContext} to call real java method
     * 
     * @param request
     * @return {@link Object}[]
     * @throws IOException
     */
    private Object[] getParameters(ContainerRequestContext request) throws IOException {

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
