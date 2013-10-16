package org.lightmare.utils.fs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.lightmare.cache.DeployData;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Utility class for directory watch service
 * 
 * @author levan
 * @since 0.0.45-SNAPSHOT
 */
public class WatchUtils {

    /**
     * Clears {@link URL} path
     * @param path
     * @return {@link String}
     */
    public static String clearPath(String path) {

	String cleanPath;

	if (path.endsWith(File.separator)) {
	    int from = CollectionUtils.FIRST_INDEX;
	    int to = path.length() - CollectionUtils.SECOND_INDEX;
	    cleanPath = path.substring(from, to);
	} else {
	    cleanPath = path;
	}

	return cleanPath;
    }

    public static URL clearURL(URL url) throws IOException {

	URL normURL;

	String path = url.toString();
	if (path.endsWith(File.separator)) {
	    int from = CollectionUtils.FIRST_INDEX;
	    int to = path.length() - CollectionUtils.SECOND_INDEX;
	    path = path.substring(from, to);
	}
	normURL = new URL(path);

	return normURL;
    }

    public static boolean checkForWatch(DeployData deployData) {

	boolean check = ObjectUtils.notNull(deployData);
	if (check) {

	    URL url = deployData.getUrl();
	    FileType fileType = deployData.getType();
	    check = ObjectUtils.notNull(url) && ObjectUtils.notNull(fileType);
	    if (check) {
		check = fileType.equals(FileType.JAR)
			|| fileType.equals(FileType.EAR)
			|| fileType.equals(FileType.EDIR);
	    }
	}

	return check;
    }
}
