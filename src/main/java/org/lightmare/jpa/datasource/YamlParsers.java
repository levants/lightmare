package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Initializes data source from configuration YAML file
 * 
 * @author Levan Tsinadze
 * @since 0.1.2
 */
public class YamlParsers {

    private static final Logger LOG = Logger.getLogger(YamlParsers.class);

    private static void setProperty(Map.Entry<Object, Object> entry,
	    Properties propertis) {

	Object key = entry.getKey();
	Object value = entry.getValue();
	if (ConnectionConfig.DRIVER_PROPERTY.name.equals(key)) {
	    String name = ObjectUtils.cast(value, String.class);
	    value = DriverConfig.getDriverName(name);
	}
	propertis.put(key, value);
    }

    private static Properties initDataSource(Map<Object, Object> datasource)
	    throws IOException {

	Properties properties = new Properties();

	Set<Map.Entry<Object, Object>> entrySet = datasource.entrySet();
	for (Map.Entry<Object, Object> entry : entrySet) {
	    setProperty(entry, properties);
	}

	return properties;
    }

    public static List<Properties> parseYaml(Configuration config) throws IOException {

	List<Properties> datasources = new ArrayList<Properties>();

	Properties datasource;
	List<Map<Object, Object>> YamlDatasources = config.getDataSources();
	if (CollectionUtils.valid(YamlDatasources)) {
	    for (Map<Object, Object> yamlDatasource : YamlDatasources) {
		try {
		    datasource = initDataSource(yamlDatasource);
		    datasources.add(datasource);
		} catch (IOException ex) {
		    LOG.error(ex.getMessage(), ex);
		}
	    }
	}

	Map<Object, Object> yamlDatasource = config.getDataSource();
	if (CollectionUtils.valid(yamlDatasource)) {
	    datasource = initDataSource(yamlDatasource);
	    datasources.add(datasource);
	}

	return datasources;
    }
}
