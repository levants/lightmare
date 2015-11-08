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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.Context;
import javax.sql.DataSource;

import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Parses XML and property files to initialize and cache {@link DataSource}
 * objects
 *
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
 */
public abstract class Initializer {

    // Caches already initialized data source file paths
    private static final Set<String> INITIALIZED_SOURCES = Collections.synchronizedSet(new HashSet<String>());

    // Caches already initialized data source JNDI names
    private static final Set<String> INITIALIZED_NAMES = Collections.synchronizedSet(new HashSet<String>());

    // Locks data base driver initialization to avoid thread blocking
    private static final Lock DRIVER_LOCK = new ReentrantLock();

    /**
     * Container for connection configuration properties
     *
     * @author Levan Tsinadze
     * @since 0.0.80-SNAPSHOT
     */
    public static enum ConnectionConfig {

	DRIVER_PROPERTY("driver"), // driver
	USER_PROPERTY("user"), // user
	PASSWORD_PROPERTY("password"), // password
	URL_PROPERTY("url"), // URL
	JNDI_NAME_PROPERTY("jndiname"), // JNDI name
	NAME_PROPERTY("name");// data source name

	public String name;

	private ConnectionConfig(String name) {
	    this.name = name;
	}
    }

    public static void setDsAsInitialized(String datasourcePath) {
	INITIALIZED_SOURCES.add(datasourcePath);
    }

    public static void removeInitialized(String datasourcePath) {
	INITIALIZED_SOURCES.remove(datasourcePath);
    }

    public static boolean checkDSPath(String datasourcePath) {
	return INITIALIZED_SOURCES.contains(datasourcePath);
    }

    private static boolean validate(String datasourcePath) {
	return StringUtils.valid(datasourcePath) && Boolean.FALSE.equals(checkDSPath(datasourcePath));
    }

    /**
     * Gets JNDI name from passed data source configuration {@link Properties}
     * instance
     *
     * @param properties
     * @return {@link String}
     */
    public static String getJndiName(Properties properties) {
	String jndiName = properties.getProperty(ConnectionConfig.JNDI_NAME_PROPERTY.name);
	return jndiName;
    }

    /**
     * Loads JDBC driver class
     *
     * @param driver
     */
    public static void initializeDriver(String driver) throws IOException {

	boolean locked = Boolean.FALSE;
	while (Boolean.FALSE.equals(locked)) {
	    locked = ObjectUtils.tryLock(DRIVER_LOCK);
	    if (locked) {
		try {
		    ClassUtils.initClassForName(driver);
		} finally {
		    ObjectUtils.unlock(DRIVER_LOCK);
		}
	    }
	}
    }

    /**
     * Initializes and creates {@link Collection} of data sources
     *
     * @param paths
     * @return {@link Collection} of data sources
     */
    private static Collection<String> initializeDataSources(Collection<String> paths) {

	Collection<String> values = new HashSet<String>();

	if (CollectionUtils.valid(paths)) {
	    for (String value : paths) {
		if (validate(value)) {
		    values.add(value);
		}
	    }
	}

	return values;
    }

    /**
     * Initializes data sources from passed {@link Configuration} instance
     *
     * @throws IOException
     */
    public static void initializeDataSources(Configuration config) throws IOException {

	Collection<String> paths = config.getDataSourcePath();
	Collection<String> values = initializeDataSources(paths);
	FileParsers.parseDataSources(values, config);
    }

    /**
     * Initializes and registers {@link DataSource} object in JNDI by
     * {@link Properties} {@link Context}
     *
     * @param properties
     * @throws IOException
     */
    public static void registerDataSource(Properties properties) throws IOException {

	InitDataSource initDataSource = InitDataSourceFactory.get(properties);
	initDataSource.create();
	// Caches jndiName for data source
	String jndiName = getJndiName(properties);
	INITIALIZED_NAMES.add(jndiName);
    }

    /**
     * Checks and clears data source
     *
     * @param dataSource
     */
    private static void checkAndClear(DataSource dataSource) {

	if (ObjectUtils.notNull(dataSource)) {
	    cleanUp(dataSource);
	}
    }

    /**
     * Closes and removes from {@link Context} data source with specified JNDI
     * name
     *
     * @param jndiName
     * @throws IOException
     */
    public static void close(String jndiName) throws IOException {

	DataSource dataSource = JndiManager.lookup(jndiName);
	checkAndClear(dataSource);
	dataSource = null;
	JndiManager.unbind(jndiName);
	INITIALIZED_NAMES.remove(jndiName);
    }

    /**
     * Closes and removes from {@link Context} all initialized and cached data
     * sources
     *
     * @throws IOException
     */
    public static void closeAll() throws IOException {

	Set<String> dataSources = new HashSet<String>(INITIALIZED_NAMES);
	for (String jndiName : dataSources) {
	    close(jndiName);
	}
    }

    /**
     * Checks and removes data sources from {@link Context} by passed file path
     *
     * @param dataSourcePath
     * @param jndiNames
     * @throws IOException
     */
    private static void checkAndUndeploy(String dataSourcePath, Collection<String> jndiNames) throws IOException {

	if (StringUtils.valid(dataSourcePath)) {
	    for (String jndiName : jndiNames) {
		close(jndiName);
	    }
	}
    }

    /**
     * Closes and removes from {@link Context} all data sources from passed file
     * path
     *
     * @param dataSourcePath
     * @throws IOException
     */
    public static void undeploy(String dataSourcePath) throws IOException {

	Collection<String> jndiNames = XMLFileParsers.dataSourceNames(dataSourcePath);
	checkAndUndeploy(dataSourcePath, jndiNames);
	removeInitialized(dataSourcePath);
    }

    /**
     * Cleans and destroys passed {@link DataSource} instance
     *
     * @param dataSource
     */
    public static void cleanUp(DataSource dataSource) {
	InitDataSourceFactory.destroy(dataSource);
    }
}
