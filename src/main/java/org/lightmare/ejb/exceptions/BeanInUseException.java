package org.lightmare.ejb.exceptions;

import java.io.IOException;

/**
 * {@link Exception} which is thrown at bean deploy time if bean already is
 * deployed
 * 
 * @author levan
 * 
 */
public class BeanInUseException extends IOException {

    /**
	 * 
	 */
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
}
