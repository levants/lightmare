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
package org.lightmare.utils.fs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.lightmare.cache.DeployData;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Utility class for directory watch service
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class WatchUtils {

    /**
     * Clears passed path
     * 
     * @param path
     * @return {@link String}
     */
    public static String clearPath(String path) {

	String cleanPath;

	if (path.endsWith(File.separator)) {
	    int from = CollectionUtils.FIRST_INDEX;
	    int to = path.length() - CollectionUtils.SECOND_INDEX;
	    cleanPath = path.substring(from, to);
	} else {
	    cleanPath = path;
	}

	return cleanPath;
    }

    /**
     * Clears path in passed {@link URL} instance
     * 
     * @param url
     * @return {@link URL}
     * @throws IOException
     */
    public static URL clearURL(URL url) throws IOException {

	URL normURL;

	String path = url.toString();
	if (path.endsWith(File.separator)) {
	    int from = CollectionUtils.FIRST_INDEX;
	    int to = path.length() - CollectionUtils.SECOND_INDEX;
	    path = path.substring(from, to);
	}
	normURL = new URL(path);

	return normURL;
    }

    /**
     * Checks passed {@link DeployData} on watch service
     * 
     * @param deployData
     * @return <code>boolean</code>
     */
    public static boolean checkForWatch(DeployData deployData) {

	boolean check = ObjectUtils.notNull(deployData);

	if (check) {

	    URL url = deployData.getUrl();
	    FileType fileType = deployData.getType();
	    check = ObjectUtils.notNull(url) && ObjectUtils.notNull(fileType);
	    if (check) {
		check = fileType.equals(FileType.JAR)
			|| fileType.equals(FileType.EAR)
			|| fileType.equals(FileType.EDIR);
	    }
	}

	return check;
    }
}
