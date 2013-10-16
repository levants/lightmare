package org.lightmare.utils.fs.codecs;

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
import org.lightmare.scannotation.AnnotationDB;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.IOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.fs.FileType;
import org.lightmare.utils.fs.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility class for checking jar, ear and zip files or ear and jar directories
 * from application server deployments (jboss) read contents and etc.
 * 
 * @author levan
 * @since 0.0.83-SNAPSHOT
 */
public abstract class ArchiveUtils {

    protected Map<URL, URL> xmlURLs;

    protected Map<String, URL> xmlFiles;

    protected List<URL> libURLs;

    protected List<URL> ejbURLs;

    protected String path;

    protected File realFile;

    protected ZipFile earFile;

    protected List<File> tmpFiles;

    protected boolean isDirectory;

    protected boolean xmlFromJar;

    protected boolean executed;

    // Cached file types and extensions
    public static final String JAR = "jar";

    public static final String JAR_FILE_EXT = ".jar";

    public static final String EAR = "ear";

    public static final String EAR_FILE_EXT = ".ear";

    public static final String CLASS_FILE_EXT = ".class";

    public static final String LIB = "lib";

    public static final String LIB_WITH_DELIM = "lib/";

    public static final String PERSISTENCE_XML = "persistence.xml";

    public static final String APPLICATION_XML_PATH = "META-INF/application.xml";

    public static final char ARCHIVE_URL_DELIM = '!';

    public static final String FILE_SEPARATOR = File.separator;

    // Application descriptor XML file tags
    public static final String EJB_TAG_NAME = "ejb";

    public ArchiveUtils(String path) {
	this.path = path;
	realFile = new File(path);
	isDirectory = realFile.isDirectory();
    }

    public ArchiveUtils(File file) {
	this.path = file.getPath();
	realFile = file;
	isDirectory = realFile.isDirectory();
    }

    public ArchiveUtils(URL url) throws IOException {

	this.path = url.toString();

	try {
	    realFile = new File(url.toURI());
	} catch (URISyntaxException ex) {
	    throw new IOException(ex);
	}

	isDirectory = realFile.isDirectory();
    }

    /**
     * Gets files type
     * 
     * @return {@link FileType}
     */
    public abstract FileType getType();

    /**
     * Ensures that all temporary files will be removed at finish of program
     * 
     * @param file
     */
    protected void ensureTmpFile(File file) {

	file.deleteOnExit();
    }

    public boolean isExecuted() {
	return executed;
    }

    public boolean notExecuted() {
	return ObjectUtils.notTrue(executed);
    }

    public void setXmlFromJar(boolean xmlFromJar) {

	this.xmlFromJar = xmlFromJar;
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

    /**
     * Gets appropriated {@link FileType} for passed {@link File} instance
     * 
     * @param appFile
     * @return {@link FileType}
     */
    private static FileType getType(File appFile) {

	FileType fileType;

	String appPath = appFile.getPath();
	if (appFile.isDirectory() && appPath.endsWith(EAR_FILE_EXT)) {
	    fileType = FileType.EDIR;
	} else if (appPath.endsWith(EAR_FILE_EXT)) {
	    fileType = FileType.EAR;
	} else if (appPath.endsWith(JAR_FILE_EXT)) {
	    fileType = FileType.JAR;
	} else {
	    boolean isEarDir = FileUtils.checkOnEarDir(appFile);
	    if (isEarDir) {
		fileType = FileType.EDIR;
	    } else {
		fileType = FileType.DIR;
	    }
	}

	return fileType;
    }

    /**
     * Gets appropriated {@link ArchiveUtils} implementation class instance for
     * passed {@link URL} and {@link FileType} parameters
     * 
     * @param url
     * @param fileType
     * @return {@link ArchiveUtils}
     * @throws IOException
     */
    public static ArchiveUtils getAppropriatedType(URL url, FileType fileType)
	    throws IOException {

	ArchiveUtils ioUtils;

	File appFile;
	try {
	    appFile = new File(url.toURI());
	} catch (URISyntaxException ex) {
	    throw new IOException(ex);
	}

	FileType typToCheck = fileType;

	if (fileType == null) {
	    typToCheck = getType(appFile);
	}

	if (typToCheck.equals(FileType.EDIR)) {
	    ioUtils = new DirUtils(appFile);
	} else if (typToCheck.equals(FileType.EAR)) {
	    ioUtils = new ExtUtils(appFile);
	} else if (typToCheck.equals(FileType.JAR)) {
	    ioUtils = new JarUtils(appFile);
	} else if (typToCheck.equals(FileType.DIR)) {
	    ioUtils = new SimpleUtils(appFile);
	} else {
	    ioUtils = null;
	}

	return ioUtils;
    }

    /**
     * Gets appropriated {@link ArchiveUtils} implementation for passed
     * {@link URL} parameter
     * 
     * @param url
     * @return {@link ArchiveUtils}
     * @throws IOException
     */
    public static ArchiveUtils getAppropriatedType(URL url) throws IOException {

	ArchiveUtils ioUtils = getAppropriatedType(url, null);

	return ioUtils;
    }

    /**
     * Finds persistence.xml {@link URL} by class name
     * 
     * @param classOwnersFiles
     * @param className
     * @return {@link URL}
     */
    public URL getAppropriatedURL(Map<String, String> classOwnersFiles,
	    String className) {

	URL xmlURL;

	String jarName = classOwnersFiles.get(className);
	if (jarName == null || jarName.isEmpty()) {
	    xmlURL = null;
	} else {
	    xmlURL = getXmlFiles().get(jarName);
	}

	return xmlURL;
    }

    /**
     * Finds persistence.xml {@link URL} by class name
     * 
     * @param annotationDB
     * @param className
     * @return {@link URL}
     */
    public URL getAppropriatedURL(AnnotationDB annotationDB, String className) {

	URL xmlURL;

	Map<String, String> classOwnersFiles = annotationDB
		.getClassOwnersFiles();
	xmlURL = getAppropriatedURL(classOwnersFiles, className);

	return xmlURL;
    }

    public Set<String> appXmlParser(InputStream xmlStream) throws IOException {

	Set<String> ejbNames = new HashSet<String>();

	try {
	    Document document = FileParsers.parse(xmlStream);
	    NodeList nodeList = document.getElementsByTagName(EJB_TAG_NAME);
	    String ejbName;

	    for (int i = 0; i < nodeList.getLength(); i++) {

		Element ejbElement = (Element) nodeList.item(i);
		ejbName = FileParsers.getContext(ejbElement);

		if (ObjectUtils.notNull(ejbName)) {
		    ejbNames.add(ejbName);
		}
	    }
	} finally {
	    IOUtils.close(xmlStream);
	}

	return ejbNames;
    }

    public Set<String> appXmlParser() throws IOException {

	Set<String> jarNames;

	InputStream stream = earReader();
	jarNames = appXmlParser(stream);

	return jarNames;
    }

    public abstract InputStream earReader() throws IOException;

    public void readEntries() throws IOException {

	InputStream xmlStream = earReader();
	Set<String> jarNames = appXmlParser(xmlStream);

	extractEjbJars(jarNames);
    }

    /**
     * Gets {@link URL}s in {@link List} for EJB library files from ear
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

	if (CollectionUtils.valid(files)) {
	    parentFile = CollectionUtils.getFirst(files);
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
	    } else if (fileName.endsWith(JAR_FILE_EXT)
		    || fileName.endsWith(CLASS_FILE_EXT)) {
		fileURL = subFile.toURI().toURL();
		getEjbURLs().add(fileURL);
		getLibURLs().add(fileURL);
	    } else if (fileName.equals(PERSISTENCE_XML)) {
		fileURL = subFile.toURI().toURL();
		getXmlURLs().put(realFile.toURI().toURL(), fileURL);
	    }
	}
    }

    protected abstract void scanArchive(Object... args) throws IOException;

    public void scan(Object... args) throws IOException {

	scanArchive(args);
	executed = Boolean.TRUE;
    }

    public URL[] getLibs() {

	URL[] urls;

	if (libURLs == null) {
	    urls = null;
	} else {
	    urls = CollectionUtils.toArray(libURLs, URL.class);
	}

	return urls;
    }

    public URL[] getEjbs() {

	URL[] urls;

	if (ejbURLs == null) {
	    urls = null;
	} else {
	    urls = CollectionUtils.toArray(ejbURLs, URL.class);
	}

	return urls;
    }

    public URL[] getURLs() {

	URL[] urls;

	List<URL> fullURLs = new ArrayList<URL>();

	if (ObjectUtils.notNull(ejbURLs)) {
	    fullURLs.addAll(ejbURLs);
	}
	if (ObjectUtils.notNull(libURLs)) {
	    fullURLs.addAll(libURLs);
	}

	urls = CollectionUtils.toArray(fullURLs, URL.class);

	return urls;
    }

    protected List<File> getForAddTmpFiles() {

	if (tmpFiles == null) {
	    tmpFiles = new ArrayList<File>();
	}

	return tmpFiles;
    }

    /**
     * Saves temporary files at cache
     * 
     * @param tmpFile
     */
    protected void addTmpFile(File tmpFile) {

	ensureTmpFile(tmpFile);
	getForAddTmpFiles().add(tmpFile);
    }

    public List<File> getTmpFiles() {
	return tmpFiles;
    }
}
