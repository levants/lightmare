package org.lightmare.jpa;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;

/**
 * For getting resources from persistence.xml path
 * 
 * @author Levan
 * @since 0.0.16-SNAPSHOT
 */
public class ConfigLoader {

    // Path for ORM configuration file
    public static final String XML_PATH = "META-INF/persistence.xml";

    // Error messages
    private static final String PATH_NOT_PROVIDED_ERROR = "Path is not provided";

    private static final String COULD_NOT_FIND_ERROR = "Could not find persistence.xml file on path ";

    private String shortPath;

    public String getShortPath() {
	return shortPath;
    }

    /**
     * Converts passed to {@link Enumeration} to build persistence configuration
     * 
     * @see Ejb3ConfigurationImpl#configure(String, java.util.Map)
     * 
     * @param path
     * @return Enumeration<{@link URL}>
     * @throws IOException
     */
    public Enumeration<URL> readURL(final URL url) {

	Enumeration<URL> xmls = new Enumeration<URL>() {

	    private boolean nextElement = Boolean.TRUE;

	    @Override
	    public boolean hasMoreElements() {
		return nextElement;
	    }

	    @Override
	    public URL nextElement() {

		nextElement = Boolean.FALSE;

		return url;
	    }
	};

	shortPath = StringUtils
		.concat(ArchiveUtils.ARCHIVE_URL_DELIM, XML_PATH);

	return xmls;
    }

    /**
     * Reads {@link URL} from passed path {@link String} for build persistence
     * configuration
     * 
     * @see Ejb3ConfigurationImpl#configure(String, java.util.Map)
     * 
     * @param path
     * @return Enumeration<{@link URL} >
     * @throws IOException
     */
    public Enumeration<URL> readFile(String path) throws IOException {

	Enumeration<URL> xmls;
	
	if (path == null || path.isEmpty()) {
	    throw new IOException(PATH_NOT_PROVIDED_ERROR);
	}

	File file = new File(path);
	if (ObjectUtils.notTrue(file.exists())) {
	    throw new IOException(
		    StringUtils.concat(COULD_NOT_FIND_ERROR, path));
	}

	shortPath = file.getName();
	final URL url = file.toURI().toURL();
	xmls = readURL(url);

	return xmls;
    }
}
