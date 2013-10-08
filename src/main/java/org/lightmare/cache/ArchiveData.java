package org.lightmare.cache;

import org.lightmare.utils.fs.codecs.AbstractIOUtils;

/**
 * Container class for {@link org.lightmare.utils.fs.codecs.AbstractIOUtils} and
 * {@link ClassLoader} for each {@link java.net.URL} to cache and avoid
 * duplicates at deploy time
 * 
 * @author levan
 * 
 */
public class ArchiveData {

    private AbstractIOUtils ioUtils;

    private ClassLoader loader;

    public AbstractIOUtils getIoUtils() {
	return ioUtils;
    }

    public void setIoUtils(AbstractIOUtils ioUtils) {
	this.ioUtils = ioUtils;
    }

    public ClassLoader getLoader() {
	return loader;
    }

    public void setLoader(ClassLoader loader) {
	this.loader = loader;
    }
}
