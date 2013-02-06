package org.lightmare.ejb.meta;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public class TmpData<V> {

	private Future<V> future;

	private List<File> tmpFiles;

	public TmpData(Future<V> future, List<File> tmpFiles) {
		this.future = future;
		this.tmpFiles = tmpFiles;
	}

	public Future<V> getFuture() {
		return future;
	}

	public void setFuture(Future<V> future) {
		this.future = future;
	}

	public List<File> getTmpFiles() {
		return tmpFiles;
	}

	public void setTmpFiles(List<File> tmpFile) {
		this.tmpFiles = tmpFile;
	}
}
