package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.ObjectUtils;

public class YamlParsers {

    private static final String DATASOURCES_KEY = "datasources";

    private static final String DATASOURCE_KEY = "datasource";

    private static final Logger LOG = Logger.getLogger(YamlParsers.class);

    private void initDataSource(Map<Object, Object> datasource)
	    throws IOException {

	Properties propertis = new Properties();
	Set<Map.Entry<Object, Object>> entrySet = datasource.entrySet();
	Object key;
	Object value;
	for (Map.Entry<Object, Object> entry : entrySet) {
	    key = entry.getKey();
	    value = entry.getValue();
	    if (ConnectionConfig.DRIVER_PROPERTY.name.equals(key)) {
		value = DriverConfig
			.getDriverName(ConnectionConfig.DRIVER_PROPERTY.name);
	    }
	    propertis.put(key, value);
	}
	Initializer.registerDataSource(propertis);
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
