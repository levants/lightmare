package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.ObjectUtils;

/**
 * Initializes data source from configuration YAML file
 * 
 * @author Levan Tsinadze
 * @since 0.1.2
 */
public class YamlParsers {

    // Key elements
    private static final String DATASOURCES_KEY = "datasources";

    private static final String DATASOURCE_KEY = "datasource";

    private static final Logger LOG = Logger.getLogger(YamlParsers.class);

    private void setProperty(Map.Entry<Object, Object> entry,
	    Properties propertis) {

	Object key = entry.getKey();
	Object value = entry.getValue();
	if (ConnectionConfig.DRIVER_PROPERTY.name.equals(key)) {
	    String name = ObjectUtils.cast(value, String.class);
	    value = DriverConfig.getDriverName(name);
	}
	propertis.put(key, value);
    }

    private void initDataSource(Map<Object, Object> datasource)
	    throws IOException {

	Properties properties = new Properties();
	Set<Map.Entry<Object, Object>> entrySet = datasource.entrySet();
	for (Map.Entry<Object, Object> entry : entrySet) {
	    setProperty(entry, properties);
	}
	Initializer.registerDataSource(properties);
    }

    public void parseYaml(Map<Object, Object> config) throws IOException {

	Object value = config.get(DATASOURCES_KEY);
	if (ObjectUtils.notNull(value)) {
	    List<Map<Object, Object>> datasources = ObjectUtils.cast(value);
	    for (Map<Object, Object> datasource : datasources) {
		try {
		    initDataSource(datasource);
		} catch (IOException ex) {
		    LOG.error(ex.getMessage(), ex);
		}
	    }
	}

	value = config.get(DATASOURCE_KEY);
	if (ObjectUtils.notNull(value)) {
	    Map<Object, Object> datasource = ObjectUtils.cast(value);
	    initDataSource(datasource);
	}
    }
}
