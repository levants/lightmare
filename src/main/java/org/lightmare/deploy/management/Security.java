package org.lightmare.deploy.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

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

    private static final String PROXY_HEADER = "x-forwarded-for";

    private static final String LOCALHOST = "localhost";

    private static final String LOCALHOST_PREFIX = "127.0.0.";

    public Security() throws IOException {
	cacheUsers();
    }

    private void loadUsers(File file) throws IOException {

	cache = new Properties();
	InputStream stream = new FileInputStream(file);
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

    public boolean controlAllowed(HttpServletRequest request) {

	boolean valid = Configuration.getRemoteControl();

	if (ObjectUtils.notTrue(valid)) {

	    String header = request.getHeader(PROXY_HEADER);
	    valid = (header == null);
	    if (valid) {
		header = request.getHeader(PROXY_HEADER.toUpperCase());
		valid = (header == null);
	    }

	    if (valid) {
		String host = request.getRemoteAddr();
		valid = StringUtils.valid(host)
			&& (host.equals("localhost") || host
				.startsWith("127.0.0."));
	    }
	}

	return valid;
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
