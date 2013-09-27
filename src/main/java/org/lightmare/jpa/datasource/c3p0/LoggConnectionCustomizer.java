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
 * @author levan
 * 
 */
public class LoggConnectionCustomizer extends AbstractConnectionCustomizer {

    // Keeps count of active connections
    private static final AtomicInteger ACTIVE_CONNECTIONS = new AtomicInteger();

    // Keeps count of connections in pool
    private static final AtomicInteger POOLED_CONNECTIONS = new AtomicInteger();

    private static final Logger LOG = Logger
	    .getLogger(LoggConnectionCustomizer.class);

    public static void setMaxPoolSize(int maxPoolSize) {
	POOLED_CONNECTIONS.set(maxPoolSize);
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
