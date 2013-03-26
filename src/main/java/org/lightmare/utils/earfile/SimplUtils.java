package org.lightmare.utils.earfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.lightmare.utils.AbstractIOUtils;

/**
 * Implementation of {@link AbstractIOUtils} for directories
 * 
 * @author levan
 * 
 */
public class SimplUtils extends AbstractIOUtils {

    public SimplUtils(File file) {
	super(file);
    }

    @Override
    public InputStream earReader() throws IOException {
	return null;
    }

    @Override
    public void getEjbLibs() throws IOException {

    }

    @Override
    public void extractEjbJars(Set<String> jarNames) throws IOException {

    }

    @Override
    public boolean checkOnOrm(String jarName) throws IOException {
	return false;
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {
	getEjbURLs().add(realFile.toURI().toURL());
    }

}
