package org.lightmare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Easy way to retrieve configuration properties from configuration file
 * 
 * @author levan
 * 
 */
public class Configuration {

	private final Map<String, String> config = new HashMap<String, String>();

	private static Logger logger = Logger.getLogger(Configuration.class);

	public String getStringValue(String key) {
		return config.get(key);
	}

	public int getIntValue(String key) {
		return Integer.parseInt(config.get(key));
	}

	public long getLongValue(String key) {
		return Long.parseLong(config.get(key));
	}

	public boolean getBooleanValue(String key) {
		return Boolean.parseBoolean(config.get(key));
	}

	public void putValue(String key, String value) {
		config.put(key, value);
	}

	public void loadFromFile(String configFilename) {
		try {
			FileInputStream propertiesStream = new FileInputStream(new File(
					configFilename));
			loadFromStream(propertiesStream);
			propertiesStream.close();
		} catch (Exception ex) {
			logger.error("Could not open config file", ex);
		}

	}

	public void loadFromResource(String resourceName, ClassLoader loader) {
		InputStream resourceStream = loader
				.getResourceAsStream(new StringBuilder("META-INF/").append(
						resourceName).toString());
		if (resourceStream == null) {
			logger.error("Configuration resource doesn't exist");
			return;
		}
		loadFromStream(resourceStream);
		try {
			resourceStream.close();
		} catch (IOException ex) {
			logger.error("Could not load resource", ex);
		}
	}

	/**
	 * Load {@link Configuration} in memory as {@link Map} of parameters
	 * 
	 * @throws IOException
	 */
	public void loadFromStream(InputStream propertiesStream) {
		try {
			Properties props = new Properties();
			props.load(propertiesStream);
			for (String propertyName : props.stringPropertyNames()) {
				config.put(propertyName, props.getProperty(propertyName));
			}
			propertiesStream.close();
		} catch (Exception ex) {
			logger.error("Could not load configuration", ex);
		}
	}
}
