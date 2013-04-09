package org.lightmare.jpa.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.lightmare.deploy.BeanLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mchange.v2.codegen.bean.Property;

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

    public Document document(File file) throws MalformedURLException,
	    IOException {

	return document(file.toURI().toURL());
    }

    public Document document(URL url) throws IOException {

	URLConnection connection = url.openConnection();
	InputStream stream = connection.getInputStream();
	try {
	    return parse(stream);
	} finally {
	    stream.close();
	}
    }

    /**
     * To get text from tag depended on jre installation
     * 
     * @param element
     * @return {@link String}
     */
    public static String getContext(Element element) {

	NodeList textList = element.getChildNodes();
	String data = ((Node) textList.item(0)).getNodeValue().trim();
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

	Element thisElement = (Element) nodeList.item(0);
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
	    if (elementLength == 0) {
		continue;
	    }
	    Element userElement = (Element) userList.item(0);
	    String user = getContext(userElement);

	    properties.setProperty(DataSourceInitializer.USER_PROPERTY, user);

	    NodeList passList = thisElement.getElementsByTagName(PASSWORD_TAG);
	    elementLength = passList.getLength();
	    if (elementLength == 0) {
		continue;
	    }
	    Element passElement = (Element) passList.item(0);
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
	    if (elementLength == 0) {
		continue;
	    }
	    Element minPoolSizeElement = (Element) minPoolSizeList.item(0);
	    String minPoolSize = getContext(minPoolSizeElement);

	    properties.setProperty(PoolConfig.MIN_POOL_SIZE, minPoolSize);

	    NodeList maxPoolSizeList = thisElement
		    .getElementsByTagName(MAX_POOL_TAG);
	    elementLength = maxPoolSizeList.getLength();
	    if (elementLength == 0) {
		continue;
	    }
	    Element maxPoolSizeElement = (Element) maxPoolSizeList.item(0);
	    String maxPoolSize = getContext(maxPoolSizeElement);

	    properties.setProperty(PoolConfig.MAX_POOL_SIZE, maxPoolSize);

	    NodeList initPoolSizeList = thisElement
		    .getElementsByTagName(INITIAL_POOL_TAG);
	    elementLength = initPoolSizeList.getLength();
	    if (elementLength == 0) {
		continue;
	    }
	    Element initPoolSizeElement = (Element) initPoolSizeList.item(0);
	    String prefill = getContext(initPoolSizeElement);
	    if (Boolean.valueOf(prefill)) {
		properties.setProperty(PoolConfig.INITIAL_POOL_SIZE,
			minPoolSize);
	    }
	}
    }

    /**
     * Gets {@link javax.sql.DataSource}s configuration properties as
     * {@link List} of {@link Property}
     * 
     * @param nodeList
     * @return
     */
    public List<Properties> getDataFromJBoss(NodeList nodeList) {

	List<Properties> properties = new ArrayList<Properties>();
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Element thisElement = (Element) nodeList.item(i);
	    Properties props = new Properties();
	    props.setProperty(DataSourceInitializer.JNDI_NAME_PROPERTY,
		    thisElement.getAttribute(JNDI_NAME_TAG));
	    NodeList urlList = thisElement
		    .getElementsByTagName(CONNECTION_URL_TAG);
	    int urlElementLength = urlList.getLength();
	    if (urlElementLength == 0) {
		continue;
	    }
	    Element urlElement = (Element) urlList.item(0);
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

    /**
     * Parses standalone.xml file and initializes {@link javax.sql.DataSource}s
     * and binds them to jndi context
     * 
     * @param dataSourcePath
     * @throws IOException
     */
    public void parseStandaloneXml(String dataSourcePath) throws IOException {

	File file = new File(dataSourcePath);
	Document document = document(file);
	NodeList nodeList = document.getElementsByTagName(DATA_SURCE_TAG);

	List<Properties> properties = getDataFromJBoss(nodeList);
	DataSourceInitializer initializer = new DataSourceInitializer();

	// Blocking semaphore before all data source initialization finished
	CountDownLatch dsLatch = new CountDownLatch(properties.size());

	BeanLoader.DataSourceParameters parameters;
	for (Properties props : properties) {
	    try {
		// Initializes and fills BeanLoader.DataSourceParameters class
		// to deploy data source
		parameters = new BeanLoader.DataSourceParameters();
		parameters.initializer = initializer;
		parameters.properties = props;
		parameters.dsLatch = dsLatch;
		BeanLoader.initializeDatasource(parameters);

	    } catch (IOException ex) {
		LOG.error("Could not initialize datasource", ex);
	    }
	}

	try {
	    dsLatch.await();
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	}

	DataSourceInitializer.setDsAsInitialized(dataSourcePath);
    }
}
