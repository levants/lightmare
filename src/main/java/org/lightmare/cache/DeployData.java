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
package org.lightmare.cache;

import java.net.URL;

import org.lightmare.utils.fs.FileType;

/**
 * Caches information about deployed file {@link URL} and {@link FileType} for
 * hot deployment processing
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class DeployData {

    // Deployed file type (jar, ear, directory and etc)
    private FileType type;

    // URL to appropriated deployed file
    private URL url;

    public FileType getType() {
	return type;
    }

    public void setType(FileType type) {
	this.type = type;
    }

    public URL getUrl() {
	return url;
    }

    public void setUrl(URL url) {
	this.url = url;
    }
}
