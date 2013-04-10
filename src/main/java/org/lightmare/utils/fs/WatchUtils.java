package org.lightmare.utils.fs;

import java.net.URL;

import org.lightmare.cache.DeployData;
import org.lightmare.utils.ObjectUtils;

/**
 * Utility class for directory watch service
 * 
 * @author levan
 * 
 */
public class WatchUtils {

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
