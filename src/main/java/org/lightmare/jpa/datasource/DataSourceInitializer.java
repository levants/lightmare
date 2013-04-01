package org.lightmare.jpa.datasource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.jndi.NamingUtils;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * Parses xml and property files to initialize and cache {@link DataSource}
 * objects
 * 
 * @author levan
 * 
 */
public class DataSourceInitializer {

    private NamingUtils namingUtils;

    private Context context;

    private static final Set<String> INITIALIZED_SOURCES = Collections
	    .synchronizedSet(new HashSet<String>());

    public static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    public DataSourceInitializer() throws IOException {
	this.namingUtils = new NamingUtils();
	context = this.namingUtils.getContext();
    }

    private static boolean checkForDataSource(String path) {
	return path != null && !path.isEmpty();
    }

    /**
     * Initialized datasource
     * 
     * @throws IOException
     */
    public static void initializeDataSource(String path) throws IOException {
	if (checkForDataSource(path)
		&& !DataSourceInitializer.checkDSPath(path)) {
	    FileParsers parsers = new FileParsers();
	    parsers.parseStandaloneXml(path);
	}
    }

    /**
     * Sets default connection pooling properties
     * 
     * @return
     */
    private static Map<Object, Object> getDefaultPooling() {
	Map<Object, Object> c3p0Properties = new HashMap<Object, Object>();
	c3p0Properties.put(PoolConfig.MAX_POOL_SIZE,
		PoolConfig.MAX_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.INITIAL_POOL_SIZE,
		PoolConfig.INITIAL_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MIN_POOL_SIZE,
		PoolConfig.MIN_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MAX_IDLE_TIMEOUT,
		PoolConfig.MAX_IDLE_TIMEOUT_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MAX_STATEMENTS,
		PoolConfig.MAX_STATEMENTS_DEF_VALUE);
	c3p0Properties.put(PoolConfig.AQUIRE_INCREMENT,
		PoolConfig.AQUIRE_INCREMENT_DEF_VALUE);

	return c3p0Properties;
    }

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     * 
     * @param properties
     * @return {@link DataSource}
     * @throws IOException
     */
    public DataSource initilizeDriver(Properties properties) throws IOException {

	String driver = properties.getProperty("driver").trim();
	String url = properties.getProperty("url").trim();
	String user = properties.getProperty("user").trim();
	String password = properties.getProperty("password").trim();

	ComboPooledDataSource dataSource = new ComboPooledDataSource();
	dataSource.setJdbcUrl(url);
	try {
	    dataSource.setDriverClass(driver);
	} catch (PropertyVetoException ex) {
	    throw new IOException(ex);
	}
	dataSource.setUser(user);
	dataSource.setPassword(password);

	return dataSource;

    }

    /**
     * Registers {@link DataSource} object in jndi {@link Context}
     * 
     * @param poolingProperties
     * @param dataSource
     * @param jndiName
     * @throws IOException
     */
    public void registerDataSource(Map<Object, Object> poolingProperties,
	    DataSource dataSource, String jndiName) throws IOException {
	try {
	    DataSource namedDataSource = DataSources.pooledDataSource(
		    dataSource, poolingProperties);
	    if (namedDataSource instanceof PooledDataSource) {
		context.rebind(jndiName, namedDataSource);
	    } else {
		throw new IOException(
			"Data source is not PooledDataSource instance");
	    }
	    LOG.info(String.format("Data source %s initialized", jndiName));
	} catch (SQLException ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	} catch (NamingException ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	} catch (Exception ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	}
    }

    /**
     * Initializes and registers {@link DataSource} object in jndi
     * {@link Context}
     * 
     * @param poolingProperties
     * @param dataSource
     * @param jndiName
     * @throws IOException
     */
    public void registerDataSource(Properties properties,
	    Properties poolingProperties) throws IOException {
	String jndiName = properties.getProperty("name");
	LOG.info(String.format("Initializing data source %s", jndiName));
	DataSource dataSource = initilizeDriver(properties);
	Map<Object, Object> poolProps = poolingProperties;
	if (poolingProperties == null) {
	    poolProps = getDefaultPooling();
	}
	registerDataSource(poolProps, dataSource, jndiName);
    }

    /**
     * Parses xml file and initializes and registers {@link DataSource} object
     * in jndi
     * 
     * @param properties
     * @throws IOException
     */
    public void registerDataSource(Properties properties) throws IOException {
	registerDataSource(properties, null);
    }

    public static void setDsAsInitialized(String datasourcePath) {
	INITIALIZED_SOURCES.add(datasourcePath);
    }

    public static boolean checkDSPath(String datasourcePath) {
	return INITIALIZED_SOURCES.contains(datasourcePath);
    }

    /**
     * Destroys all registered pooled {@link DataSource}s for shut down hook
     */
    public static void cleanUp() {

	@SuppressWarnings("unchecked")
	Set<DataSource> dataSources = C3P0Registry.getPooledDataSources();
	for (DataSource dataSource : dataSources) {
	    try {
		DataSources.destroy(dataSource);
	    } catch (SQLException ex) {
		LOG.error("Could not destroy data source", ex);
	    }
	}
    }

}
