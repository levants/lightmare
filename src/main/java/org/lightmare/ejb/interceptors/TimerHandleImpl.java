package org.lightmare.ejb.interceptors;

import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

/**
 * Implementation of {@link TimerHandle} interface
 * 
 * @author levan
 * @since 0.0.65-SNAPSHOT
 */
public class TimerHandleImpl implements TimerHandle {

    private static final long serialVersionUID = 1L;

    //Appropriated Timer instance
    private Timer timer;

    /**
     * Constructor with {@link Timer} instance
     * 
     * @param timer
     */
    public TimerHandleImpl(Timer timer) {

	this.timer = timer;
    }

    @Override
    public Timer getTimer() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return timer;
    }

}
