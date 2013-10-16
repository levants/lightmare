package org.lightmare.ejb.exceptions;

import java.io.IOException;

import org.lightmare.utils.LogUtils;

/**
 * Extension of {@link Exception} class which is thrown at bean deploy time if
 * bean already is deployed
 * 
 * @author levan
 * @since 0.0.16-SNAPSHOT
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

    /**Initializes {@link BeanInUseException} with existing error message format and passed bean name
     * @param bean
     * @return {@link BeanInUseException}
     */
    public static BeanInUseException get(Object bean) {

	return get(ERROR_MESSAGE_FORMAT, bean);
    }
}
