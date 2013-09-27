package org.lightmare.jpa.datasource.c3p0;

import java.sql.Connection;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;

public class OracleConnectionCustomizer extends AbstractConnectionCustomizer {

    @Override
    public void onAcquire(Connection connection, String tocken)
	    throws Exception {

	System.out.format("onAcquire %s\n", tocken);

	super.onAcquire(connection, tocken);
    }

    @Override
    public void onDestroy(Connection connection, String tocken)
	    throws Exception {

	System.out.format("onDestroy %s\n", tocken);

	super.onDestroy(connection, tocken);
    }

    @Override
    public void onCheckOut(Connection connection, String tocken)
	    throws Exception {

	System.out.format("onCheckOut %s\n", tocken);

	super.onCheckOut(connection, tocken);
    }

    @Override
    public void onCheckIn(Connection connection, String tocken)
	    throws Exception {

	System.out.format("onCheckIn %s\n", tocken);

	super.onCheckIn(connection, tocken);
    }
}
