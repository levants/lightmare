package org.lightmare.cache;

import org.lightmare.utils.fs.codecs.ArchiveUtils;

/**
 * Container class for {@link org.lightmare.utils.fs.codecs.ArchiveUtils} and
 * {@link ClassLoader} for each {@link java.net.URL} to cache and avoid
 * duplicates at deploy time
 * 
 * @author levan
 * @since 0.0.45-SNAPSHOT
 */
public class ArchiveData {

    private ArchiveUtils ioUtils;

    private ClassLoader loader;

    public ArchiveUtils getIoUtils() {
	return ioUtils;
    }

    public void setIoUtils(ArchiveUtils ioUtils) {
	this.ioUtils = ioUtils;
    }

    public ClassLoader getLoader() {
	return loader;
    }

    public void setLoader(ClassLoader loader) {
	this.loader = loader;
    }
}
