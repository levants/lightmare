package org.lightmare.deploy.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.IOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Manages and administrator users
 * 
 * @author Levan
 * 
 */
public class Security {

    private Properties cache;

    public static final String DEPLOY_PASS_KEY = "deploy_manager_pass";

    public Security() throws IOException {
	cacheUsers();
    }

    private void loadUsers(File file) throws IOException {

	InputStream stream = new FileInputStream(file);
	cache = new Properties();
	try {
	    cache.load(stream);
	} finally {
	    IOUtils.close(stream);
	}
    }

    public void cacheUsers() throws IOException {

	String path = Configuration.getAdminUsersPath();
	if (StringUtils.invalid(path)) {
	    path = ConfigKeys.ADMIN_USERS_PATH.getValue();
	}

	File file = new File(path);
	if (file.exists()) {
	    loadUsers(file);
	}
    }

    public boolean check() {

	return CollectionUtils.invalid(cache);
    }

    public boolean authenticate(String user, String pass) {

	boolean valid;

	if (CollectionUtils.valid(cache)) {

	    Object cacheData = cache.get(user);
	    String cachedPass = ObjectUtils.cast(cacheData, String.class);
	    valid = (StringUtils.valid(cachedPass) && cachedPass.equals(pass));
	} else {
	    valid = Boolean.TRUE;
	}

	return valid;
    }
}
