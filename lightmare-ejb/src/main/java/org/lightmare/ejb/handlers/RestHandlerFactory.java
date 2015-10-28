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
package org.lightmare.ejb.handlers;

/**
 * Factory class to create a {@link RestHandler} instance for REST services
 *
 * @author Levan Tsinadze
 * @since 0.0.81
 */
public class RestHandlerFactory {

    /**
     * Creates {@link RestHandler} instance
     *
     * @param handler
     * @param bean
     * @return {@link RestHandler}
     */
    public static <T> RestHandler<T> get(BeanHandler handler, T bean) {
	RestHandler<T> restHandler = new RestHandler<T>(handler, bean);
	return restHandler;
    }
}
