package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.lightmare.utils.ObjectUtils;

public class YamlParsers {

    private static final String DATASOURCES_KEY = "datasources";

    public void parseYaml(Map<Object, Object> config) throws IOException {

	Object value = config.get(DATASOURCES_KEY);
	List<Map<Object, Object>> datasources = ObjectUtils.cast(value);
	Properties propertis;
	for (Map<Object, Object> datasource : datasources) {
	    propertis = new Properties();
	    propertis.putAll(datasource);
	    Initializer.registerDataSource(propertis);
	}
    }
}
