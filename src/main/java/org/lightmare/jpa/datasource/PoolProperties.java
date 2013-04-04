package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.lightmare.utils.ObjectUtils;

/**
 * Searchs properties files for c3p0 pool and loads them
 * 
 * @author levan
 * 
 */
public class PoolProperties {

    public static Properties load(String path) throws IOException {

	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	if (!ObjectUtils.available(path)) {
	    path = "META-INF/pool.properties";
	}
	InputStream stream = loader.getResourceAsStream(path);
	try {
	    Properties properties = new Properties();
	    properties.load(stream);

	    return properties;
	} finally {
	    if (ObjectUtils.notNull(stream)) {
		stream.close();
	    }
	}

    }
}
