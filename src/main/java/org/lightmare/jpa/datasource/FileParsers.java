package org.lightmare.jpa.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.lightmare.deploy.BeanLoader;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses xml files to initialize {@link javax.sql.DataSource}s and bind them to
 * <a href="http://www.oracle.com/technetwork/java/jndi/index.html">jndi</a>
 * {@link javax.naming.Context} by name
 * 
 * @author levan
 * 
 */
public class FileParsers {

    public static final String JBOSS_TAG_NAME = "urn:jboss:domain:datasources:1.0";

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

    private static final Logger LOG = Logger.getLogger(FileParsers.class);

    public static Document document(File file) throws IOException {

	return document(file.toURI().toURL());
    }

    public static Document document(URL url) throws IOException {

	URLConnection connection = url.openConnection();
	InputStream stream = connection.getInputStream();
	try {
	    return parse(stream);
	} finally {
	    ObjectUtils.close(stream);
	}
    }

    /**
     * Gets item with index 0 from passed {@link NodeList} instance
     * 
     * @param list
     * @return {@link Node}
     */
    private static Node getFirst(NodeList list) {

	return list.item(ObjectUtils.FIRST_INDEX);
    }

    /**
     * To get text from tag depended on jre installation
     * 
     * @param element
     * @return {@link String}
     */
    public static String getContext(Element element) {

	NodeList textList = element.getChildNodes();
	Node firstNode = getFirst(textList);
	String data = firstNode.getNodeValue().trim();
	return data;
    }

    /**
     * Parses xml document to initialize {@link javax.sql.DataSource}s
     * configuration properties
     * 
     * @param stream
     * @return {@link Document}
     * @throws IOException
     */
    public static Document parse(InputStream stream) throws IOException {

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	Document document;
	try {
	    builder = factory.newDocumentBuilder();
	    document = builder.parse(stream);
	} catch (ParserConfigurationException ex) {
	    throw new IOException(ex);
	} catch (SAXException ex) {
	    throw new IOException(ex);
	}

	return document;
    }

    /**
     * Initializes <a
     * href="http://www.oracle.com/technetwork/java/javase/jdbc/index.html"
     * >jdbc</a> driver for appropriated {@link javax.sql.DataSource} for
     * connection pooling
     * 
     * @param nodeList
     * @param properties
     */
    public void setDataFromJBossDriver(NodeList nodeList, Properties properties) {

	Element thisElement = (Element) getFirst(nodeList);
	String name = getContext(thisElement);
	String driverName = DriverConfig.getDriverName(name);
	properties.setProperty(DataSourceInitializer.DRIVER_PROPERTY,
		driverName);
    }

    /**
     * Gets security information from {@link javax.sql.DataSource} meta data
     * 
     * @param nodeList
     * @param properties
     */
    public void setDataFromJBossSecurity(NodeList nodeList,
	    Properties properties) {

	for (int i = 0; i < nodeList.getLength(); i++) {
	    Element thisElement = (Element) nodeList.item(i);
	    NodeList userList = thisElement.getElementsByTagName(USER_TAG);
	    int elementLength = userList.getLength();
	    if (elementLength == ObjectUtils.EMPTY_ARRAY_LENGTH) {
		continue;
	    }
	    Element userElement = (Element) getFirst(userList);
	    String user = getContext(userElement);

	    properties.setProperty(DataSourceInitializer.USER_PROPERTY, user);

	    NodeList passList = thisElement.getElementsByTagName(PASSWORD_TAG);
	    elementLength = passList.getLength();
	    if (elementLength == ObjectUtils.EMPTY_ARRAY_LENGTH) {
		continue;
	    }
	    Element passElement = (Element) getFirst(passList);
	    String password = getContext(passElement);

	    properties.setProperty(DataSourceInitializer.PASSWORD_PROPERTY,
		    password);
	}
    }

    /**
     * Gets security information from {@link javax.sql.DataSource} meta data
     * 
     * @param nodeList
     * @param properties
     */
    public void setDataFromJBossPool(NodeList nodeList, Properties properties) {

	for (int i = 0; i < nodeList.getLength(); i++) {
	    Element thisElement = (Element) nodeList.item(i);
	    NodeList minPoolSizeList = thisElement
		    .getElementsByTagName(MIN_POOL_TAG);
	    int elementLength = minPoolSizeList.getLength();
	    if (elementLength == ObjectUtils.EMPTY_ARRAY_LENGTH) {
		continue;
	    }
	    Element minPoolSizeElement = (Element) getFirst(minPoolSizeList);
	    String minPoolSize = getContext(minPoolSizeElement);

	    properties.setProperty(PoolConfig.MIN_POOL_SIZE, minPoolSize);

	    NodeList maxPoolSizeList = thisElement
		    .getElementsByTagName(MAX_POOL_TAG);
	    elementLength = maxPoolSizeList.getLength();
	    if (elementLength == ObjectUtils.EMPTY_ARRAY_LENGTH) {
		continue;
	    }
	    Element maxPoolSizeElement = (Element) getFirst(maxPoolSizeList);
	    String maxPoolSize = getContext(maxPoolSizeElement);

	    properties.setProperty(PoolConfig.MAX_POOL_SIZE, maxPoolSize);

	    NodeList initPoolSizeList = thisElement
		    .getElementsByTagName(INITIAL_POOL_TAG);
	    elementLength = initPoolSizeList.getLength();
	    if (elementLength == ObjectUtils.EMPTY_ARRAY_LENGTH) {
		continue;
	    }
	    Element initPoolSizeElement = (Element) getFirst(initPoolSizeList);
	    String prefill = getContext(initPoolSizeElement);
	    if (Boolean.valueOf(prefill)) {
		properties.setProperty(PoolConfig.INITIAL_POOL_SIZE,
			minPoolSize);
	    }
	}
    }

    /**
     * Gets {@link javax.sql.DataSource}s configuration properties as
     * {@link List} of {@link Properties}
     * 
     * @param nodeList
     * @return
     */
    public List<Properties> getDataFromJBoss(NodeList nodeList) {

	List<Properties> properties = new ArrayList<Properties>();
	String jndiName;
	String clearName;
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Element thisElement = (Element) nodeList.item(i);
	    Properties props = new Properties();
	    jndiName = thisElement.getAttribute(JNDI_NAME_TAG);
	    clearName = NamingUtils.clearDataSourceName(jndiName);
	    props.setProperty(DataSourceInitializer.JNDI_NAME_PROPERTY,
		    jndiName);
	    props.setProperty(DataSourceInitializer.NAME_PROPERTY, clearName);
	    NodeList urlList = thisElement
		    .getElementsByTagName(CONNECTION_URL_TAG);
	    int urlElementLength = urlList.getLength();
	    if (urlElementLength == ObjectUtils.EMPTY_ARRAY_LENGTH) {
		continue;
	    }
	    Element urlElement = (Element) getFirst(urlList);
	    String url = getContext(urlElement);
	    props.setProperty(DataSourceInitializer.URL_PROPERTY, url);
	    NodeList securityList = thisElement
		    .getElementsByTagName(SECURITY_TAG);
	    setDataFromJBossSecurity(securityList, props);

	    NodeList poolList = thisElement.getElementsByTagName(POOL_TAG);
	    setDataFromJBossPool(poolList, props);

	    NodeList driverList = thisElement.getElementsByTagName(DRIVER_TAG);
	    setDataFromJBossDriver(driverList, props);

	    properties.add(props);
	}

	return properties;
    }

    private static NodeList getDataSourceTags(Document document) {

	NodeList nodeList = document.getElementsByTagName(DATA_SURCE_TAG);

	return nodeList;
    }

    private static NodeList getDataSourceTags(File file) throws IOException {

	Document document = document(file);
	NodeList nodeList = getDataSourceTags(document);

	return nodeList;
    }

    private static NodeList getDataSourceTags(String dataSourcePath)
	    throws IOException {

	File file = new File(dataSourcePath);
	NodeList nodeList = getDataSourceTags(file);

	return nodeList;
    }

    /**
     * Retrieves data source jndi names from passed file
     * 
     * @param dataSourcePath
     * @return
     * @throws IOException
     */
    public static Collection<String> dataSourceNames(String dataSourcePath)
	    throws IOException {

	Collection<String> jndiNames = new HashSet<String>();
	NodeList nodeList = getDataSourceTags(dataSourcePath);
	String jndiName;
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Element thisElement = (Element) nodeList.item(i);
	    jndiName = thisElement.getAttribute(JNDI_NAME_TAG);
	    jndiNames.add(jndiName);
	}

	return jndiNames;
    }

    /**
     * Parses standalone.xml file and initializes {@link javax.sql.DataSource}s
     * and binds them to jndi context
     * 
     * @param dataSourcePath
     * @throws IOException
     */
    public void parseStandaloneXml(String dataSourcePath) throws IOException {

	NodeList nodeList = getDataSourceTags(dataSourcePath);

	List<Properties> properties = getDataFromJBoss(nodeList);

	// Blocking semaphore before all data source initialization finished
	CountDownLatch blocker = new CountDownLatch(properties.size());

	BeanLoader.DataSourceParameters parameters;
	for (Properties props : properties) {
	    try {
		// Initializes and fills BeanLoader.DataSourceParameters class
		// to deploy data source
		parameters = new BeanLoader.DataSourceParameters();
		parameters.properties = props;
		parameters.blocker = blocker;

		BeanLoader.initializeDatasource(parameters);

	    } catch (IOException ex) {
		LOG.error("Could not initialize datasource", ex);
	    }
	}

	try {
	    blocker.await();
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	}

	DataSourceInitializer.setDsAsInitialized(dataSourcePath);
    }
}
