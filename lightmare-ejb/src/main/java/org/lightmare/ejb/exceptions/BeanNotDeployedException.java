package org.lightmare.ejb.exceptions;

import java.io.IOException;

import org.lightmare.utils.logging.LogUtils;

public class BeanNotDeployedException extends IOException {

    private static final long serialVersionUID = 1L;

    // Error message for BeanInUseException class
    private static final String ERROR_MESSAGE_FORMAT = "Bean %s is not deployed";

    public BeanNotDeployedException() {
        super();
    }

    public BeanNotDeployedException(String message) {
        super(message);
    }

    public BeanNotDeployedException(Throwable thr) {
        super(thr);
    }

    public BeanNotDeployedException(String message, Throwable thr) {
        super(message, thr);
    }

    /**
     * Initializes {@link BeanInUseException} with passed error message format
     *
     * @param message
     * @param formats
     * @return {@link BeanInUseException}
     */
    public static BeanNotDeployedException get(String message, Object... formats) {

        BeanNotDeployedException exception;

        String errorMessage = LogUtils.logMessage(message, formats);
        exception = new BeanNotDeployedException(errorMessage);

        return exception;
    }

    /**
     * Initializes {@link BeanInUseException} with existing error message format
     * and passed bean name
     *
     * @param bean
     * @return {@link BeanInUseException}
     */
    public static BeanNotDeployedException get(Object bean) {
        return get(ERROR_MESSAGE_FORMAT, bean);
    }
}
