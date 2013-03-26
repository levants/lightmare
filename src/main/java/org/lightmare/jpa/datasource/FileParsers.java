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
import org.lightmare.ejb.startup.BeanLoader;
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
	properties.setProperty("driver", driverName);
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
	    NodeList userList = thisElement.getElementsByTagName("user-name");
	    int elementLength = userList.getLength();
	    if (elementLength == 0) {
		continue;
	    }
	    Element userElement = (Element) userList.item(0);
	    String user = getContext(userElement);

	    properties.setProperty("user", user);

	    NodeList passList = thisElement.getElementsByTagName("password");
	    elementLength = passList.getLength();
	    if (elementLength == 0) {
		continue;
	    }
	    Element passElement = (Element) passList.item(0);
	    String password = getContext(passElement);

	    properties.setProperty("password", password);
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
	    props.setProperty("name", thisElement.getAttribute("jndi-name"));
	    NodeList urlList = thisElement
		    .getElementsByTagName("connection-url");
	    int urlElementLength = urlList.getLength();
	    if (urlElementLength == 0) {
		continue;
	    }
	    Element urlElement = (Element) urlList.item(0);
	    String url = getContext(urlElement);
	    props.setProperty("url", url);
	    NodeList securityList = thisElement
		    .getElementsByTagName("security");
	    setDataFromJBossSecurity(securityList, props);

	    NodeList driverList = thisElement.getElementsByTagName("driver");
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
	NodeList nodeList = document.getElementsByTagName("datasource");

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
