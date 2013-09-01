package org.lightmare.utils.earfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.AbstractIOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Utility class for checking jar, ear and zip files read contents and etc.
 * 
 * @author levan
 * 
 */
public class EarUtils extends AbstractIOUtils {

    public EarUtils(String path) {
	super(path);
    }

    public EarUtils(File file) {
	super(file);
    }

    public EarUtils(URL url) throws IOException {
	super(url);
    }

    @Override
    public FileType getType() {

	return FileType.EAR;
    }

    @Override
    public InputStream earReader() throws IOException {

	InputStream xmlStream;
	ZipFile zipFile = getEarFile();
	ZipEntry entry = zipFile.getEntry(APPLICATION_XML_PATH);
	if (entry == null) {
	    xmlStream = null;
	} else {
	    xmlStream = zipFile.getInputStream(entry);
	}

	return xmlStream;
    }

    @Override
    public void getEjbLibs() throws IOException {

	URL earURL = realFile.toURI().toURL();

	Enumeration<? extends ZipEntry> entries = getEarFile().entries();
	String earPath;
	ZipEntry libEntry;
	String libPath;
	while (entries.hasMoreElements()) {
	    libEntry = entries.nextElement();
	    libPath = libEntry.toString();
	    if ((libPath.startsWith(LIB_WITH_DELIM) && ObjectUtils
		    .notTrue(libPath.endsWith(LIB_WITH_DELIM)))
		    || libPath.endsWith(JAR_FILE_EXT)) {
		earPath = StringUtils.concat(earURL.toString(),
			ARCHIVE_URL_DELIM, FILE_SEPARATOR, libPath);
		URL url = new URL(JAR, StringUtils.EMPTY_STRING, earPath);
		getLibURLs().add(url);
	    }
	}
    }

    /**
     * Writes ejb jar {@link File} to temporal file to keep {@link URL} from
     * persistence.xml
     * 
     * @param entry
     * @return {@link URL}
     * @throws IOException
     */
    public URL extractEjbJar(ZipEntry entry) throws IOException {

	URL url = null;
	InputStream jarStream = getEarFile().getInputStream(entry);
	if (ObjectUtils.notNull(jarStream)) {
	    File tmpFile = File.createTempFile(UUID.randomUUID().toString(),
		    JAR_FILE_EXT);
	    tmpFile.deleteOnExit();
	    FileOutputStream output = new FileOutputStream(tmpFile);
	    write(jarStream, output);
	    URL jarURL = tmpFile.toURI().toURL();
	    String jarPath = StringUtils.concat(jarURL.toString(),
		    ARCHIVE_URL_DELIM, FILE_SEPARATOR, ConfigLoader.XML_PATH);
	    url = new URL(JAR, StringUtils.EMPTY_STRING, jarPath);
	}

	return url;
    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {

	ZipFile zipFile = getEarFile();
	ZipEntry jarEntry = zipFile.getEntry(jarName);
	InputStream stream = zipFile.getInputStream(jarEntry);
	ZipInputStream zipStream = new ZipInputStream(stream);
	ZipEntry xmlEntry = zipStream.getNextEntry();
	boolean check = Boolean.FALSE;
	while (ObjectUtils.notNull(xmlEntry) && ObjectUtils.notTrue(check)) {
	    check = xmlEntry.getName().equals(ConfigLoader.XML_PATH);
	    if (ObjectUtils.notTrue(check)) {
		xmlEntry = zipStream.getNextEntry();
	    }
	}

	return check;
    }

    @Override
    public void extractEjbJars(Set<String> jarNames) throws IOException {

	ZipFile earFile = getEarFile();
	URL url;
	ZipEntry jarEntry;
	String earPath = realFile.toURI().toURL().toString();
	String jarPath;
	URL jarURL;
	boolean checkOnOrm;
	for (String jarName : jarNames) {
	    checkOnOrm = checkOnOrm(jarName);
	    jarPath = StringUtils.concat(earPath, ARCHIVE_URL_DELIM,
		    FILE_SEPARATOR, jarName);
	    jarURL = new URL(JAR, StringUtils.EMPTY_STRING, jarPath);
	    getEjbURLs().add(jarURL);
	    if (xmlFromJar && checkOnOrm) {
		jarEntry = earFile.getEntry(jarName);
		url = extractEjbJar(jarEntry);
		getXmlFiles().put(jarName, url);
		getXmlURLs().put(jarURL, url);
	    }
	}
    }

    public void checkFile() throws IOException {

	if (path.endsWith(EAR_FILE_EXT) && ObjectUtils.notTrue(isDirectory)) {
	    getEjbLibs();
	}
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {

	if (ObjectUtils.available(args)) {
	    xmlFromJar = (Boolean) ObjectUtils.getFirst(args);
	}

	getEjbLibs();
	Set<String> appNames = appXmlParser();
	extractEjbJars(appNames);
    }
}
