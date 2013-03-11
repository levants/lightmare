package org.lightmare.utils.fs;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility for removing {@link File}s recursively from file system
 * 
 * @author Levan
 * 
 */
public class FileUtils {

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
	    isEarDir = (files != null && files.length > 0);
	    if (isEarDir) {
		String path = file.getPath();
		String delim;
		if (path.endsWith("/")) {
		    delim = "";
		} else {
		    delim = "/";
		}
		String appxmlPath = String.format(
			"%s%sMETA-INF/application.xml", path, delim);
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
	    if (subFiles != null) {
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
}
