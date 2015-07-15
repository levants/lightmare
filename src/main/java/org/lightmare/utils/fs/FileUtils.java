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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;

/**
 * Utility for removing {@link File}s recursively from file system
 *
 * @author Levan Tsinadze
 * @since 0.0.20-SNAPSHOT
 */
public class FileUtils {

    // First bytes for ZIP file
    private static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

    // Read privilege for random access file
    private static final String READ = "r";

    private static final Logger LOG = Logger.getLogger(FileUtils.class);

    /**
     * Lists java archive class files in passed file
     *
     * @param file
     * @return {@link File}[]
     */
    private static File[] listJavaFiles(File file) {

	File[] subFiles = file.listFiles(new FilenameFilter() {

	    private boolean isJavaFile(String name) {
		return (name.endsWith(ArchiveUtils.JAR_FILE_EXT) || name.endsWith(ArchiveUtils.CLASS_FILE_EXT));
	    }

	    @Override
	    public boolean accept(File file, String name) {
		return (isJavaFile(name) || file.isDirectory());
	    }
	});

	return subFiles;
    }

    /**
     * Adds passed {@link File}'s {@link URL} to passed {@link Collection} of
     * {@link URL} objects
     *
     * @param urls
     * @param file
     * @throws IOException
     */
    private static void addURL(Collection<URL> urls, File file) throws IOException {

	try {
	    urls.add(file.toURI().toURL());
	} catch (MalformedURLException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Adds sub files of passed {@link File} array to passed {@link List} of
     * {@link URL} objects
     *
     * @param files
     * @param urls
     * @throws IOException
     */
    private static void addSubDirectory(File[] files, Set<URL> urls) throws IOException {

	for (File subFile : files) {
	    if (subFile.isDirectory()) {
		getSubfiles(subFile, urls);
	    } else {
		addURL(urls, subFile);
	    }
	}
    }

    /**
     * Gets all jar or class subfiles from specified {@link File} recursively
     *
     * @param file
     * @param urls
     * @throws IOException
     */
    public static void getSubfiles(File file, Set<URL> urls) throws IOException {

	if (file.isDirectory()) {
	    File[] subFiles = listJavaFiles(file);
	    if (CollectionUtils.valid(subFiles)) {
		addSubDirectory(subFiles, urls);
	    }
	} else {
	    addURL(urls, file);
	}
    }

    /**
     * Check whether passed {@link URL} is from extracted ear directory
     *
     * @param url
     * @return boolean
     * @throws IOException
     */
    public static boolean checkOnEarDir(URL url) throws IOException {

	boolean isEarDir;

	try {
	    File file = new File(url.toURI());
	    isEarDir = checkOnEarDir(file);
	} catch (URISyntaxException ex) {
	    throw new IOException(ex);
	}

	return isEarDir;
    }

    /**
     * Check whether passed path is extracted ear directory path
     *
     * @param file
     * @return boolean
     */
    public static boolean checkOnEarDir(String path) {

	boolean isEarDir;

	File file = new File(path);
	isEarDir = checkOnEarDir(file);

	return isEarDir;
    }

    /**
     * Initializes file path suffix
     *
     * @param path
     * @return {@link String} file suffix
     */
    private static String getDeliminator(String path) {

	String delim;

	if (path.endsWith(ArchiveUtils.FILE_SEPARATOR)) {
	    delim = StringUtils.EMPTY_STRING;
	} else {
	    delim = ArchiveUtils.FILE_SEPARATOR;
	}

	return delim;
    }

    /**
     * Check whether passed file is extracted ear directory
     *
     * @param file
     * @return boolean
     */
    public static boolean checkOnEarDir(File file) {

	boolean isEarDir = file.isDirectory();

	if (isEarDir) {
	    File[] files = file.listFiles();
	    isEarDir = CollectionUtils.valid(files);
	    if (isEarDir) {
		String path = file.getPath();
		String delim = getDeliminator(path);
		String xmlPath = ArchiveUtils.APPLICATION_XML_PATH;
		String appxmlPath = StringUtils.concat(path, delim, xmlPath);
		File appXmlFile = new File(appxmlPath);
		isEarDir = appXmlFile.exists();
	    }
	}

	return isEarDir;
    }

    /**
     * Deletes directory content
     *
     * @param file
     */
    private static void deleteSubFiles(File file) {

	File[] subFiles = file.listFiles();
	if (CollectionUtils.valid(subFiles)) {
	    for (File subFile : subFiles) {
		deleteFile(subFile);
	    }
	}
    }

    /**
     * Checks if passed file is directory then deletes it's content
     *
     * @param file
     */
    private static void checkAndDelete(File file) {

	if (file.isDirectory()) {
	    deleteSubFiles(file);
	}
    }

    /**
     * Removes passed {@link File}s from file system and if
     * {@link File#isDirectory()} removes all it's content recursively
     *
     * @param file
     * @return boolean
     */
    public static boolean deleteFile(File file) {
	checkAndDelete(file);
	return file.delete();
    }

    /**
     * Iterates over passed {@link File}s and removes each of them from file
     * system and if {@link File#isDirectory()} removes all it's content
     * recursively
     *
     * @param files
     */
    public static void deleteFiles(Iterable<File> files) {

	for (File fileToDelete : files) {
	    deleteFile(fileToDelete);
	}
    }

    /**
     * Converts passed file to {@link URL} instance
     *
     * @param file
     * @return {@link URL}
     * @throws IOException
     */
    public static URL toURL(File file) throws IOException {
	return file.toURI().toURL();
    }

    /**
     * Creates {@link URL} from passed path
     *
     * @param path
     * @return {@link URL}
     * @throws IOException
     */
    public static URL toURL(String path) throws IOException {
	File file = new File(path);
	return toURL(file);
    }

    /**
     * Checks passed path and if its empty path for current class directory
     *
     * @param path
     * @return {@link String}
     */
    public static List<URL> toURLWithClasspath(String path) throws IOException {

	List<URL> urls = new ArrayList<URL>();

	URL url;
	if (StringUtils.valid(path)) {
	    url = toURL(path);
	    urls.add(url);
	} else if (ObjectUtils.notNull(path) && path.isEmpty()) {
	    Enumeration<URL> urlEnum = LibraryLoader.getContextClassLoader().getResources(path);
	    while (urlEnum.hasMoreElements()) {
		url = urlEnum.nextElement();
		urls.add(url);
	    }
	}

	return urls;
    }

    /**
     * Checks if passed file is ZIP archive file
     *
     * @param file
     * @return <code>boolean</code>
     */
    public static boolean checkOnZip(File file) {

	boolean isZip = Boolean.TRUE;

	int length = MAGIC.length;
	byte[] buffer = new byte[length];
	try {
	    RandomAccessFile raf = new RandomAccessFile(file, READ);
	    try {
		raf.readFully(buffer);
		for (int i = CollectionUtils.FIRST_INDEX; i < length && isZip; i++) {
		    isZip = (buffer[i] == MAGIC[i]);
		}
	    } finally {
		raf.close();
	    }
	} catch (IOException ex) {
	    LOG.error(ex.getMessage(), ex);
	    isZip = Boolean.FALSE;
	}

	return isZip;
    }
}
