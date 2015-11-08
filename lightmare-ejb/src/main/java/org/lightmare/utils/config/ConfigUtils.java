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
package org.lightmare.utils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class to convert configuration attributes from files and java types
 *
 * @author Levan Tsinadze
 * @since 0.1.1
 */
public class ConfigUtils {

    /**
     * Converts data to boolean from several java types
     *
     * @param value
     * @return <code>boolean</code>
     */
    public static boolean getBoolean(Object value) {

	boolean answer;

	if (value == null) {
	    answer = Boolean.FALSE;
	} else if (value instanceof Boolean) {
	    answer = ObjectUtils.cast(value);
	} else if (value instanceof String) {
	    String text = ObjectUtils.cast(value, String.class);
	    answer = Boolean.parseBoolean(text);
	} else {
	    answer = Boolean.FALSE;
	}

	return answer;
    }

    /**
     * Converts data to {@link String} from several java types
     *
     * @param value
     * @return {@link String} value of passed parameter
     */
    private static String getText(Object value) {

	String text;

	if (value == null) {
	    text = null;
	} else if (value instanceof String) {
	    text = ObjectUtils.cast(value, String.class);
	} else if (value instanceof CharSequence) {
	    CharSequence chars = ObjectUtils.cast(value, CharSequence.class);
	    text = chars.toString();
	} else if (value instanceof File) {
	    File file = ObjectUtils.cast(value, File.class);
	    text = file.getPath();
	} else {
	    text = null;
	}

	return text;
    }

    /**
     * Gets paths of instant EJB module from configuration
     *
     * @param value
     * @return String[] path of EJB modules
     */
    public static String[] getModule(Object value) {

	String[] module;

	String path;
	File file;
	File[] files;
	if (value instanceof String[]) {
	    module = ObjectUtils.cast(value);
	} else if (value instanceof File[]) {
	    files = ObjectUtils.cast(value);
	    int length = files.length;
	    module = new String[length];
	    for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
		file = files[i];
		path = file.getPath();
		module[i] = path;
	    }
	} else {
	    path = getText(value);
	    if (path == null) {
		module = null;
	    } else {
		module = new String[] { path };
	    }
	}

	return module;
    }

    /**
     * Adds initialized EJB module paths to passed collection
     *
     * @param modules
     * @param configModule
     */
    private static void addModule(List<String[]> modules, Object configModule) {

	if (ObjectUtils.notNull(configModule)) {
	    String[] module = getModule(configModule);
	    if (ObjectUtils.notNull(module)) {
		modules.add(module);
	    }
	}
    }

    /**
     * Converts passed value to deployment module paths from several java types
     *
     * @param value
     * @return {@link List} of EJB module paths
     */
    public static List<String[]> getModules(Object value) {

	List<String[]> modules;

	Object[] configModules;
	if (value == null) {
	    configModules = null;
	} else if (value instanceof Object[]) {
	    configModules = ObjectUtils.cast(value);
	} else {
	    configModules = getModule(value);
	}

	if (CollectionUtils.valid(configModules)) {
	    modules = new ArrayList<String[]>();
	    for (Object configModule : configModules) {
		addModule(modules, configModule);
	    }
	} else {
	    modules = null;
	}

	return modules;
    }

    /**
     * Converts data to {@link Set} of {@link String} from several java types
     *
     * @param value
     * @return {@link Set} of appropriated values
     */
    public static Set<String> getSet(Object value) {

	Set<String> values;

	if (value == null) {
	    values = null;
	} else if (value instanceof Set) {
	    Set<?> paths = ObjectUtils.cast(value);
	    values = new HashSet<String>();
	    String path;
	    for (Object data : paths) {
		path = getText(data);
		if (StringUtils.valid(path)) {
		    values.add(path);
		}
	    }
	} else {
	    String[] paths = getModule(value);
	    if (paths == null) {
		values = null;
	    } else {
		values = new HashSet<String>(Arrays.asList(paths));
	    }
	}

	return values;
    }

    /**
     * Initializes {@link DeploymentDirectory} instance from several java types
     *
     * @param value
     * @return initialized {@link DeploymentDirectory} instance
     */
    private static DeploymentDirectory getDeployment(Object value) {

	DeploymentDirectory deployment;

	String path;
	if (value == null) {
	    deployment = null;
	} else if (value instanceof DeploymentDirectory) {
	    deployment = ObjectUtils.cast(value, DeploymentDirectory.class);
	} else {
	    path = getText(value);
	    if (StringUtils.valid(path)) {
		deployment = new DeploymentDirectory(path);
	    } else {
		deployment = null;
	    }
	}

	return deployment;
    }

    /**
     * Initializes {@link DeploymentDirectory} from passed java type and adds it
     * to appropriate collection
     *
     * @param value
     * @param deployments
     */
    private static void addDirectory(Object value, Set<DeploymentDirectory> deployments) {

	DeploymentDirectory directory = getDeployment(value);
	if (ObjectUtils.notNull(directory)) {
	    deployments.add(directory);
	}
    }

    /**
     * Initializes {@link DeploymentDirectory} instance from several java types
     *
     * @param collection
     * @return initialized {@link DeploymentDirectory} instance
     */
    private static Set<DeploymentDirectory> getDeployments(Collection<?> collection) {

	Set<DeploymentDirectory> deployments;

	if (CollectionUtils.valid(collection)) {
	    deployments = new HashSet<DeploymentDirectory>();
	    for (Object data : collection) {
		addDirectory(data, deployments);
	    }
	} else {
	    deployments = null;
	}

	return deployments;
    }

    /**
     * Initializes {@link DeploymentDirectory} instance from several java types
     *
     * @param collection
     * @return initialized {@link DeploymentDirectory} instance
     */
    private static Set<DeploymentDirectory> getDeployments(Object[] array) {

	Set<DeploymentDirectory> deployments;

	if (CollectionUtils.valid(array)) {
	    deployments = new HashSet<DeploymentDirectory>();
	    for (Object data : array) {
		addDirectory(data, deployments);
	    }
	} else {
	    deployments = null;
	}

	return deployments;
    }

    /**
     * Initializes deployment path parameters
     *
     * @param value
     * @return {@link Set} of {@link DeploymentDirectory} instances
     */
    public static Set<DeploymentDirectory> getDeployments(Object value) {

	Set<DeploymentDirectory> deployments;

	if (value instanceof Collection) {
	    Collection<?> values = ObjectUtils.cast(value);
	    if (values == null || values.isEmpty()) {
		deployments = null;
	    } else {
		deployments = getDeployments(values);
	    }
	} else if (value instanceof Object[]) {
	    Object[] values = ObjectUtils.cast(value);
	    deployments = getDeployments(values);
	} else if (value instanceof String || value instanceof File) {
	    DeploymentDirectory deployment = getDeployment(value);
	    deployments = Collections.singleton(deployment);
	} else {
	    deployments = null;
	}

	return deployments;
    }
}
