package org.lightmare.utils.fs;

import java.io.File;

/**
 * Utility for removing {@link File}s recursively from file system
 * 
 * @author Levan
 * 
 */
public class FileUtils {

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
}
