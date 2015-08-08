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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lightmare.deploy.BeanLoader;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 *
 * @author Levan Tsinadze
 * @since 0.0.45
 * @see org.lightmare.deploy.BeanLoader#removeResources(List)
 * @see org.lightmare.utils.finalizers.ShutDown
 */
public class TmpResources {

    // Cache for all temporal files used at deployment time
    private Set<List<File>> tmpFiles = new HashSet<List<File>>();

    /**
     * Caches passed collection of temporal files
     *
     * @param files
     */
    public void addFile(List<File> files) {

	for (File file : files) {
	    file.deleteOnExit();
	}
	// Caches temporal files
	tmpFiles.add(files);
    }

    /**
     * Deletes all temporal files used for deployment
     *
     * @throws IOException
     */
    public void removeTempFiles() throws IOException {

	for (List<File> files : tmpFiles) {
	    BeanLoader.removeResources(files);
	}
	// Clears temporal files cache
	tmpFiles.clear();
    }

    /**
     * Gets size of cached temporal files
     *
     * @return <code>int</code>
     */
    public int size() {
	return tmpFiles.size();
    }
}
