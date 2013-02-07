package org.lightmare.ejb.meta;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.lightmare.ejb.startup.BeanLoader;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * 
 */
public class TmpResources<V> {

	private Set<TmpData<V>> TMP_FILES = Collections
			.synchronizedSet(new HashSet<TmpData<V>>());

	public void addFile(Future<V> future, List<File> files) {

		for (File file : files) {
			file.deleteOnExit();
		}
		TmpData<V> tmpData = new TmpData<V>(future, files);
		TMP_FILES.add(tmpData);
	}

	public void removeTempFiles() {

		for (TmpData<V> tmpData : TMP_FILES) {
			BeanLoader.removeResources(tmpData);
		}
	}

	public int size() {
		return TMP_FILES.size();
	}
}
