package org.lightmare.utils.fs;

import java.io.File;

/**
 * Utility for removing {@link File}s recursively from file system
 * 
 * @author Levan
 * 
 */
public class FileUtils {

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
