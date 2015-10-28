package org.lightmare.utils.io.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class for parsing and reading XML file
 * 
 * @author Levan Tsinadze
 *
 */
public class XMLUtils {

    /**
     * Reads passed {@link File} as {@link Document} object
     * 
     * @param file
     * @return {@link Document}
     * @throws IOException
     */
    public static Document document(File file) throws IOException {
	return document(file.toURI().toURL());
    }

    /**
     * Initializes {@link DocumentBuilder} from passed {@link InputStream} of
     * XML document's file
     * 
     * @param stream
     * @return {@link DocumentBuilder} initialized from {@link InputStream}
     * @throws ParserConfigurationException
     */
    private static DocumentBuilder initDocumentBuilder(InputStream stream)
	    throws ParserConfigurationException {

	DocumentBuilder builder;

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	builder = factory.newDocumentBuilder();

	return builder;
    }

    /**
     * Parses XML document to initialize {@link javax.sql.DataSource}s
     * configuration properties
     * 
     * @param stream
     * @return {@link Document}
     * @throws IOException
     */
    public static Document parse(InputStream stream) throws IOException {

	Document document;

	try {
	    DocumentBuilder builder = initDocumentBuilder(stream);
	    document = builder.parse(stream);
	} catch (ParserConfigurationException ex) {
	    throw new IOException(ex);
	} catch (SAXException ex) {
	    throw new IOException(ex);
	}

	return document;
    }

    /**
     * Reads passed {@link URL} as {@link Document} object
     * 
     * @param url
     * @return Document
     * @throws IOException
     */
    public static Document document(URL url) throws IOException {

	Document document;

	URLConnection connection = url.openConnection();
	InputStream stream = connection.getInputStream();
	try {
	    document = parse(stream);
	} finally {
	    IOUtils.close(stream);
	}

	return document;
    }

    /**
     * Validates passed {@link NodeList} length
     * 
     * @param nodeList
     * @return
     */
    public static boolean validate(NodeList nodeList) {

	boolean valid;

	int elementLength = nodeList.getLength();
	valid = ObjectUtils.notEquals(elementLength,
		CollectionUtils.EMPTY_ARRAY_LENGTH);

	return valid;
    }

    /**
     * Gets item with first index from passed {@link NodeList} instance
     * 
     * @param list
     * @return {@link Node}
     */
    public static Node getFirst(NodeList list) {
	return list.item(CollectionUtils.FIRST_INDEX);
    }

    /**
     * Gets and casts first element from {@link NodeList} instance
     * 
     * @param nodeList
     * @return {@link Element}
     */
    public static Element getFirstElement(NodeList nodeList) {

	Element element;

	Node node = getFirst(nodeList);
	element = ObjectUtils.cast(node, Element.class);

	return element;
    }

    /**
     * Gets and casts i'th element from {@link NodeList} instance
     * 
     * @param nodeList
     * @param i
     * @return {@link Element}
     */
    public static Element getElement(NodeList nodeList, int i) {

	Element element;

	Node node = nodeList.item(i);
	element = ObjectUtils.cast(node, Element.class);

	return element;
    }

    /**
     * To get text from tag depended on JRE installation
     * 
     * @param element
     * @return {@link String}
     */
    public static String getContext(Element element) {

	String data;

	NodeList textList = element.getChildNodes();
	Node firstNode = getFirst(textList);
	data = firstNode.getNodeValue().trim();

	return data;
    }
}
