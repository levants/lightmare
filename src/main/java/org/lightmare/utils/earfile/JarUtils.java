package org.lightmare.utils.earfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.AbstractIOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link AbstractIOUtils} for jar files
 * 
 * @author levan
 * 
 */
public class JarUtils extends AbstractIOUtils {

    public static final FileType type = FileType.JAR;

    public JarUtils(String path) {
	super(path);
    }

    public JarUtils(File file) {
	super(file);
    }

    public JarUtils(URL url) throws IOException {
	super(url);
    }

    @Override
    public FileType getType() {

	return type;
    }

    @Override
    public InputStream earReader() throws IOException {
	return null;
    }

    @Override
    public void getEjbLibs() throws IOException {

    }

    @Override
    public void extractEjbJars(Set<String> jarNames) throws IOException {

	URL currentURL = realFile.toURI().toURL();
	getEjbURLs().add(currentURL);

	boolean checkOnOrm = checkOnOrm(path);
	if (xmlFromJar && checkOnOrm) {
	    String xmlPath = StringUtils.concat(currentURL.toString(),
		    ARCHIVE_URL_DELIM, ConfigLoader.XML_PATH);
	    URL xmlURL = new URL(JAR, StringUtils.EMPTY_STRING, xmlPath);
	    getXmlFiles().put(realFile.getName(), xmlURL);
	    getXmlURLs().put(currentURL, xmlURL);
	}
    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {
	ZipFile zipFile = getEarFile();
	ZipEntry xmlEntry = zipFile.getEntry(ConfigLoader.XML_PATH);

	return ObjectUtils.notNull(xmlEntry);
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {
	if (ObjectUtils.available(args)) {
	    xmlFromJar = (Boolean) ObjectUtils.getFirst(args);
	}
	extractEjbJars(Collections.<String> emptySet());
    }
}
