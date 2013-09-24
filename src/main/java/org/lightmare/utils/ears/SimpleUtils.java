package org.lightmare.utils.ears;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.lightmare.utils.AbstractIOUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link AbstractIOUtils} for directories
 * 
 * @author levan
 * 
 */
public class SimpleUtils extends AbstractIOUtils {

    public static final FileType type = FileType.DIR;

    public SimpleUtils(File file) {
	super(file);
    }

    @Override
    public FileType getType() {

	return type;
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

	return Boolean.FALSE;
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {

	getEjbURLs().add(realFile.toURI().toURL());
    }
}
