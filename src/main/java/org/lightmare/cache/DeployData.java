package org.lightmare.cache;

import java.net.URL;

import org.lightmare.utils.fs.FileType;

/**
 * Caches information about deployed file {@link URL} and {@link FileType} for
 * hot deployment processing
 * 
 * @author levan
 * @since 0.0.45-SNAPSHOT
 */
public class DeployData {

    private FileType type;

    private URL url;

    public FileType getType() {
	return type;
    }

    public void setType(FileType type) {
	this.type = type;
    }

    public URL getUrl() {
	return url;
    }

    public void setUrl(URL url) {
	this.url = url;
    }
}
