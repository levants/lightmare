package org.lightmare.cache;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lightmare.deploy.BeanLoader;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * @since 0.0.45-SNAPSHOT
 */
public class TmpResources {

    // Cache for all temporal files used at deployment time
    private Set<List<File>> tmpFiles = new HashSet<List<File>>();

    /**
     * Caches passed collection of temporal files
     * 
     * @param files
     */
    public void addFile(List<File> files) {

	for (File file : files) {
	    file.deleteOnExit();
	}

	tmpFiles.add(files);
    }

    /**
     * Deletes all temporal files for deployment
     * 
     * @throws IOException
     */
    public void removeTempFiles() throws IOException {

	for (List<File> files : tmpFiles) {
	    BeanLoader.removeResources(files);
	}

	tmpFiles.clear();
    }

    /**
     * Gets size of cached temporal files
     * @return <code>int</code>
     */
    public int size() {
	return tmpFiles.size();
    }
}
