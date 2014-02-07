/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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

import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.lightmare.cache.RestContainer;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.ObjectUtils;

/**
 * Class to check if {@link Class} is annotated for jax.rs appropriated REST
 * annotations and valid to create
 * {@link org.glassfish.jersey.server.model.Resource} classes
 * 
 * @author Levan Tsinadze
 * @since 0.0.74-SNAPSHOT
 */
public class RestCheck {

    /**
     * Reloads REST service
     */
    public static void reload() {

	if (RestContainer.hasRest()) {
	    RestProvider.reload();
	}
    }

    /**
     * Checks annotations on {@link Class} and its {@link Method}s for REST
     * resources
     * 
     * @param method
     * @return <code>boolean</code>
     */
    private static boolean checkAnnotation(Method method) {

	boolean valid = (method.isAnnotationPresent(GET.class)
		|| method.isAnnotationPresent(POST.class)
		|| method.isAnnotationPresent(PUT.class) || method
		.isAnnotationPresent(DELETE.class));

	return valid;
    }

    /**
     * Checks if passed {@link Class} is available to create
     * {@link org.glassfish.jersey.server.model.Resource} instance
     * 
     * @param resourceClass
     * @return <code>boolean</code>
     */
    public static boolean check(Class<?> resourceClass) {

	boolean valid = ObjectUtils.notNull(resourceClass)
		&& resourceClass.isAnnotationPresent(Path.class);

	Method[] methods = resourceClass.getDeclaredMethods();
	int length = methods.length;
	Method method;
	for (int i = 0; i < length && ObjectUtils.notTrue(valid); i++) {
	    method = methods[i];
	    valid = checkAnnotation(method);
	}

	return valid;
    }
}
