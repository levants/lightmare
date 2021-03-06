/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.utils.fs.codecs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.lightmare.jpa.XMLInitializer;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link ArchiveUtils} for ear directories
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public class DirUtils extends ArchiveUtils {

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
    public FileType getType() {
	return type;
    }

    @Override
    public InputStream earReader() throws IOException {

	InputStream stream;

	String appXmlPath;
	if (path.endsWith(FILE_SEPARATOR)) {
	    appXmlPath = StringUtils.concat(path, APPLICATION_XML_PATH);
	} else {
	    appXmlPath = StringUtils.concat(path, FILE_SEPARATOR,
		    APPLICATION_XML_PATH);
	}

	File xmlFile = new File(appXmlPath);
	stream = new FileInputStream(xmlFile);

	return stream;
    }

    private void fillLibs(File libDirectory) throws IOException {

	File[] libJars = libDirectory.listFiles(new FileFilter() {

	    @Override
	    public boolean accept(File jarFile) {
		return jarFile.getName().endsWith(JAR_FILE_EXT)
			&& Boolean.FALSE.equals(jarFile.isDirectory());
	    }
	});

	String jarPath;
	URL jarURL;
	if (CollectionUtils.valid(libJars)) {

	    for (File libFile : libJars) {
		URL url = libFile.toURI().toURL();
		jarPath = StringUtils.concat(url.toString(), ARCHIVE_URL_DELIM,
			FILE_SEPARATOR);
		jarURL = new URL(JAR, StringUtils.EMPTY_STRING, jarPath);
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
		return file.getName().endsWith(LIB) && file.isDirectory();
	    }
	});

	if (CollectionUtils.valid(files)) {
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
		XMLInitializer.XML_PATH);

	return ObjectUtils.notNull(xmlEntry);
    }

    @Override
    public void extractEjbJars(Set<String> jarNames) throws IOException {

	String xmlPath;
	if (path.endsWith(FILE_SEPARATOR)) {
	    xmlPath = path;
	} else {
	    xmlPath = StringUtils.concat(path, FILE_SEPARATOR);
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
			ARCHIVE_URL_DELIM, FILE_SEPARATOR,
			XMLInitializer.XML_PATH);
		URL jarURL = new URL(JAR, StringUtils.EMPTY_STRING, jarPath);
		getXmlFiles().put(jarName, jarURL);
		getXmlURLs().put(currentURL, jarURL);
	    }
	}
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {

	if (CollectionUtils.valid(args)) {
	    xmlFromJar = (Boolean) CollectionUtils.getFirst(args);
	}

	getEjbLibs();
	Set<String> appNames = appXmlParser();
	extractEjbJars(appNames);
    }
}
