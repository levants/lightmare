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
	    String xmlPath = String.format("%s!/%s", currentURL.toString(),
		    ConfigLoader.XML_PATH);
	    URL xmlURL = new URL("jar", "", xmlPath);
	    getXmlFiles().put(realFile.getName(), xmlURL);
	    getXmlURLs().put(currentURL, xmlURL);
	}
    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {
	ZipFile zipFile = getEarFile();
	ZipEntry xmlEntry = zipFile.getEntry(ConfigLoader.XML_PATH);

	return xmlEntry != null;
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {
	if (args.length > 0) {
	    xmlFromJar = (Boolean) args[0];
	}
	extractEjbJars(Collections.<String> emptySet());
    }
}
