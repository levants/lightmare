package org.lightmare.ejb.exceptions;

import java.io.IOException;

import org.lightmare.utils.LogUtils;

/**
 * Implementation of {@link Exception} class which is thrown at bean deploy time
 * if bean already is deployed
 * 
 * @author levan
 * @since 0.0.16-SNAPSHOT
 */
public class BeanInUseException extends IOException {

    private static final long serialVersionUID = 1L;

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

    public static BeanInUseException get(String message, Object... formats) {

	String errorMessage = LogUtils.logMessage(message, formats);

	return new BeanInUseException(errorMessage);
    }
}
