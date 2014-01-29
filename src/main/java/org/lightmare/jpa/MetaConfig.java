package org.lightmare.jpa;

import java.net.URL;
import java.util.List;

/**
 * Additional properties for JPA configuration
 * 
 * @author Levan Tsinadze
 * @since 0.0.86-SNAPSHOT
 */
public class MetaConfig {

    // Arguments from lightmare
    private transient ClassLoader overridenClassLoader;

    private transient List<String> classes;

    private transient List<URL> xmls;

    private transient boolean swapDataSource;

    private transient boolean scanArchives;

    private transient String shortPath = "/META-INF/persistence.xml";

    public static ClassLoader getOverridenClassLoader(MetaConfig metaConfig) {

	ClassLoader loader;

	if (metaConfig == null) {
	    loader = null;
	} else {
	    loader = metaConfig.overridenClassLoader;
	}

	return loader;
    }

    public void setOverridenClassLoader(ClassLoader overridenClassLoader) {
	this.overridenClassLoader = overridenClassLoader;
    }

    public static List<String> getClasses(MetaConfig metaConfig) {

	List<String> entities;

	if (metaConfig == null) {
	    entities = null;
	} else {
	    entities = metaConfig.classes;
	}

	return entities;
    }

    public void setClasses(List<String> classes) {
	this.classes = classes;
    }

    public static List<URL> getXmls(MetaConfig metaConfig) {

	List<URL> urls;

	if (metaConfig == null) {
	    urls = null;
	} else {
	    urls = metaConfig.xmls;
	}

	return urls;
    }

    public void setXmls(List<URL> xmls) {
	this.xmls = xmls;
    }

    public static boolean isSwapDataSource(MetaConfig metaConfig) {

	boolean valid;

	if (metaConfig == null) {
	    valid = Boolean.FALSE;
	} else {
	    valid = metaConfig.swapDataSource;
	}

	return valid;
    }

    public void setSwapDataSource(boolean swapDataSource) {
	this.swapDataSource = swapDataSource;
    }

    public static boolean isScanArchives(MetaConfig metaConfig) {

	boolean valid;

	if (metaConfig == null) {
	    valid = Boolean.FALSE;
	} else {
	    valid = metaConfig.scanArchives;
	}

	return valid;
    }

    public void setScanArchives(boolean scanArchives) {
	this.scanArchives = scanArchives;
    }

    public static String getShortPath(MetaConfig metaConfig) {

	String path;

	if (metaConfig == null) {
	    path = null;
	} else {
	    path = metaConfig.shortPath;
	}

	return path;
    }

    public void setShortPath(String shortPath) {
	this.shortPath = shortPath;
    }
}
