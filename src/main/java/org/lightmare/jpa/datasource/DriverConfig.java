package org.lightmare.jpa.datasource;

/**
 * Configuration of jdbc driver classes and database names
 * 
 * @author levan
 * 
 */
public class DriverConfig {

    /**
     * Caches database driver names and classes
     * 
     * @author levan
     * 
     */
    public static enum Drivers {

	ORACLE("oracle", "oracle.jdbc.OracleDriver"), // Oracle
	MYSQL("mysql", "com.mysql.jdbc.Driver"), // MYSQL
	MSSQL("mssql", "com.microsoft.sqlserver.jdbc.SQLServerDriver"), // MSSQL
	DB2("db2", "com.ibm.db2.jcc.DB2Driver"), // DB2
	H2("h2", "org.h2.Driver"), // H2
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
     * Resolves appropriate jdbc driver class name by database name
     * 
     * @param name
     * @return {@link String}
     */
    public static String getDriverName(String name) {

	if (Drivers.ORACLE.name.equals(name)) {
	    return Drivers.ORACLE.driver;
	} else if (Drivers.MYSQL.name.equals(name)) {
	    return Drivers.MYSQL.driver;
	} else if (Drivers.DB2.name.equals(name)) {
	    return Drivers.DB2.driver;
	} else if (Drivers.MSSQL.name.equals(name)) {
	    return Drivers.MSSQL.driver;
	} else if (Drivers.H2.name.equals(name)) {
	    return Drivers.H2.driver;
	} else if (Drivers.DERBY.name.equals(name)) {
	    return Drivers.DERBY.driver;
	} else {
	    return null;
	}
    }

    public static boolean isOracle(String name) {

	return ORACLE_DRIVER.equals(name);
    }

    public static boolean isMySQL(String name) {

	return MYSQL_DRIVER.equals(name);
    }

    public static boolean isDB2(String name) {

	return DB2_DRIVER.equals(name);
    }

    public static boolean isMsSQL(String name) {

	return MSSQL_DRIVER.equals(name);
    }

    public static boolean isH2(String name) {

	return H2_DRIVER.equals(name);
    }

    public static boolean isDerby(String name) {

	return DERBY_DRIVER.equals(name);
    }
}
