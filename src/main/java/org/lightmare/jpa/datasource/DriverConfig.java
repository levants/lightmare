package org.lightmare.jpa.datasource;

import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Configuration of JDBC driver classes and database names
 * 
 * @author levan
 * @since 0.0.15-SNAPSHOT
 */
public abstract class DriverConfig {

    /**
     * Caches database driver names and classes
     * 
     * @author levan
     * @since 0.0.81-SNAPSHOT
     */
    public static enum Drivers {

	// Database names and associated JDBC driver class names
	ORACLE("oracle", "oracle.jdbc.OracleDriver"), // Oracle
	MYSQL("mysql", "com.mysql.jdbc.Driver"), // MYSQL
	POSTGRE("postgre", "org.postgresql.Driver"), // PostgreSQL
	MSSQL("mssql", "com.microsoft.sqlserver.jdbc.SQLServerDriver"), // MSSQL
	DB2("db2", "com.ibm.db2.jcc.DB2Driver"), // DB2
	H2("h2", "org.h2.Driver"), // H2
	HYPERSONIC("hypersonic", "org.hsql.jdbcDriver"), // Hypersonic
	SYBASE("sybase", "ncom.sybase.jdbc2.jdbc.SybDriver"), // Sybase
	INTERBASE("interbase", "interbase.interclient.Driver"), // Interbase
	DERBY("derby", "org.apache.derby.jdbc.EmbeddedDriver"); // DERBY

	// Database names
	public String name;

	// Driver class names
	public String driver;

	private Drivers(String name, String driver) {
	    this.name = name;
	    this.driver = driver;
	}
    }

    /**
     * Resolves appropriate JDBC driver class name by database name
     * 
     * @param name
     * @return {@link String}
     */
    public static String getDriverName(String name) {

	String driverName = null;

	Drivers[] drivers = Drivers.values();

	Drivers driver;
	int length = drivers.length;
	boolean match = Boolean.FALSE;

	for (int i = CollectionUtils.FIRST_INDEX; i < length
		&& ObjectUtils.notTrue(match); i++) {

	    driver = drivers[i];
	    match = driver.name.equals(name);

	    if (match) {
		driverName = driver.driver;
	    }
	}

	return driverName;
    }
}
