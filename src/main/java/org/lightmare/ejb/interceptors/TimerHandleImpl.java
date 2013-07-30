package org.lightmare.ejb.interceptors;

import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

/**
 * Implementation of {@link TimerHandle} interface
 * 
 * @author levan
 * 
 */
public class TimerHandleImpl implements TimerHandle {

    private static final long serialVersionUID = 1L;

    private Timer timer;

    public TimerHandleImpl(Timer timer) {

	this.timer = timer;
    }

    @Override
    public Timer getTimer() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return timer;
    }

}
