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

import org.lightmare.remote.rpc.RPCall;

/**
 * Factory class to initialize {@link BeanLocalHandler} class instance
 * 
 * @author Levan Tsinadze
 * @since 0.0.66-SNAPSHOT
 */
public class BeanLocalHandlerFactory {

    /**
     * Initializes {@link RPCall} class instance by passed RPC arguments
     * 
     * @param rpcArgs
     * @return {@link RPCall}
     */
    private static RPCall createRPCall(Object... rpcArgs) {

	RPCall call;

	String host = (String) rpcArgs[0];
	int port = (Integer) rpcArgs[1];
	call = new RPCall(host, port);

	return call;
    }

    /**
     * Creates {@link BeanLocalHandler} instance with host and port arguments
     * 
     * @param rpcArgs
     * @return {@link BeanLocalHandler}
     */
    public static BeanLocalHandler get(Object... rpcArgs) {

	BeanLocalHandler handler;

	RPCall call = createRPCall(rpcArgs);
	handler = new BeanLocalHandler(call);

	return handler;
    }
}
