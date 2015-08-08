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
package org.lightmare.ejb.exceptions;

import java.io.IOException;

import org.lightmare.utils.logging.LogUtils;

/**
 * Extension of {@link Exception} class which is thrown at bean deploy time if
 * bean already is deployed
 *
 * @author Levan Tsinadze
 * @since 0.0.16
 */
public class BeanInUseException extends IOException {

    private static final long serialVersionUID = 1L;

    // Error message for BeanInUseException class
    private static final String ERROR_MESSAGE_FORMAT = "bean %s is alredy in use";

    public BeanInUseException() {
	super();
    }

    public BeanInUseException(String message) {
	super(message);
    }

    public BeanInUseException(Throwable thr) {
	super(thr);
    }

    public BeanInUseException(String message, Throwable thr) {
	super(message, thr);
    }

    /**
     * Initializes {@link BeanInUseException} with passed error message format
     *
     * @param message
     * @param formats
     * @return {@link BeanInUseException}
     */
    public static BeanInUseException get(String message, Object... formats) {

	String errorMessage = LogUtils.logMessage(message, formats);

	return new BeanInUseException(errorMessage);
    }

    /**
     * Initializes {@link BeanInUseException} with existing error message format
     * and passed bean name
     *
     * @param bean
     * @return {@link BeanInUseException}
     */
    public static BeanInUseException get(Object bean) {
	return get(ERROR_MESSAGE_FORMAT, bean);
    }
}
