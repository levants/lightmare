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
package org.lightmare.jpa.datasource;

import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Configuration of JDBC driver classes and database names
 * 
 * @author Levan Tsinadze
 * @since 0.0.15-SNAPSHOT
 */
public abstract class DriverConfig {

    /**
     * Caches database driver names and classes
     * 
     * @author Levan Tsinadze
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
	DERBY("derby", "org.apache.derby.jdbc.EmbeddedDriver"), // DERBY
	HYPERSONIC("hypersonic", "org.hsql.jdbcDriver"), // Hypersonic
	SYBASE("sybase", "ncom.sybase.jdbc2.jdbc.SybDriver"), // Sybase
	INTERBASE("interbase", "interbase.interclient.Driver"); // Interbase

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
