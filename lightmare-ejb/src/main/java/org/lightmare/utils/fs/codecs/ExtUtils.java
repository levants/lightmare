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
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.FileType;
import org.lightmare.utils.io.IOUtils;

/**
 * Implementation of {@link DirUtils} for ear files
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public class ExtUtils extends DirUtils {

    public static final FileType type = FileType.EAR;

    private File tmpFile;

    public ExtUtils(String path) {
	super(path);
    }

    public ExtUtils(File file) {
	super(file);
    }

    public ExtUtils(URL url) throws IOException {
	super(url);
    }

    @Override
    public FileType getType() {
	return type;
    }

    /**
     * Extracts ear file into the temporary file
     * 
     * @throws IOException
     */
    protected void exctractEar() throws IOException {

	tmpFile = File.createTempFile(realFile.getName(), StringUtils.EMPTY_STRING);
	tmpFile.delete();
	tmpFile.mkdir();
	addTmpFile(tmpFile);
	ZipFile zipFile = getEarFile();
	Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();
	while (zipFileEntries.hasMoreElements()) {
	    ZipEntry entry = zipFileEntries.nextElement();
	    exctractFile(entry);
	}
    }

    protected void exctractFile(ZipEntry entry) throws IOException {

	InputStream extStream = getEarFile().getInputStream(entry);
	File file = new File(tmpFile, entry.getName());
	File parrent = file.getParentFile();
	if (Boolean.FALSE.equals(parrent.exists())) {
	    parrent.mkdirs();
	    addTmpFile(parrent);
	}
	// Caches temporal files
	addTmpFile(file);
	if (Boolean.FALSE.equals(entry.isDirectory())) {
	    if (Boolean.FALSE.equals(file.exists())) {
		file.createNewFile();
	    }
	    OutputStream out = new FileOutputStream(file);
	    IOUtils.write(extStream, out);
	} else {
	    file.mkdir();
	}
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {
	exctractEar();
	super.realFile = tmpFile;
	super.path = tmpFile.getPath();
	super.isDirectory = tmpFile.isDirectory();
	super.scanArchive(args);
    }
}
