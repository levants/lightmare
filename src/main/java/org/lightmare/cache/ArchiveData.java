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
package org.lightmare.cache;

import org.lightmare.utils.fs.codecs.ArchiveUtils;

/**
 * Container class for {@link org.lightmare.utils.fs.codecs.ArchiveUtils} files
 * container and {@link ClassLoader} associated with this files for each
 * {@link java.net.URL} to cache and avoid duplicates at deploy time.
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 * 
 * @see org.lightmare.deploy.MetaCreator
 */
public class ArchiveData {

    private ArchiveUtils ioUtils;

    private ClassLoader loader;

    public ArchiveUtils getIoUtils() {
	return ioUtils;
    }

    public void setIoUtils(ArchiveUtils ioUtils) {
	this.ioUtils = ioUtils;
    }

    public ClassLoader getLoader() {
	return loader;
    }

    public void setLoader(ClassLoader loader) {
	this.loader = loader;
    }
}
