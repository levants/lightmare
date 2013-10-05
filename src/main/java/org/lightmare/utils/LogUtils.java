package org.lightmare.utils;

import org.apache.log4j.Logger;

/**
 * Utility class for logging
 * 
 * @author levan
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

    public static void error(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);
	if (ex == null) {
	    log.error(logMessage);
	} else {
	    log.error(logMessage, ex);
	}
    }

    public static void error(Logger log, String message, Object... formats) {

	error(log, null, message, formats);
    }

    public static void debug(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);
	if (ex == null) {
	    log.debug(logMessage);
	} else {
	    log.debug(logMessage, ex);
	}
    }

    public static void debug(Logger log, String message, Object... formats) {

	debug(log, null, message, formats);
    }

    public static void info(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);
	if (ex == null) {
	    log.info(logMessage);
	} else {
	    log.info(logMessage, ex);
	}
    }

    public static void info(Logger log, String message, Object... formats) {

	info(log, null, message, formats);
    }
}
