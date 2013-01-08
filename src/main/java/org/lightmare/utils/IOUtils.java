package org.lightmare.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import org.lightmare.jpa.datasource.FileParsers;
import org.lightmare.utils.earfile.DirUtils;
import org.lightmare.utils.earfile.EarUtils;
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

	protected Map<URL, URL> xmlURLs;

	protected Map<String, URL> xmlFiles;

	protected List<URL> libURLs;

	protected List<URL> ejbURLs;

	protected String path;

	protected File realFile;

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

	public Map<URL, URL> getXmlURLs() {
		if (xmlURLs == null) {
			xmlURLs = new HashMap<URL, URL>();
		}

		return xmlURLs;
	}

	public Map<String, URL> getXmlFiles() {
		if (xmlFiles == null) {
			xmlFiles = new HashMap<String, URL>();
		}

		return xmlFiles;
	}

	public List<URL> getLibURLs() {
		if (libURLs == null) {
			libURLs = new ArrayList<URL>();
		}

		return libURLs;
	}

	public List<URL> getEjbURLs() {
		if (ejbURLs == null) {
			ejbURLs = new ArrayList<URL>();
		}

		return ejbURLs;
	}

	public ZipFile getEarFile() throws IOException {
		if (earFile == null) {
			earFile = new ZipFile(path);
		}

		return earFile;
	}

	public static IOUtils getAppropriateType(URL url) throws URISyntaxException {
		IOUtils ioUtils = null;
		String path = url.getPath();
		File appFile = new File(url.toURI());
		if (appFile.isDirectory() && path.endsWith(".ear")) {
			ioUtils = new DirUtils(appFile);
		} else if (path.endsWith(".ear")) {
			ioUtils = new EarUtils(appFile);
		}

		return ioUtils;
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

	public Set<String> appXmlParser() throws IOException {
		InputStream stream = earReader();
		Set<String> jarNames = appXmlParser(stream);

		return jarNames;
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

	/**
	 * Scans project directory for class or jar files and persistence.xml (uses
	 * for development process)
	 * 
	 * @param files
	 * @throws MalformedURLException
	 */
	public void scanDirectory(File... files) throws MalformedURLException {
		File parentFile;
		if (files.length >= 1) {
			parentFile = files[0];
		} else {
			parentFile = realFile;
		}
		File[] subFiles = parentFile.listFiles();
		String fileName;
		URL fileURL;
		for (File subFile : subFiles) {
			fileName = subFile.getName();
			if (subFile.isDirectory()) {
				scanDirectory(subFile);
			} else if (fileName.endsWith(".jar") || fileName.endsWith(".class")) {
				fileURL = subFile.toURI().toURL();
				getEjbURLs().add(fileURL);
				getLibURLs().add(fileURL);
			} else if (fileName.equals("persistence.xml")) {
				fileURL = subFile.toURI().toURL();
				getXmlURLs().put(realFile.toURI().toURL(), fileURL);
			}
		}
	}

	public URL[] getLibs() {
		URL[] urls;
		if (libURLs == null) {
			urls = null;
		} else {
			urls = libURLs.toArray(new URL[libURLs.size()]);
		}

		return urls;
	}

	public URL[] getEjbs() {
		URL[] urls;
		if (ejbURLs == null) {
			urls = null;
		} else {
			urls = ejbURLs.toArray(new URL[ejbURLs.size()]);
		}

		return urls;
	}

	public URL[] getURLs() {

		List<URL> fullURLs = new ArrayList<URL>();
		URL[] urls;

		if (ejbURLs != null) {
			fullURLs.addAll(ejbURLs);
		}
		if (libURLs != null) {
			fullURLs.addAll(libURLs);
		}

		urls = fullURLs.toArray(new URL[fullURLs.size()]);

		return urls;
	}
}
