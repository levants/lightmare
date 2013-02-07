package org.lightmare.ejb.meta;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lightmare.ejb.startup.BeanLoader;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * 
 */
public class TmpResources<V> {

	private Set<List<File>> TMP_FILES = new HashSet<List<File>>();

	public void addFile(List<File> files) {

		for (File file : files) {
			file.deleteOnExit();
		}
		TMP_FILES.add(files);
	}

	public void removeTempFiles() {

		for (List<File> files : TMP_FILES) {
			BeanLoader.removeResources(files);
		}
	}

	public int size() {
		return TMP_FILES.size();
	}
}
