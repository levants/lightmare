package org.lightmare.utils;

import org.apache.log4j.Logger;

/**
 * Utility class for logging
 * 
 * @author levan
 * 
 */
public class LogUtils {

    public static String logMessage(String message, Object... formats) {

	String logMessage;
	if (ObjectUtils.available(formats)) {
	    logMessage = String.format(message, formats);
	} else {
	    logMessage = message;
	}

	return logMessage;
    }

    public static void fatal(Logger log, Throwable ex, String message,
	    Object... formats) {

	String logMessage = logMessage(message, formats);
	if (ex == null) {
	    log.fatal(logMessage);
	} else {
	    log.fatal(logMessage, ex);
	}
    }

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

    public static void fatal(Logger log, String message, Object... formats) {

	fatal(log, null, message, formats);
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

    public static void fatal(Logger log, String message, Object... formats) {

	fatal(log, null, message, formats);
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

    public static void fatal(Logger log, String message, Object... formats) {

	fatal(log, null, message, formats);
    }
}
