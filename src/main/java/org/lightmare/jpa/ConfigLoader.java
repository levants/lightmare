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
package org.lightmare.jpa;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;

/**
 * For getting XML resources and resources from persistence.xml path
 * 
 * @author Levan Tsinadze
 * @since 0.0.16-SNAPSHOT
 */
public class ConfigLoader {

    // Short path for ORM configuration file
    private String shortPath;

    // Path for ORM configuration file
    public static final String XML_PATH = "META-INF/persistence.xml";

    // Error messages
    private static final String PATH_NOT_PROVIDED_ERROR = "Path is not provided";
    private static final String COULD_NOT_FIND_ERROR = "Could not find persistence.xml file on path ";

    public String getShortPath() {
	return shortPath;
    }

    /**
     * Converts passed to {@link Enumeration} to build persistence configuration
     * 
     * @see Ejb3ConfigurationImpl#configure(String, java.util.Map)
     * 
     * @param path
     * @return Enumeration<{@link URL}>
     * @throws IOException
     */
    public List<URL> readURL(final URL url) {

	List<URL> xmls = new ArrayList<URL>();

	shortPath = StringUtils
		.concat(ArchiveUtils.ARCHIVE_URL_DELIM, XML_PATH);

	return xmls;
    }

    /**
     * Reads {@link URL} from passed path {@link String} for build persistence
     * configuration
     * 
     * @see Ejb3ConfigurationImpl#configure(String, java.util.Map)
     * 
     * @param path
     * @return Enumeration<{@link URL} >
     * @throws IOException
     */
    public List<URL> readFile(String path) throws IOException {

	List<URL> xmls;

	if (path == null || path.isEmpty()) {
	    throw new IOException(PATH_NOT_PROVIDED_ERROR);
	}

	File file = new File(path);
	if (ObjectUtils.notTrue(file.exists())) {
	    throw new IOException(
		    StringUtils.concat(COULD_NOT_FIND_ERROR, path));
	}
	shortPath = file.getName();
	final URL url = file.toURI().toURL();
	xmls = readURL(url);

	return xmls;
    }
}
