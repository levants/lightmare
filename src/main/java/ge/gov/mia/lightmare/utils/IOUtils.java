package ge.gov.mia.lightmare.utils;

import ge.gov.mia.lightmare.jpa.datasource.FileParsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility class for checking jar, ear and zip files or ear and jar directories
 * from application server deployments (jboss) read contents and etc.
 * 
 * @author levan
 * 
 */
public abstract class IOUtils {

	protected Map<String, URL> xmlURLs;

	protected List<URL> libURLs;

	protected String path;

	private File realFile;

	protected ZipFile earFile;

	protected boolean isDirectory;

	public IOUtils(String path) {
		this.path = path;
		realFile = new File(path);
		isDirectory = realFile.isDirectory();
	}

	public IOUtils(File file) {
		this.path = file.getPath();
		realFile = file;
		isDirectory = realFile.isDirectory();
	}

	public IOUtils(URL url) throws URISyntaxException {
		this.path = url.toString();
		realFile = new File(url.toURI());
		isDirectory = realFile.isDirectory();
	}

	public Map<String, URL> getXmlURLs() {
		if (xmlURLs == null) {
			xmlURLs = new HashMap<String, URL>();
		}

		return xmlURLs;
	}

	public List<URL> getLibURLs() {
		if (libURLs == null) {
			libURLs = new ArrayList<URL>();
		}

		return libURLs;
	}

	public ZipFile getEarFile() throws IOException {
		if (earFile == null) {
			earFile = new ZipFile(path);
		}

		return earFile;
	}

	public Set<String> appXmlParser(InputStream xmlStream) throws IOException {
		try {
			Document document = FileParsers.parse(xmlStream);
			NodeList nodeList = document.getElementsByTagName("ejb");
			Set<String> ejbNames = new HashSet<String>();
			String ejbName;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element ejbElement = (Element) nodeList.item(i);
				ejbName = FileParsers.getContext(ejbElement);
				if (ejbName != null) {
					ejbNames.add(ejbName);
				}
			}

			return ejbNames;
		} finally {
			xmlStream.close();
		}
	}

	public abstract InputStream earReader() throws IOException;

	public void readEntries() throws IOException {
		InputStream xmlStream = earReader();
		Set<String> jarNames = appXmlParser(xmlStream);

		extractEjbJars(jarNames);
	}

	/**
	 * Gets {@link URL}s in {@link List} for ejb library files from ear
	 * {@link File}
	 * 
	 * @throws IOException
	 */
	public abstract void getEjbLibs() throws IOException;

	public abstract void extractEjbJars(Set<String> jarNames)
			throws IOException;

	public abstract boolean checkOnOrm(String jarName) throws IOException;

	public URL[] getLibs() {
		URL[] urls;
		if (libURLs == null) {
			urls = null;
		} else {
			urls = libURLs.toArray(new URL[libURLs.size()]);
		}

		return urls;
	}
}
