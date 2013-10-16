package org.lightmare.utils.fs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;

/**
 * Utility for removing {@link File}s recursively from file system
 * 
 * @author Levan
 * @since 0.0.20-SNAPSHOT
 */
public class FileUtils {

    /**
     * Lists java archive class files in passed file
     * 
     * @param file
     * @return {@link File}[]
     */
    private static File[] listJavaFiles(File file) {

	File[] subFiles = file.listFiles(new FilenameFilter() {

	    @Override
	    public boolean accept(File file, String name) {

		return name.endsWith(ArchiveUtils.JAR_FILE_EXT)
			|| name.endsWith(ArchiveUtils.CLASS_FILE_EXT)
			|| file.isDirectory();
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
    private static void addURL(Collection<URL> urls, File file)
	    throws IOException {

	try {
	    urls.add(file.toURI().toURL());
	} catch (MalformedURLException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Adds sub files of passed {@link File} array to passed {@link List} of {@link URL} objects
     * @param files
     * @param urls
     * @throws IOException
     */
    private static void addSubDirectory(File[] files, Set<URL> urls)
	    throws IOException {

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

	File file;
	try {
	    file = new File(url.toURI());
	    boolean isEarDir = checkOnEarDir(file);

	    return isEarDir;
	} catch (URISyntaxException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Check whether passed path is extracted ear directory path
     * 
     * @param file
     * @return boolean
     */
    public static boolean checkOnEarDir(String path) {

	File file = new File(path);
	boolean isEarDir = checkOnEarDir(file);

	return isEarDir;
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
		String delim;
		if (path.endsWith(ArchiveUtils.FILE_SEPARATOR)) {
		    delim = StringUtils.EMPTY_STRING;
		} else {
		    delim = ArchiveUtils.FILE_SEPARATOR;
		}
		String appxmlPath = StringUtils.concat(path, delim,
			ArchiveUtils.APPLICATION_XML_PATH);
		File appXmlFile = new File(appxmlPath);
		isEarDir = appXmlFile.exists();
	    }
	}

	return isEarDir;
    }

    /**
     * Removes passed {@link File}s from file system and if
     * {@link File#isDirectory()} removes all it's content recursively
     * 
     * @param file
     * @return boolean
     */
    public static boolean deleteFile(File file) {

	if (file.isDirectory()) {
	    File[] subFiles = file.listFiles();
	    if (CollectionUtils.valid(subFiles)) {
		for (File subFile : subFiles) {
		    deleteFile(subFile);
		}
	    }
	}

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

    public static URL toURL(File file) throws IOException {

	return file.toURI().toURL();
    }

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
	    Enumeration<URL> urlEnum = LibraryLoader.getContextClassLoader()
		    .getResources(path);
	    while (urlEnum.hasMoreElements()) {
		url = urlEnum.nextElement();
		urls.add(url);
	    }
	}

	return urls;
    }
}
