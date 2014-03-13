package org.lightmare.utils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

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
	    answer = Boolean.valueOf(text);
	} else {
	    answer = Boolean.FALSE;
	}

	return answer;
    }

    /**
     * Converts data to {@link String} array from several java types
     * 
     * @param value
     * @return {@link String}[] array of specific values
     */
    public static String[] getArray(Object value) {

	String[] values;

	if (value == null) {
	    values = null;
	} else if (value instanceof String[]) {
	    values = ObjectUtils.cast(value);
	} else if (value instanceof String) {
	    String path = ObjectUtils.cast(value, String.class);
	    values = new String[] { path };
	} else {
	    values = null;
	}

	return values;
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
	    values = ObjectUtils.cast(value);
	} else if (value instanceof String) {
	    String path = ObjectUtils.cast(value, String.class);
	    values = Collections.singleton(path);
	} else {
	    values = null;
	}

	return values;
    }

    /**
     * Gets paths of instant EJB module from configuration
     * 
     * @param configModule
     * @return String[] path of EJB modules
     */
    private static String[] getModule(Object configModule) {

	String[] module;

	String path;
	File file;
	File[] files;
	if (configModule instanceof String[]) {
	    module = ObjectUtils.cast(configModule);
	} else if (configModule instanceof String) {
	    path = ObjectUtils.cast(configModule, String.class);
	    module = new String[] { path };
	} else if (configModule instanceof File) {
	    file = ObjectUtils.cast(configModule, File.class);
	    path = file.getPath();
	    module = new String[] { path };
	} else if (configModule instanceof File[]) {
	    files = ObjectUtils.cast(configModule);
	    int length = files.length;
	    module = new String[length];
	    for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
		file = files[i];
		path = file.getPath();
		module[i] = path;
	    }
	} else {
	    module = null;
	}

	return module;
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
	    String[] module;
	    for (Object configModule : configModules) {
		if (ObjectUtils.notNull(configModule)) {
		    module = getModule(configModule);
		    if (ObjectUtils.notNull(module)) {
			modules.add(module);
		    }
		}
	    }
	} else {
	    modules = null;
	}

	return modules;
    }

    /**
     * Initializes {@link DeploymentDirectory} instance from several java types
     * 
     * @param value
     * @return initialized {@link DeploymentDirectory} instance
     */
    private static DeploymentDirectory getDeployment(Object value) {

	DeploymentDirectory deployment;

	if (value == null) {
	    deployment = null;
	} else if (value instanceof DeploymentDirectory) {
	    deployment = ObjectUtils.cast(value, DeploymentDirectory.class);
	} else if (value instanceof String) {
	    String path = ObjectUtils.cast(value, String.class);
	    deployment = new DeploymentDirectory(path);
	} else {
	    deployment = null;
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
    private static void addDirectory(Object value,
	    Set<DeploymentDirectory> deployments) {

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
    private static Set<DeploymentDirectory> getDeployments(
	    Collection<?> collection) {

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
	} else if (value instanceof String) {
	    DeploymentDirectory deployment = getDeployment(value);
	    deployments = Collections.singleton(deployment);
	} else {
	    deployments = null;
	}

	return deployments;
    }
}
