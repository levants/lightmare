package org.lightmare.cache;

/**
 * Container class to cache deployment directory information
 * 
 * @author levan
 * @since 0.0.46-SNAPSHOT
 */
public class DeploymentDirectory {

    private String path;

    private boolean scan;

    public DeploymentDirectory(String path) {
	this.path = path;
    }

    public DeploymentDirectory(String path, boolean scan) {
	this(path);
	this.scan = scan;
    }

    public boolean isScan() {
	return scan;
    }

    public void setScan(boolean scan) {
	this.scan = scan;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }
}
