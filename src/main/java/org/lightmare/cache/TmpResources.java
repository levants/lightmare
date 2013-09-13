package org.lightmare.cache;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lightmare.deploy.BeanLoader;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * 
 */
public class TmpResources {

    private Set<List<File>> tmpFiles = new HashSet<List<File>>();

    public void addFile(List<File> files) {

	for (File file : files) {
	    file.deleteOnExit();
	}

	tmpFiles.add(files);
    }

    public void removeTempFiles() {

	for (List<File> files : tmpFiles) {
	    BeanLoader.removeResources(files);
	}
    }

    public int size() {
	return tmpFiles.size();
    }
}
