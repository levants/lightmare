package org.lightmare.ejb.interceptors;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.NoMoreTimeoutsException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

/**
 * Implementation of {@link Timer} of intercept timer task
 * 
 * @author levan
 * 
 */
public class TimerImpl implements Timer {

    @Override
    public void cancel() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {
	// TODO Auto-generated method stub

    }

    @Override
    public long getTimeRemaining() throws IllegalStateException,
	    NoSuchObjectLocalException, NoMoreTimeoutsException, EJBException {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public Date getNextTimeout() throws IllegalStateException,
	    NoSuchObjectLocalException, NoMoreTimeoutsException, EJBException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ScheduleExpression getSchedule() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean isPersistent() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean isCalendarTimer() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Serializable getInfo() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public TimerHandle getHandle() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {
	// TODO Auto-generated method stub
	return null;
    }

}
