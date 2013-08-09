package org.lightmare.jpa.datasource;

/**
 * Configuration of jdbc driver
 * 
 * @author levan
 * 
 */
public class DriverConfig {

    private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    private static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver";

    private static final String H2_DRIVER = "org.h2.Driver";

    private static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final String ORACLE_NAME = "oracle";

    private static final String MYSQL_NAME = "mysql";

    private static final String DB2_NAME = "db2";

    private static final String MSSQL_NAME = "mssql";

    private static final String H2_NAME = "h2";

    private static final String DERBY_NAME = "derby";

    public static String getDriverName(String name) {
	if (ORACLE_NAME.equals(name)) {
	    return ORACLE_DRIVER;
	} else if (MYSQL_NAME.equals(name)) {
	    return MYSQL_DRIVER;
	} else if (DB2_NAME.equals(name)) {
	    return DB2_DRIVER;
	} else if (MSSQL_NAME.equals(name)) {
	    return MSSQL_DRIVER;
	} else if (H2_NAME.equals(name)) {
	    return H2_DRIVER;
	} else if (DERBY_NAME.equals(name)) {
	    return DERBY_DRIVER;
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
