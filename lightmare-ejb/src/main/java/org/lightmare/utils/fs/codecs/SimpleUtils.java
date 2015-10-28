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
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link ArchiveUtils} for directories
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public class SimpleUtils extends ArchiveUtils {

    public static final FileType type = FileType.DIR;

    public SimpleUtils(File file) {
	super(file);
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

    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {
	return Boolean.FALSE;
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {

	getEjbURLs().add(realFile.toURI().toURL());
    }
}
