package ge.gov.mia.lightmare.jpa.datasource;

public class DriverConfig {

	private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

	private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

	private static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static final String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver";

	private static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

	public static String getDriverName(String name) {
		if ("oracle".equals(name)) {
			return ORACLE_DRIVER;
		} else if ("mysql".equals(name)) {
			return MYSQL_DRIVER;
		} else if ("db2".equals(name)) {
			return DB2_DRIVER;
		} else if ("mssql".equals(name)) {
			return MSSQL_DRIVER;
		} else if ("derby".equals(name)) {
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

	public static boolean isDerby(String name) {
		return DERBY_DRIVER.equals(name);
	}
}
