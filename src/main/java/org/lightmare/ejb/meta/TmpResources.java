package org.lightmare.ejb.meta;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.lightmare.utils.fs.FileUtils;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * 
 */
public class TmpResources {

	private static final Set<File> TMP_Files = Collections
			.synchronizedSet(new HashSet<File>());

	public static void addFile(File file) {
		TMP_Files.add(file);
		file.deleteOnExit();
	}

	public static void removeTempFiles() {

		synchronized (TMP_Files) {
			for (File tmpFile : TMP_Files) {
				FileUtils.deleteFile(tmpFile);
			}
		}
	}

	public static int size() {
		return TMP_Files.size();
	}
}
