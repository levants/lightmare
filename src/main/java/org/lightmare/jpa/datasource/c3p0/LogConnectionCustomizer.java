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
package org.lightmare.jpa.datasource.c3p0;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;

/**
 * Logger class for customize pooled connections
 * 
 * @author Levan Tsinadze
 * @since 0.0.82-SNAPSHOT
 */
public class LogConnectionCustomizer extends AbstractConnectionCustomizer {

    // Keeps count of active connections
    private static final AtomicInteger ACTIVE_CONNECTIONS = new AtomicInteger();

    // Keeps count of connections in pool
    private static final AtomicInteger POOLED_CONNECTIONS = new AtomicInteger();

    private static final Logger LOG = Logger
	    .getLogger(LogConnectionCustomizer.class);

    /**
     * Sets initial size of pool for pooled connection counter after
     * instantiation
     * 
     * @param initialPoolSize
     */
    public static void initialPoolSize(int initialPoolSize) {
	POOLED_CONNECTIONS.set(initialPoolSize);
    }

    @Override
    public void onAcquire(Connection connection, String tocken)
	    throws Exception {

	int aquired = ACTIVE_CONNECTIONS.incrementAndGet();
	LOG.info(StringUtils.concat("Active connections are ", aquired));

	super.onAcquire(connection, tocken);
    }

    @Override
    public void onDestroy(Connection connection, String tocken)
	    throws Exception {

	if (ObjectUtils.notNull(connection)
		&& ObjectUtils.notTrue(connection.isClosed())) {
	    connection.close();
	}

	int aquired = ACTIVE_CONNECTIONS.decrementAndGet();
	LOG.info(StringUtils.concat("Active connections are ", aquired));

	super.onDestroy(connection, tocken);
    }

    @Override
    public void onCheckOut(Connection connection, String tocken)
	    throws Exception {

	int pooled = POOLED_CONNECTIONS.decrementAndGet();
	LOG.info(StringUtils.concat("Pooled connections are ", pooled));

	super.onCheckOut(connection, tocken);
    }

    @Override
    public void onCheckIn(Connection connection, String tocken)
	    throws Exception {

	int pooled = POOLED_CONNECTIONS.incrementAndGet();
	LOG.info(StringUtils.concat("Pooled connections are ", pooled));

	super.onCheckIn(connection, tocken);
    }
}
