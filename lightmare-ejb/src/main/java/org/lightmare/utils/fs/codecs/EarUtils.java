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

import org.lightmare.jpa.XMLInitializer;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.FileType;
import org.lightmare.utils.io.IOUtils;

/**
 * Utility class for checking jar, ear and zip files read contents and etc.
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public class EarUtils extends ArchiveUtils {

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
     * Writes EJB jar {@link File} to temporal file to keep {@link URL} from
     * persistence.xml
     * 
     * @param entry
     * @return {@link URL}
     * @throws IOException
     */
    public URL extractEjbJar(ZipEntry entry) throws IOException {

	URL url;

	InputStream jarStream = getEarFile().getInputStream(entry);
	if (ObjectUtils.notNull(jarStream)) {
	    File tmpFile = File.createTempFile(UUID.randomUUID().toString(),
		    JAR_FILE_EXT);
	    tmpFile.deleteOnExit();
	    FileOutputStream output = new FileOutputStream(tmpFile);
	    IOUtils.write(jarStream, output);
	    URL jarURL = tmpFile.toURI().toURL();
	    String jarPath = StringUtils.concat(jarURL.toString(),
		    ARCHIVE_URL_DELIM, FILE_SEPARATOR, XMLInitializer.XML_PATH);
	    url = new URL(JAR, StringUtils.EMPTY_STRING, jarPath);
	} else {
	    url = null;
	}

	return url;
    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {

	boolean check = Boolean.FALSE;

	ZipFile zipFile = getEarFile();
	ZipEntry jarEntry = zipFile.getEntry(jarName);
	InputStream stream = zipFile.getInputStream(jarEntry);
	ZipInputStream zipStream = new ZipInputStream(stream);
	ZipEntry xmlEntry = zipStream.getNextEntry();

	while (ObjectUtils.notNull(xmlEntry) && Boolean.FALSE.equals(check)) {
	    check = xmlEntry.getName().equals(XMLInitializer.XML_PATH);
	    if (Boolean.FALSE.equals(check)) {
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
	if (path.endsWith(EAR_FILE_EXT) && Boolean.FALSE.equals(isDirectory)) {
	    getEjbLibs();
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
