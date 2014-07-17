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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.deploy.BeanLoader;
import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Parses XML files to initialize {@link javax.sql.DataSource}s and bind them to
 * <a href="http://www.oracle.com/technetwork/java/jndi/index.html">jndi</a>
 * {@link javax.naming.Context} by name
 * 
 * @author Levan Tsinadze
 * @since 0.0.15-SNAPSHOT
 */
public class FileParsers {

    private static final String DEFAULT_KEY = String
	    .valueOf(StringUtils.HYPHEN);;

    private static final Logger LOG = Logger.getLogger(FileParsers.class);

    /**
     * Checks if JNDI name is valid and passed container not contains elements
     * with this name
     * 
     * @param jndiName
     * @param container
     * @return <code>boolean</code>
     */
    private static boolean valid(String jndiName,
	    Map<String, Properties> container) {
	return StringUtils.valid(jndiName)
		&& (container.isEmpty() || CollectionUtils.notContains(
			container, jndiName));
    }

    /**
     * Gets data source {@link Properties} from XML configuration file
     * 
     * @param paths
     * @param datasources
     * @throws IOException
     */
    private static void fillFromXml(Collection<String> paths,
	    Map<String, List<Properties>> datasources) throws IOException {

	if (CollectionUtils.valid(paths)) {
	    List<Properties> xmlDatasources;
	    for (String dataSourcePath : paths) {
		xmlDatasources = XMLFileParsers
			.getPropertiesFromJBoss(dataSourcePath);
		if (CollectionUtils.valid(xmlDatasources)) {
		    datasources.put(dataSourcePath, xmlDatasources);
		}
	    }
	}
    }

    /**
     * Gets data source {@link Properties} from YAML configuration file
     * 
     * @param config
     * @param datasources
     * @throws IOException
     */
    private static void fillFromYaml(Configuration config,
	    Map<String, List<Properties>> datasources) throws IOException {

	if (ObjectUtils.notNull(config)) {
	    List<Properties> yamlDatasources = YamlParsers.parseYaml(config);
	    if (CollectionUtils.valid(yamlDatasources)) {
		datasources.put(DEFAULT_KEY, yamlDatasources);
	    }
	}
    }

    /**
     * Checks if passed {@link Properties} contains JNDI name property and puts
     * this name in passed {@link Map} of data sources
     * 
     * @param properties
     * @param container
     */
    private static void fillContainer(Properties properties,
	    Map<String, Properties> container) {

	String property = ConnectionConfig.JNDI_NAME_PROPERTY.name;
	String jndiName = properties.getProperty(property);
	if (valid(jndiName, container)) {
	    container.put(jndiName, properties);
	}
    }

    /**
     * Avoids duplicate data source properties
     * 
     * @param datasources
     * @return {@link List} of data source {@link Properties} without duplicates
     */
    private static List<Properties> shrink(List<Properties> datasources) {

	List<Properties> shrinked;

	if (CollectionUtils.valid(datasources)) {
	    Map<String, Properties> container = new HashMap<String, Properties>();
	    for (Properties properties : datasources) {
		fillContainer(properties, container);
	    }
	    shrinked = new ArrayList<Properties>(container.values());
	} else {
	    shrinked = Collections.emptyList();
	}

	return shrinked;
    }

    /**
     * Avoids duplicate data source properties
     * 
     * @param datasources
     * @return {@link Map} of JNDI names and {@link List} of data source
     *         {@link Properties} without duplicates
     */
    private static Map<String, List<Properties>> shrink(
	    Map<String, List<Properties>> datasources) {

	Map<String, List<Properties>> shrinked = new HashMap<String, List<Properties>>();

	if (CollectionUtils.valid(datasources)) {
	    Set<Map.Entry<String, List<Properties>>> entrySet = datasources
		    .entrySet();
	    String key;
	    List<Properties> value;
	    List<Properties> fine;
	    for (Map.Entry<String, List<Properties>> entry : entrySet) {
		key = entry.getKey();
		value = entry.getValue();
		fine = shrink(value);
		shrinked.put(key, fine);
	    }
	}

	return shrinked;
    }

    /**
     * Calculates sum of sizes for each {@link List} of data source
     * {@link Properties} for passed data sources
     * 
     * @param datasources
     * @return <code>int</code> suze of all {@link List} of {@link Properties}
     *         in passed data sources
     */
    private static int calculateSize(Map<String, List<Properties>> datasources) {

	int size = CollectionUtils.EMPTY_ARRAY_LENGTH;

	if (CollectionUtils.valid(datasources)) {
	    Collection<List<Properties>> values = datasources.values();
	    for (List<Properties> value : values) {
		size += value.size();
	    }
	}

	return size;
    }

    /**
     * Initializes and connection pool
     * 
     * @param properties
     * @param blocker
     */
    private static void initDatasource(Properties properties,
	    CountDownLatch blocker) {

	try {
	    // Initializes and fills BeanLoader.DataSourceParameters class
	    // to deploy data source
	    BeanLoader.DataSourceParameters parameters = new BeanLoader.DataSourceParameters();
	    parameters.properties = properties;
	    parameters.blocker = blocker;
	    BeanLoader.initializeDatasource(parameters);
	} catch (IOException ex) {
	    LOG.error(InitMessages.INITIALIZING_ERROR.message, ex);
	}
    }

    /**
     * Initializes each data source from {@link Properties} in parallel mode
     * 
     * @param datasources
     * @param blocker
     */
    private static void initDatasources(List<Properties> datasources,
	    CountDownLatch blocker) {

	if (CollectionUtils.valid(datasources)) {
	    for (Properties properties : datasources) {
		initDatasource(properties, blocker);
	    }
	}
    }

    /**
     * Parses standalone.xml file and initializes {@link javax.sql.DataSource}s
     * and binds them to JNDI context
     * 
     * @param dataSourcePath
     * @throws IOException
     */
    public static void parseDataSources(Collection<String> paths,
	    Configuration config) throws IOException {

	Map<String, List<Properties>> datasources = new HashMap<String, List<Properties>>();

	fillFromYaml(config, datasources);
	fillFromXml(paths, datasources);
	datasources = shrink(datasources);
	int size = calculateSize(datasources);
	// Blocking semaphore before all data source initialization finished
	CountDownLatch blocker = new CountDownLatch(size);
	Set<Map.Entry<String, List<Properties>>> entrySet = datasources
		.entrySet();
	String key;
	List<Properties> value;
	for (Map.Entry<String, List<Properties>> entry : entrySet) {
	    key = entry.getKey();
	    value = entry.getValue();
	    initDatasources(value, blocker);
	    Initializer.setDsAsInitialized(key);
	}
	// Tries to lock until operation is complete
	try {
	    blocker.await();
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	}
    }

    public static void parseDataSources(String path) throws IOException {
	Collection<String> paths = Collections.singleton(path);
	parseDataSources(paths, null);
    }
}
