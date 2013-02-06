package org.lightmare.ejb.meta;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.lightmare.ejb.startup.BeanLoader;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * 
 */
public class TmpResources {

	private ConcurrentMap<Future<Boolean>, List<File>> TMP_FILES = new ConcurrentHashMap<Future<Boolean>, List<File>>();

	public void addFile(Future<Boolean> future, List<File> files) {

		TMP_FILES.putIfAbsent(future, files);
		for (File file : files) {
			file.deleteOnExit();
		}
	}

	public void removeTempFiles() {

		for (Map.Entry<Future<Boolean>, List<File>> tmpFiles : TMP_FILES
				.entrySet()) {
			BeanLoader.removeResources(tmpFiles);
		}
	}

	public Iterator<Future<Boolean>> getDeployeds() {

		return TMP_FILES.keySet().iterator();
	}

	public int size() {
		return TMP_FILES.size();
	}
}
