package org.lightmare.ejb.meta;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
public class TmpResources<V> {

	private ConcurrentMap<URL, List<TmpData<V>>> TMP_FILES = new ConcurrentHashMap<URL, List<TmpData<V>>>();

	public void addFile(URL url, Future<V> future, List<File> files) {

		for (File file : files) {
			file.deleteOnExit();
		}
		TmpData<V> tmpData = new TmpData<V>(future, files);
		List<TmpData<V>> tmpDatas = TMP_FILES.get(url);
		if (tmpDatas == null) {
			tmpDatas = new ArrayList<TmpData<V>>();
			TMP_FILES.put(url, tmpDatas);
		}
		tmpDatas.add(tmpData);
	}

	public void removeTempFiles() {

		List<TmpData<V>> tmpDatas;
		for (Map.Entry<URL, List<TmpData<V>>> tmpFiles : TMP_FILES.entrySet()) {
			tmpDatas = tmpFiles.getValue();
			BeanLoader.removeResources(tmpDatas);
		}
	}

	public int size() {
		return TMP_FILES.size();
	}
}
