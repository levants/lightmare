/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link ArchiveUtils} for jar files
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public class JarUtils extends ArchiveUtils {

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

	if (CollectionUtils.valid(args)) {
	    xmlFromJar = (Boolean) CollectionUtils.getFirst(args);
	}

	extractEjbJars(Collections.<String> emptySet());
    }
}
