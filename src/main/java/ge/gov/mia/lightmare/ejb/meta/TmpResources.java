package ge.gov.mia.lightmare.ejb.meta;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Caches all temporal {@link File} instances and deletes them after processing
 * 
 * @author levan
 * 
 */
public class TmpResources {

	public static final Set<File> tmpFiles = Collections
			.synchronizedSet(new HashSet<File>());

	public static void removeTempFiles() {

		synchronized (tmpFiles) {
			for (File tmpFile : tmpFiles) {
				tmpFile.delete();
			}
		}
	}
}
