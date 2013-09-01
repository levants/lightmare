package org.lightmare.utils.earfile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.AbstractIOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link AbstractIOUtils} for ear directories
 * 
 * @author levan
 * 
 */
public class DirUtils extends AbstractIOUtils {

    public static final FileType type = FileType.EDIR;

    private static final String APPLICATION_XML_PATH = "META-INF/application.xml";

    public DirUtils(String path) {
	super(path);
    }

    public DirUtils(File file) {
	super(file);
    }

    public DirUtils(URL url) throws IOException {
	super(url);
    }

    @Override
    public FileType getType() {

	return type;
    }

    @Override
    public InputStream earReader() throws IOException {

	String appXmlPath;
	if (path.endsWith(File.pathSeparator)) {
	    appXmlPath = StringUtils.concat(path, APPLICATION_XML_PATH);
	} else {
	    appXmlPath = StringUtils.concat(path, APPLICATION_XML_PATH);
	}
	File xmlFile = new File(appXmlPath);
	InputStream stream = new FileInputStream(xmlFile);

	return stream;
    }

    private void fillLibs(File libDirectory) throws IOException {

	File[] libJars = libDirectory.listFiles(new FileFilter() {

	    @Override
	    public boolean accept(File jarFile) {

		return jarFile.getName().endsWith(JAR_FILE_EXT)
			&& !jarFile.isDirectory();
	    }
	});
	String jarPath;
	URL jarURL;
	if (ObjectUtils.available(libJars)) {
	    for (File libFile : libJars) {
		URL url = libFile.toURI().toURL();
		jarPath = StringUtils.concat(url.toString(), ARCHIVE_URL_DELIM,
			File.pathSeparator);
		jarURL = new URL(JAR, "", jarPath);
		getLibURLs().add(url);
		getLibURLs().add(jarURL);
	    }
	}
    }

    @Override
    public void getEjbLibs() throws IOException {

	File[] files = realFile.listFiles(new FileFilter() {

	    @Override
	    public boolean accept(File file) {

		return file.getName().endsWith("lib") && file.isDirectory();
	    }
	});
	if (ObjectUtils.available(files)) {
	    for (File libDirectory : files) {
		fillLibs(libDirectory);
	    }
	}
    }

    private JarFile extracted(String jarName) throws IOException {

	return new JarFile(jarName);
    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {

	JarEntry xmlEntry = extracted(jarName).getJarEntry(
		ConfigLoader.XML_PATH);

	return (xmlEntry != null);
    }

    @Override
    public void extractEjbJars(Set<String> jarNames) throws IOException {

	String xmlPath;
	if (path.endsWith(File.pathSeparator)) {
	    xmlPath = path;
	} else {
	    xmlPath = StringUtils.concat(path, File.pathSeparator);
	}

	String fillXmlPath;
	String jarPath;
	URL currentURL;
	boolean checkOnOrm;
	for (String jarName : jarNames) {
	    fillXmlPath = StringUtils.concat(xmlPath, jarName);
	    checkOnOrm = checkOnOrm(fillXmlPath);
	    currentURL = new File(fillXmlPath).toURI().toURL();
	    getEjbURLs().add(currentURL);
	    if (xmlFromJar && checkOnOrm) {
		jarPath = StringUtils.concat(currentURL.toString(),
			ARCHIVE_URL_DELIM, ConfigLoader.XML_PATH);
		URL jarURL = new URL(JAR, StringUtils.EMPTY_STRING, jarPath);
		getXmlFiles().put(jarName, jarURL);
		getXmlURLs().put(currentURL, jarURL);
	    }
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
