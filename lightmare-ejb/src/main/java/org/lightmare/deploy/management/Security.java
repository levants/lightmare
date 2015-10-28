/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.deploy.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.io.IOUtils;

/**
 * Manages container administrator users for
 * {@link org.lightmare.deploy.management.DeployManager} service
 *
 * @author Levan Tsinadze
 * @since 0.0.45
 * @see DeployManager
 */
public class Security {

    private Properties cache;

    public static final String DEPLOY_PASS_KEY = "deploy_manager_pass";

    private static final String PROXY_HEADER = "x-forwarded-for";

    public Security() throws IOException {
	cacheUsers();
    }

    /**
     * Loads administrator user information from passed {@link File} parameter
     *
     * @param file
     * @throws IOException
     */
    private void loadUsers(File file) throws IOException {

	cache = new Properties();
	InputStream stream = new FileInputStream(file);
	try {
	    cache.load(stream);
	} finally {
	    IOUtils.close(stream);
	}
    }

    /**
     * Caches administrator users
     *
     * @throws IOException
     */
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

    /**
     * Checks if administrator users cache is valid
     *
     * @return <code>boolean</code>
     */
    public boolean check() {
	return CollectionUtils.invalid(cache);
    }

    /**
     * Checks if passed {@link HttpServletRequest} not contains PROXY user
     * address forwarding header
     *
     * @param request
     * @return <code>boolean</code> validation result
     */
    private static boolean checkHeader(HttpServletRequest request) {

	boolean valid;

	String header = request.getHeader(PROXY_HEADER);
	valid = (header == null);
	if (valid) {
	    header = request.getHeader(PROXY_HEADER.toUpperCase());
	    valid = (header == null);
	}

	return valid;
    }

    /**
     * Checks if container allows remote control
     *
     * @param request
     * @return <code>boolean</code>
     */
    public boolean controlAllowed(HttpServletRequest request) {

	boolean valid = Configuration.getRemoteControl();

	if (Boolean.FALSE.equals(valid)) {
	    valid = checkHeader(request);
	    if (valid) {
		String host = request.getRemoteAddr();
		String localhost = request.getLocalAddr();
		valid = StringUtils.validAll(host, localhost) && host.equals(localhost);
	    }
	}

	return valid;
    }

    /**
     * Authenticates passed user name and password
     *
     * @param user
     * @param pass
     * @return <code>boolean</code>
     */
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
