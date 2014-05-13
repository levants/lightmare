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

import java.io.IOException;

import org.lightmare.cache.MetaData;

/**
 * Factory class to initialize / clone {@link BeanHandler} instance
 * 
 * @author Levan Tsinadze
 * @since 0.0.66-SNAPSHOT
 */
public class BeanHandlerFactory {

    /**
     * Sets bean instance and calls {@link BeanHandler#configure()} method
     * 
     * @param handler
     * @param bean
     * @throws IOException
     */
    private static void configure(final BeanHandler handler, final Object bean)
	    throws IOException {

	handler.setBean(bean);
	handler.configure();
    }

    /**
     * Clones existing {@link BeanHandler} object
     * 
     * @param handler
     * @return {@link BeanHandler}
     * @throws IOException
     */
    private static BeanHandler cloneHandler(BeanHandler handler)
	    throws IOException {

	BeanHandler cloneHandler;

	try {
	    cloneHandler = (BeanHandler) handler.clone();
	} catch (CloneNotSupportedException ex) {
	    throw new IOException(ex);
	}

	return cloneHandler;
    }

    /**
     * Gets {@link BeanHandler} instance from {@link MetaData} or creates new
     * instance if it is null
     * 
     * @param metaData
     * @param bean
     * @return {@link BeanHandler}
     * @throws IOException
     */
    public static BeanHandler get(MetaData metaData, Object bean)
	    throws IOException {

	BeanHandler cloneHandler;

	BeanHandler handler = metaData.getHandler();
	if (handler == null) {
	    handler = new BeanHandler(metaData);
	    metaData.setHandler(handler);
	}

	cloneHandler = cloneHandler(handler);
	configure(cloneHandler, bean);

	return cloneHandler;
    }
}
