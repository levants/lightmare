/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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

import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

/**
 * Implementation of {@link TimerHandle} interface
 *
 * @author Levan Tsinadze
 * @since 0.0.65-SNAPSHOT
 */
public class TimerHandleImpl implements TimerHandle {

    private static final long serialVersionUID = 1L;

    // Appropriated Timer instance
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
    public Timer getTimer() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
	return timer;
    }
}
