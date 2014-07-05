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
package org.lightmare.utils;

import org.apache.log4j.Logger;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class for logging
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public class LogUtils {

    /**
     * Generates logging messages
     * 
     * @param message
     * @param formats
     * @return {@link String}
     */
    public static String logMessage(String message, Object... formats) {

	String logMessage;

	if (CollectionUtils.valid(formats)) {
	    logMessage = String.format(message, formats);
	} else {
	    logMessage = message;
	}

	return logMessage;
    }

    /**
     * Generated fatal log
     * 
     * @param log
     * @param ex
     * @param message
     * @param formats
     */
    public static void fatal(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);

	if (ex == null) {
	    log.fatal(logMessage);
	} else {
	    log.fatal(logMessage, ex);
	}
    }

    /**
     * Generates fatal logs
     * 
     * @param log
     * @param message
     * @param formats
     */
    public static void fatal(Logger log, String message, Object... formats) {
	fatal(log, null, message, formats);
    }

    /**
     * Generates error log
     * 
     * @param log
     * @param ex
     * @param message
     * @param formats
     */
    public static void error(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);

	if (ex == null) {
	    log.error(logMessage);
	} else {
	    log.error(logMessage, ex);
	}
    }

    /**
     * Generates error logs
     * 
     * @param log
     * @param message
     * @param formats
     */
    public static void error(Logger log, String message, Object... formats) {
	error(log, null, message, formats);
    }

    /**
     * Generates debug logs
     * 
     * @param log
     * @param ex
     * @param message
     * @param formats
     */
    public static void debug(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);

	if (ex == null) {
	    log.debug(logMessage);
	} else {
	    log.debug(logMessage, ex);
	}
    }

    /**
     * Generates debug logs
     * 
     * @param log
     * @param message
     * @param formats
     */
    public static void debug(Logger log, String message, Object... formats) {
	debug(log, null, message, formats);
    }

    /**
     * Generates info logs
     * 
     * @param log
     * @param ex
     * @param message
     * @param formats
     */
    public static void info(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);

	if (ex == null) {
	    log.info(logMessage);
	} else {
	    log.info(logMessage, ex);
	}
    }

    /**
     * Generates info logs
     * 
     * @param log
     * @param message
     * @param formats
     */
    public static void info(Logger log, String message, Object... formats) {
	info(log, null, message, formats);
    }
}
