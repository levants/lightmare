/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
 * @author Levan Tsinadze
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
