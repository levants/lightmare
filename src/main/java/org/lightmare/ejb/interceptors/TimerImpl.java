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
 * @since 0.0.65-SNAPSHOT
 */
// TODO: Need proper implementation of Timer interface
public class TimerImpl implements Timer {

    private TimerHandle handle;

    private ScheduleExpression schedule;

    private Date nextTimeout;

    private boolean persistent;

    private boolean calendarTimer;

    private Serializable info;

    private TimerImpl() {
	handle = new TimerHandleImpl(this);
    }

    @Override
    public void cancel() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

    }

    @Override
    public long getTimeRemaining() throws IllegalStateException,
	    NoSuchObjectLocalException, NoMoreTimeoutsException, EJBException {
	return 0;
    }

    @Override
    public Date getNextTimeout() throws IllegalStateException,
	    NoSuchObjectLocalException, NoMoreTimeoutsException, EJBException {
	return nextTimeout;
    }

    @Override
    public ScheduleExpression getSchedule() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return schedule;
    }

    @Override
    public boolean isPersistent() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return persistent;
    }

    @Override
    public boolean isCalendarTimer() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return calendarTimer;
    }

    @Override
    public Serializable getInfo() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return info;
    }

    @Override
    public TimerHandle getHandle() throws IllegalStateException,
	    NoSuchObjectLocalException, EJBException {

	return handle;
    }
}
