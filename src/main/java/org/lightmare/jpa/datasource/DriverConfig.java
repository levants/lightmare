package org.lightmare.jpa.datasource;

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

	String driverName;

	Drivers[] drivers = Drivers.values();
	boolean match = Boolean.FALSE;
	for (int i = 0; i < drivers.length && ObjectUtils.notTrue(match); i++) {
	    match = drivers[i].name.equals(name);
	    if (match) {
		driverName = drivers[i].driver;
	    }
	}

	if (ObjectUtils.notTrue(match)) {
	    driverName = null;
	}

	if (Drivers.ORACLE.name.equals(name)) {
	    driverName = Drivers.ORACLE.driver;
	} else if (Drivers.MYSQL.name.equals(name)) {
	    driverName = Drivers.MYSQL.driver;
	} else if (Drivers.POSTGRE.name.equals(name)) {
	    driverName = Drivers.POSTGRE.driver;
	} else if (Drivers.DB2.name.equals(name)) {
	    driverName = Drivers.DB2.driver;
	} else if (Drivers.MSSQL.name.equals(name)) {
	    driverName = Drivers.MSSQL.driver;
	} else if (Drivers.H2.name.equals(name)) {
	    driverName = Drivers.H2.driver;
	} else if (Drivers.HYPERSONIC.name.equals(name)) {
	    driverName = Drivers.HYPERSONIC.driver;
	} else if (Drivers.DERBY.name.equals(name)) {
	    driverName = Drivers.DERBY.driver;
	} else {
	    driverName = null;
	}

	return driverName;
    }

    public static boolean isOracle(String name) {

	return Drivers.ORACLE.driver.equals(name);
    }

    public static boolean isMySQL(String name) {

	return Drivers.MYSQL.driver.equals(name);
    }

    public static boolean isPostgre(String name) {

	return Drivers.POSTGRE.driver.equals(name);
    }

    public static boolean isDB2(String name) {

	return Drivers.DB2.driver.equals(name);
    }

    public static boolean isMsSQL(String name) {

	return Drivers.MSSQL.driver.equals(name);
    }

    public static boolean isH2(String name) {

	return Drivers.H2.driver.equals(name);
    }

    public static boolean isDerby(String name) {

	return Drivers.DERBY.driver.equals(name);
    }
}
