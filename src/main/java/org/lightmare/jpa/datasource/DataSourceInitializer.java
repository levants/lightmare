package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.derby.jdbc.EmbeddedDataSource40;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.lightmare.jndi.NamingUtils;

import com.mchange.v2.c3p0.DataSources;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

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

	private static Properties getDefaultPooling() {
		Properties c3p0Properties = new Properties();
		c3p0Properties.setProperty("hibernate.c3p0.min_size", "5");
		c3p0Properties.setProperty("hibernate.c3p0.max_size", "15");
		c3p0Properties.setProperty("hibernate.c3p0.timeout", "1800");
		c3p0Properties.setProperty("hibernate.c3p0.max_statements", "50");

		return c3p0Properties;
	}

	public DataSource initilizeDriver(Properties properties) throws IOException {

		String driver = properties.getProperty("driver").trim();
		String url = properties.getProperty("url").trim();
		String user = properties.getProperty("user").trim();
		String password = properties.getProperty("password").trim();

		if (DriverConfig.isOracle(driver)) {
			try {
				OracleDataSource dataSource = new OracleDataSource();
				dataSource.setURL(url);
				dataSource.setUser(user);
				dataSource.setPassword(password);
				return dataSource;
			} catch (SQLException ex) {
				throw new IOException(ex);
			}
		} else if (DriverConfig.isMySQL(driver)) {

			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUrl(url);
			dataSource.setUser(user);
			dataSource.setPassword(password);
			return dataSource;

		} else if (DriverConfig.isDB2(driver)) {

			throw new IOException("This type of driver is not supported yet");

		} else if (DriverConfig.isMsSQL(driver)) {

			SQLServerDataSource dataSource = new SQLServerDataSource();
			dataSource.setURL(url);
			dataSource.setUser(user);
			dataSource.setPassword(password);
			return dataSource;

		} else if (DriverConfig.isH2(driver)) {

			JdbcDataSource dataSource = new JdbcDataSource();
			dataSource.setURL(url);
			dataSource.setUser(user);
			dataSource.setPassword(password);
			return dataSource;

		} else if (DriverConfig.isDerby(driver)) {
			EmbeddedDataSource40 dataSource = new EmbeddedDataSource40();
			String[] props = url.split(";");
			if (props.length == 0) {
				return null;
			}
			List<String> settingsList = Arrays.asList(props);
			Set<String> settings = new HashSet<String>();
			settings.addAll(settingsList);
			if (settings.contains("create=true")) {
				dataSource.setCreateDatabase("create");
			}
			String jdbcUrl;
			for (String setting : settings) {
				if (setting.contains("jdbc:derby:")) {
					jdbcUrl = setting.replaceAll("jdbc:derby:", "").trim();
					dataSource.setDatabaseName(jdbcUrl);
					break;
				}
			}
			dataSource.setUser(user);
			dataSource.setPassword(password);
			return dataSource;
		}
		return null;
	}

	public void registerDataSource(Properties c3p0Properties,
			DataSource dataSource, String jndiName) throws IOException {
		try {
			DataSource namedDataSource = DataSources.pooledDataSource(
					dataSource, c3p0Properties);
			context.rebind(jndiName, namedDataSource);
			LOG.info(String.format("data source %s initialized", jndiName));
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

	public void registerDataSource(Properties properties,
			Properties c3p0Properties) throws IOException {
		String jndiName = properties.getProperty("name");
		LOG.info(String.format("Initializing data source %s", jndiName));
		DataSource dataSource = initilizeDriver(properties);
		if (c3p0Properties == null) {
			c3p0Properties = getDefaultPooling();
		}
		registerDataSource(c3p0Properties, dataSource, jndiName);
	}

	public void registerDataSource(Properties properties) throws IOException {
		registerDataSource(properties, null);
	}

	public static void setDsAsInitialized(String datasourcePath) {
		INITIALIZED_SOURCES.add(datasourcePath);
	}

	public static boolean checkDSPath(String datasourcePath) {
		return INITIALIZED_SOURCES.contains(datasourcePath);
	}

}
