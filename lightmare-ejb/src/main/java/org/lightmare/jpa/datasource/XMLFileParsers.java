package org.lightmare.jpa.datasource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.io.parsers.XMLUtils;
import org.lightmare.utils.namimg.NamingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * File parser for XML data source formats
 *
 * @author Levan Tsinadze
 * @since 0.0.15-SNAPSHOT
 *
 */
public class XMLFileParsers {

    // Tag names for XML file parser
    public static final String JBOSS_TAG_NAME = "urn:jboss:domain:datasources:1.0";

    // Tag names for data source properties initialization
    private static final String DATA_SURCE_TAG = "datasource";
    private static final String USER_TAG = "user-name";
    private static final String PASSWORD_TAG = "password";
    private static final String DRIVER_TAG = "driver";
    private static final String MAX_POOL_TAG = "max-pool-size";
    private static final String MIN_POOL_TAG = "min-pool-size";
    private static final String INITIAL_POOL_TAG = "prefill";
    private static final String JNDI_NAME_TAG = "jndi-name";
    private static final String CONNECTION_URL_TAG = "connection-url";
    private static final String SECURITY_TAG = "security";
    private static final String POOL_TAG = "pool";

    /**
     * Initializes
     * <a href="http://www.oracle.com/technetwork/java/javase/jdbc/index.html" >
     * jdbc</a> driver for appropriated {@link javax.sql.DataSource} for
     * connection pooling
     *
     * @param nodeList
     * @param properties
     */
    private static void setDataFromJBossDriver(NodeList nodeList, Properties properties) {

	Element thisElement = XMLUtils.getFirstElement(nodeList);
	String name = XMLUtils.getContext(thisElement);
	String driverName = DriverConfig.getDriverName(name);
	properties.setProperty(ConnectionConfig.DRIVER_PROPERTY.name, driverName);
    }

    /**
     * Gets security information from {@link javax.sql.DataSource} meta data
     *
     * @param nodeList
     * @param properties
     */
    private static void setDataFromJBossSecurity(NodeList nodeList, Properties properties) {

	boolean valid;
	int length = nodeList.getLength();
	for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
	    Element thisElement = XMLUtils.getElement(nodeList, i);
	    NodeList userList = thisElement.getElementsByTagName(USER_TAG);
	    valid = XMLUtils.validate(userList);
	    if (valid) {
		Element userElement = XMLUtils.getFirstElement(userList);
		String user = XMLUtils.getContext(userElement);
		properties.setProperty(ConnectionConfig.USER_PROPERTY.name, user);
		NodeList passList = thisElement.getElementsByTagName(PASSWORD_TAG);
		valid = XMLUtils.validate(passList);
		if (valid) {
		    Element passElement = XMLUtils.getFirstElement(passList);
		    String password = XMLUtils.getContext(passElement);
		    properties.setProperty(ConnectionConfig.PASSWORD_PROPERTY.name, password);
		}
	    }
	}
    }

    /**
     * Sets minimum size of connections in pool
     *
     * @param element
     * @param properties
     * @return {@link String}
     */
    private static String setMinPoolSize(Element element, Properties properties) {

	String minPoolSize;

	NodeList minPoolSizeList = element.getElementsByTagName(MIN_POOL_TAG);
	int elementLength = minPoolSizeList.getLength();
	if (elementLength == CollectionUtils.EMPTY) {
	    Element minPoolSizeElement = XMLUtils.getFirstElement(minPoolSizeList);
	    minPoolSize = XMLUtils.getContext(minPoolSizeElement);

	    properties.setProperty(PoolConfig.Defaults.MIN_POOL_SIZE.key, minPoolSize);
	} else {
	    minPoolSize = null;
	}

	return minPoolSize;
    }

    /**
     * Sets maximum size of connections in pool
     *
     * @param element
     * @param properties
     * @return <code>boolean</code>
     */
    private static boolean setMaxPoolSize(Element element, Properties properties) {

	boolean valid;

	NodeList maxPoolSizeList = element.getElementsByTagName(MAX_POOL_TAG);
	int elementLength = maxPoolSizeList.getLength();
	if (elementLength > CollectionUtils.EMPTY_ARRAY_LENGTH) {
	    valid = Boolean.TRUE;
	    Element maxPoolSizeElement = XMLUtils.getFirstElement(maxPoolSizeList);
	    String maxPoolSize = XMLUtils.getContext(maxPoolSizeElement);

	    properties.setProperty(PoolConfig.Defaults.MAX_POOL_SIZE.key, maxPoolSize);
	} else {
	    valid = Boolean.FALSE;
	}

	return valid;
    }

    /**
     * Sets initial size of connections in pool
     *
     * @param element
     * @param properties
     * @param minPoolSize
     */
    private static void setInitPoolSize(Element element, Properties properties, String minPoolSize) {

	NodeList initPoolSizeList = element.getElementsByTagName(INITIAL_POOL_TAG);
	int elementLength = initPoolSizeList.getLength();
	if (elementLength > CollectionUtils.EMPTY_ARRAY_LENGTH) {
	    Element initPoolSizeElement = XMLUtils.getFirstElement(initPoolSizeList);
	    String prefill = XMLUtils.getContext(initPoolSizeElement);
	    if (Boolean.valueOf(prefill)) {
		properties.setProperty(PoolConfig.Defaults.INITIAL_POOL_SIZE.key, minPoolSize);
	    }
	}
    }

    private static void setPoolDataFromNode(NodeList nodeList, Properties properties, int i) {

	Element thisElement = XMLUtils.getElement(nodeList, i);
	String minPoolSize = setMinPoolSize(thisElement, properties);
	if (ObjectUtils.notNull(minPoolSize)) {
	    boolean valid = setMaxPoolSize(thisElement, properties);
	    if (valid) {
		setInitPoolSize(thisElement, properties, minPoolSize);
	    }
	}
    }

    /**
     * Gets security information from {@link javax.sql.DataSource} meta data
     *
     * @param nodeList
     * @param properties
     */
    private static void setDataFromJBossPool(NodeList nodeList, Properties properties) {

	for (int i = CollectionUtils.FIRST_INDEX; i < nodeList.getLength(); i++) {
	    setPoolDataFromNode(nodeList, properties, i);
	}
    }

    /**
     * Sets JNDI name and native name of connection pool
     *
     * @param element
     * @param props
     */
    private static void setJndiName(Element element, Properties properties) {

	String jndiName = element.getAttribute(JNDI_NAME_TAG);
	String clearName = NamingUtils.clearDataSourceName(jndiName);
	properties.setProperty(ConnectionConfig.JNDI_NAME_PROPERTY.name, jndiName);
	properties.setProperty(ConnectionConfig.NAME_PROPERTY.name, clearName);
    }

    /**
     * Sets data from URL tag
     *
     * @param element
     * @param urlList
     * @param properties
     */
    private static void setFromURLData(Element element, NodeList urlList, Properties properties) {

	Element urlElement = XMLUtils.getFirstElement(urlList);
	String url = XMLUtils.getContext(urlElement);
	properties.setProperty(ConnectionConfig.URL_PROPERTY.name, url);
	NodeList securityList = element.getElementsByTagName(SECURITY_TAG);
	setDataFromJBossSecurity(securityList, properties);

	NodeList poolList = element.getElementsByTagName(POOL_TAG);
	setDataFromJBossPool(poolList, properties);

	NodeList driverList = element.getElementsByTagName(DRIVER_TAG);
	setDataFromJBossDriver(driverList, properties);
    }

    /**
     * Sets JNDI name native name and security, URL, driver properties
     *
     * @param nodeList
     * @param properties
     * @param i
     *            index of current node
     */
    private static void setConnectionDataFromNode(NodeList nodeList, List<Properties> properties, int i) {

	Element thisElement = XMLUtils.getElement(nodeList, i);
	Properties props = new Properties();
	setJndiName(thisElement, props);
	NodeList urlList = thisElement.getElementsByTagName(CONNECTION_URL_TAG);
	int urlElementLength = urlList.getLength();
	if (urlElementLength > CollectionUtils.EMPTY_ARRAY_LENGTH) {
	    setFromURLData(thisElement, urlList, props);
	    properties.add(props);
	}
    }

    /**
     * Gets {@link javax.sql.DataSource}s configuration properties as
     * {@link List} of {@link Properties}
     *
     * @param nodeList
     * @return
     */
    private static List<Properties> getDataFromJBoss(NodeList nodeList) {

	List<Properties> properties = new ArrayList<Properties>();

	for (int i = CollectionUtils.FIRST_INDEX; i < nodeList.getLength(); i++) {
	    setConnectionDataFromNode(nodeList, properties, i);
	}

	return properties;
    }

    /**
     * Gets data source descriptor tags from passed {@link Document} parameter
     *
     * @param document
     * @return {@link NodeList}
     */
    private static NodeList getDataSourceTags(Document document) {
	NodeList nodeList = document.getElementsByTagName(DATA_SURCE_TAG);
	return nodeList;
    }

    /**
     * Gets data source descriptor tags from passed {@link File} parameter
     *
     * @param file
     * @return {@link NodeList}
     * @throws IOException
     */
    private static NodeList getDataSourceTags(File file) throws IOException {

	NodeList nodeList;

	Document document = XMLUtils.document(file);
	nodeList = getDataSourceTags(document);

	return nodeList;
    }

    /**
     * Gets data source descriptor tags from passed file path
     *
     * @param dataSourcePath
     * @return {@link NodeList}
     * @throws IOException
     */
    private static NodeList getDataSourceTags(String dataSourcePath) throws IOException {

	NodeList nodeList;

	File file = new File(dataSourcePath);
	nodeList = getDataSourceTags(file);

	return nodeList;
    }

    /**
     * Retrieves data source JNDI names from passed file
     *
     * @param dataSourcePath
     * @return {@link Collection} of {@link String}s
     * @throws IOException
     */
    public static Collection<String> dataSourceNames(String dataSourcePath) throws IOException {

	Collection<String> jndiNames = new HashSet<String>();

	NodeList nodeList = getDataSourceTags(dataSourcePath);
	String jndiName;
	int length = nodeList.getLength();
	for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
	    Element thisElement = XMLUtils.getElement(nodeList, i);
	    jndiName = thisElement.getAttribute(JNDI_NAME_TAG);
	    jndiNames.add(jndiName);
	}

	return jndiNames;
    }

    /**
     * Reads properties from XML file
     *
     * @param dataSourcePath
     * @return {@link Properties}
     * @throws IOException
     */
    public static List<Properties> getPropertiesFromJBoss(String dataSourcePath) throws IOException {

	List<Properties> properties;

	NodeList nodeList = getDataSourceTags(dataSourcePath);
	properties = getDataFromJBoss(nodeList);

	return properties;
    }
}
