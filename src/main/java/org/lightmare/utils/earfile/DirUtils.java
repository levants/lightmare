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
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link AbstractIOUtils} for ear directories
 * 
 * @author levan
 * 
 */
public class DirUtils extends AbstractIOUtils {

    public static final FileType type = FileType.EDIR;

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
    public InputStream earReader() throws IOException {

	String xmlPath = "META-INF/application.xml";
	String appXmlPath;
	if (path.endsWith("/")) {
	    appXmlPath = String.format("%s%s", path, xmlPath);
	} else {
	    appXmlPath = String.format("%s/%s", path, xmlPath);
	}
	File xmlFile = new File(appXmlPath);
	InputStream stream = new FileInputStream(xmlFile);

	return stream;
    }

    private void fillLibs(File libDirectory) throws IOException {

	File[] libJars = libDirectory.listFiles(new FileFilter() {

	    @Override
	    public boolean accept(File jarFile) {
		return jarFile.getName().endsWith(".jar")
			&& !jarFile.isDirectory();
	    }
	});
	String jarPath;
	URL jarURL;
	if (libJars != null) {
	    for (File libFile : libJars) {
		URL url = libFile.toURI().toURL();
		jarPath = String.format("%s!/", url.toString());
		jarURL = new URL("jar", "", jarPath);
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
	if (files != null) {
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
	if (path.endsWith("/")) {
	    xmlPath = path;
	} else {
	    xmlPath = String.format("%s/", path);
	}

	String fillXmlPath;
	String jarPath;
	URL currentURL;
	boolean checkOnOrm;
	for (String jarName : jarNames) {
	    fillXmlPath = String.format("%s%s", xmlPath, jarName);
	    checkOnOrm = checkOnOrm(fillXmlPath);
	    currentURL = new File(fillXmlPath).toURI().toURL();
	    getEjbURLs().add(currentURL);
	    if (xmlFromJar && checkOnOrm) {
		jarPath = String.format("%s!/%s", currentURL.toString(),
			ConfigLoader.XML_PATH);
		URL jarURL = new URL("jar", "", jarPath);
		getXmlFiles().put(jarName, jarURL);
		getXmlURLs().put(currentURL, jarURL);
	    }
	}

    }

    @Override
    protected void scanArchive(Object... args) throws IOException {
	if (args.length > 0) {
	    xmlFromJar = (Boolean) args[0];
	}

	getEjbLibs();
	Set<String> appNames = appXmlParser();
	extractEjbJars(appNames);
    }
}
