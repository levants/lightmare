package org.lightmare.ejb.meta;

import java.net.URL;

import org.lightmare.utils.AbstractIOUtils;

/**
 * Container class for {@link AbstractIOUtils} and {@link ClassLoader} for each
 * {@link URL} to cache to avoid duplicates at deploy time
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
