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
package org.lightmare.deploy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.lightmare.cache.ArchiveData;
import org.lightmare.cache.DeployData;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.RestContainer;
import org.lightmare.cache.TmpResources;
import org.lightmare.config.ConfigKeys;
import org.lightmare.config.Configuration;
import org.lightmare.deploy.BeanLoader.BeanParameters;
import org.lightmare.deploy.fs.Watcher;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.remote.rpc.RpcListener;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.scannotation.AnnotationFinder;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.finalizers.ShutDown;
import org.lightmare.utils.fs.FileUtils;
import org.lightmare.utils.fs.WatchUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;
import org.lightmare.utils.logging.LogUtils;

/**
 * Determines and saves in cache EJB beans {@link org.lightmare.cache.MetaData}
 * on startup
 *
 * @author Levan Tsinadze
 * @since 0.0.45
 */
public class MetaCreator {

    // Annotation scanner implementation for scanning at startup
    private AnnotationFinder annotationFinder;

    // Cached temporal resources for clean after deployment
    private TmpResources tmpResources;

    // Checks if needed await for EJB deployments
    private boolean await;

    // Blocker for deployments connections or beans
    private CountDownLatch blocker;

    // Data for cache at deploy time
    private Map<String, ArchiveUtils> aggregateds = new WeakHashMap<String, ArchiveUtils>();

    // Caches archive by URL for deployment
    private Map<URL, ArchiveData> archivesURLs;

    // Caches class file URLs by class names
    private Map<String, URL> classOwnersURL;

    // Caches deployment data meta information for file URL instance
    private Map<URL, DeployData> realURL;

    // Class loader for each deployment
    private ClassLoader current;

    // Configuration for appropriate archives URLs
    private Configuration configuration;

    // Lock for deployment and directory scanning
    private final Lock scannerLock = new ReentrantLock();

    // Lock for MetaCreator initialization
    private static final Lock LOCK = new ReentrantLock();

    private static final Logger LOG = Logger.getLogger(MetaCreator.class);

    private MetaCreator() {
	tmpResources = new TmpResources();
	ShutDown.setHook(tmpResources);
	// Configures JNDI properties
	JndiManager.loadContext();
    }

    /**
     * Initializes {@link MetaCreator} instance if it is not cached yet
     *
     * @return {@link MetaCreator}
     */
    private static MetaCreator initCreator() {

	MetaCreator creator = MetaContainer.getCreator();

	if (creator == null) {
	    creator = new MetaCreator();
	    MetaContainer.setCreator(creator);
	}

	return creator;
    }

    /**
     * Gets cached {@link MetaCreator} instance if such not exists creates new
     *
     * @return {@link MetaCreator}
     */
    private static MetaCreator get() {

	MetaCreator creator = MetaContainer.getCreator();

	if (creator == null) {
	    // Locks to provide singularity of MetaCreator instance
	    ObjectUtils.lock(LOCK);
	    try {
		creator = initCreator();
	    } finally {
		ObjectUtils.unlock(LOCK);
	    }
	}

	return creator;
    }

    /**
     * Gets {@link Configuration} instance for passed {@link URL} array of
     * archives
     *
     * @param archives
     */
    private void configure(URL[] archives) {

	if (configuration == null && CollectionUtils.valid(archives)) {
	    configuration = MetaContainer.getConfig(archives);
	}
    }

    public AnnotationFinder getAnnotationFinder() {
	return annotationFinder;
    }

    public Map<String, ArchiveUtils> getAggregateds() {

	Map<String, ArchiveUtils> clone = new HashMap<String, ArchiveUtils>();

	if (CollectionUtils.valid(aggregateds)) {
	    synchronized (aggregateds) {
		clone.putAll(aggregateds);
	    }
	}

	return clone;
    }

    /**
     * Caches each archive by it's {@link URL} for deployment
     *
     * @param ejbURLs
     * @param archiveData
     */
    private void fillArchiveURLs(Collection<URL> ejbURLs,
	    ArchiveData archiveData, DeployData deployData) {

	for (URL ejbURL : ejbURLs) {
	    archivesURLs.put(ejbURL, archiveData);
	    realURL.put(ejbURL, deployData);
	}
    }

    /**
     * Caches each archive by it's {@link URL} for deployment and creates fill
     * {@link URL} array for scanning and finding {@link javax.ejb.Stateless}
     * annotated classes
     *
     * @param archive
     * @param modifiedArchives
     * @throws IOException
     */
    private void fillArchiveURLs(URL archive, List<URL> modifiedArchives)
	    throws IOException {

	ArchiveUtils ioUtils = ArchiveUtils.getAppropriatedType(archive);
	if (ObjectUtils.notNull(ioUtils)) {
	    ioUtils.scan(configuration.isPersXmlFromJar());
	    List<URL> ejbURLs = ioUtils.getEjbURLs();
	    modifiedArchives.addAll(ejbURLs);
	    ArchiveData archiveData = new ArchiveData();
	    archiveData.setIoUtils(ioUtils);
	    DeployData deployData = new DeployData();
	    deployData.setType(ioUtils.getType());
	    deployData.setUrl(archive);
	    if (ejbURLs.isEmpty()) {
		archivesURLs.put(archive, archiveData);
		realURL.put(archive, deployData);
	    } else {
		fillArchiveURLs(ejbURLs, archiveData, deployData);
	    }
	}
    }

    /**
     * Gets {@link URL} array for all classes and jar libraries within archive
     * file for class loading policy
     *
     * @param archives
     * @return {@link URL}[]
     * @throws IOException
     */
    private URL[] getFullArchives(URL[] archives) throws IOException {

	List<URL> modifiedArchives = new ArrayList<URL>();
	for (URL archive : archives) {
	    fillArchiveURLs(archive, modifiedArchives);
	}

	return CollectionUtils.toArray(modifiedArchives, URL.class);
    }

    /**
     * Awaits for {@link Future} tasks if it set so by configuration
     *
     * @param future
     */
    private void awaitDeployment(Future<String> future) {

	if (await) {
	    try {
		String nameFromFuture = future.get();
		LogUtils.info(LOG, "Deploy processing of %s finished",
			nameFromFuture);
	    } catch (InterruptedException ex) {
		LOG.error(ex.getMessage(), ex);
	    } catch (ExecutionException ex) {
		LOG.error(ex.getMessage(), ex);
	    }
	}
    }

    /**
     * Awaits for {@link CountDownLatch} of deployments
     */
    private void awaitDeployments() {

	try {
	    blocker.await();
	} catch (InterruptedException ex) {
	    LOG.error(ex);
	}
    }

    /**
     * Initializes {@link ArchiveData} to find archives
     *
     * @param currentURL
     * @return {@link ArchiveData}
     */
    private ArchiveData initArchiveData(URL currentURL) {

	ArchiveData archiveData = archivesURLs.get(currentURL);

	if (archiveData == null) {
	    archiveData = new ArchiveData();
	}

	return archiveData;
    }

    /**
     * Initializes appropriated implementation of {@link ArchiveUtils} to
     * retrieve data from archives
     *
     * @param archiveData
     * @param currentURL
     * @return {@link ArchiveUtils}
     * @throws IOException
     */
    private ArchiveUtils initArchiveUtils(ArchiveData archiveData,
	    URL currentURL) throws IOException {

	ArchiveUtils ioUtils = archiveData.getIoUtils();

	if (ioUtils == null) {
	    ioUtils = ArchiveUtils.getAppropriatedType(currentURL);
	    archiveData.setIoUtils(ioUtils);
	}

	return ioUtils;
    }

    /**
     * Re runs scanner for archives and initializes appropriated class loader
     * with aggregated {@link URL}s if it is not executed yet
     *
     * @param archiveData
     * @param ioUtils
     * @return
     * @throws IOException
     */
    private ClassLoader initClassLoader(ArchiveData archiveData,
	    ArchiveUtils ioUtils) throws IOException {

	ClassLoader loader;

	if (ioUtils.notExecuted()) {
	    ioUtils.scan(configuration.isPersXmlFromJar());
	}
	URL[] libURLs = ioUtils.getURLs();
	loader = LibraryLoader.initializeLoader(libURLs);
	archiveData.setLoader(loader);

	return loader;
    }

    /**
     * Caches appropriated archives for instant EJB bean name
     *
     * @param beanName
     * @param ioUtils
     * @return
     */
    private List<File> cacheArchives(String beanName, ArchiveUtils ioUtils) {

	List<File> tmpFiles = ioUtils.getTmpFiles();

	synchronized (aggregateds) {
	    aggregateds.put(beanName, ioUtils);
	}

	return tmpFiles;
    }

    /**
     * Initializes {@link DeployData} to add deployments parameters
     *
     * @param currentURL
     * @return {@link DeployData}
     */
    private DeployData initDeploydata(URL currentURL) {

	DeployData deployData;

	if (CollectionUtils.valid(realURL)) {
	    deployData = realURL.get(currentURL);
	} else {
	    deployData = null;
	}

	return deployData;
    }

    /**
     * Initializes and sets fields in {@link BeanParameters} class
     *
     * @return {@link BeanParameters}
     */
    private BeanParameters initDeployParameters() {

	BeanParameters parameters = new BeanParameters();

	parameters.creator = this;
	parameters.blocker = blocker;
	parameters.configuration = configuration;

	return parameters;
    }

    /**
     * Sets appropriated {@link ClassLoader} to passed {@link BeanParameters}
     * instance
     *
     * @param currentURL
     * @param parameters
     * @throws IOException
     */
    private void setLoader(URL currentURL, BeanParameters parameters)
	    throws IOException {

	ArchiveData archiveData = initArchiveData(currentURL);
	ArchiveUtils ioUtils = initArchiveUtils(archiveData, currentURL);
	ClassLoader loader = archiveData.getLoader();
	// Finds appropriated ClassLoader if needed and or creates new one
	List<File> tmpFiles = null;
	if (ObjectUtils.notNull(ioUtils)) {
	    if (loader == null) {
		loader = initClassLoader(archiveData, ioUtils);
	    }
	    // Caches appropriated archive
	    tmpFiles = cacheArchives(parameters.className, ioUtils);
	}
	parameters.loader = loader;
	parameters.tmpFiles = tmpFiles;
    }

    /**
     * Initializes and sets {@link DeployData} to pased {@link BeanParameters}
     * instance
     *
     * @param currentURL
     * @param parameters
     */
    private void setDeployData(URL currentURL, BeanParameters parameters) {
	// Archive file URL which contains this bean
	DeployData deployData = initDeploydata(currentURL);
	parameters.deployData = deployData;
    }

    /**
     * Fills appropriated field of passed {@link BeanParameters} intance
     *
     * @param currentURL
     * @param parameters
     * @throws IOException
     */
    private void fillBeanParameters(URL currentURL, BeanParameters parameters)
	    throws IOException {
	setLoader(currentURL, parameters);
	setDeployData(currentURL, parameters);
    }

    /**
     * Initializes and fills BeanParameters class to deploy EJB bean
     *
     * @param beanName
     * @throws IOException
     */
    private BeanParameters initDeployParameters(String beanName)
	    throws IOException {

	BeanParameters parameters = initDeployParameters();

	parameters.className = beanName;
	URL currentURL = classOwnersURL.get(beanName);
	fillBeanParameters(currentURL, parameters);

	return parameters;
    }

    /**
     * Starts bean deployment process for bean name
     *
     * @param beanName
     * @throws IOException
     */
    private void deployBean(String beanName) throws IOException {

	// Initializes and fills BeanParameters class to deploy EJB bean
	BeanLoader.BeanParameters parameters = initDeployParameters(beanName);
	Future<String> future = BeanLoader.loadBean(parameters);
	awaitDeployment(future);
	List<File> tmpFiles = parameters.tmpFiles;
	if (CollectionUtils.valid(tmpFiles)) {
	    tmpResources.addFile(tmpFiles);
	}
    }

    /**
     * Deployes passed bean names
     *
     * @param beanNames
     */
    private void blockAndDeployBeans(Set<String> beanNames) {

	for (String beanName : beanNames) {
	    LogUtils.info(LOG, "Deploing bean %s", beanName);
	    try {
		deployBean(beanName);
	    } catch (IOException ex) {
		LogUtils.error(LOG, ex, "Could not deploy bean %s cause",
			beanName, ex.getMessage());
	    }
	}
    }

    /**
     * Reboots REST application server
     */
    private void realoadRestServer() {
	if (RestContainer.hasRest()) {
	    RestProvider.reload();
	}
    }

    /**
     * Deploys single bean by class name
     *
     * @param beanNames
     */
    private void deployBeans(Set<String> beanNames) {

	blocker = new CountDownLatch(beanNames.size());
	blockAndDeployBeans(beanNames);
	// Locks until deployments are finished
	awaitDeployments();
	realoadRestServer();
	// Process post deployment procedures
	boolean hotDeployment = configuration.isHotDeployment();
	boolean watchStatus = configuration.isWatchStatus();
	if (hotDeployment && Boolean.FALSE.equals(watchStatus)) {
	    Watcher.startWatch();
	    watchStatus = Boolean.TRUE;
	}
    }

    /**
     * Starts RPC server if configured as remote and server
     */
    private void readRemoteProperties() {

	if (configuration.isRemote() && Configuration.isServer()) {
	    RpcListener.startServer(configuration);
	} else if (configuration.isRemote()) {
	    RPCall.configure(configuration);
	}
    }

    /**
     * Loads libraries from specified path
     *
     * @throws IOException
     */
    private void initLibraries() throws IOException {

	String[] libraryPaths = configuration.getLibraryPaths();
	if (ObjectUtils.notNull(libraryPaths)) {
	    LibraryLoader.loadLibraries(libraryPaths);
	}
    }

    /**
     * Gets / initializes and caches class loader
     *
     * @param archives
     */
    private void initClassLoader(URL[] archives) {

	current = LibraryLoader.getContextClassLoader();
	archivesURLs = new WeakHashMap<URL, ArchiveData>();
	if (CollectionUtils.valid(archives)) {
	    realURL = new WeakHashMap<URL, DeployData>();
	}
    }

    /**
     * Reads EJB {@link Stateless} bean names from appropriated archives
     *
     * @param archives
     * @return
     * @throws IOException
     */
    private Set<String> readBeanNames(URL[] archives) throws IOException {

	Set<String> beanNames;

	URL[] fullArchives = getFullArchives(archives);
	annotationFinder = new AnnotationFinder();
	annotationFinder.setScanFieldAnnotations(Boolean.FALSE);
	annotationFinder.setScanParameterAnnotations(Boolean.FALSE);
	annotationFinder.setScanMethodAnnotations(Boolean.FALSE);
	annotationFinder.scanArchives(fullArchives);
	beanNames = annotationFinder.getAnnotationIndex()
		.get(Stateless.class.getName());
	classOwnersURL = annotationFinder.getClassOwnersURLs();

	return beanNames;
    }

    /**
     * Deploys EJB beans from passed array of {@link URL} for appropriated
     * archives
     *
     * @param archives
     * @throws IOException
     */
    private void deploy(URL[] archives) throws IOException {

	configure(archives);
	// Starts RPC server if configured as remote and server
	readRemoteProperties();
	// Loads libraries from specified path
	initLibraries();
	// Gets and caches class loader
	initClassLoader(archives);
	Set<String> beanNames = readBeanNames(archives);
	Initializer.initializeDataSources(configuration);
	if (CollectionUtils.valid(beanNames)) {
	    deployBeans(beanNames);
	}
    }

    /**
     * Clears and caches resources after deployment
     *
     * @param archives
     * @throws IOException
     */
    private void postDepoloy(URL[] archives) throws IOException {

	// Caches configuration
	MetaContainer.putConfig(archives, configuration);
	// clears cached resources
	clear();
	// gets rid from all created temporary files
	tmpResources.removeTempFiles();
    }

    /**
     * Scan application for find all {@link javax.ejb.Stateless} beans and
     * {@link Remote} or {@link Local} proxy interfaces
     *
     * @param archives
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void scanForBeans(URL[] archives) throws IOException {

	ObjectUtils.lock(scannerLock);
	try {
	    deploy(archives);
	} finally {
	    postDepoloy(archives);
	    ObjectUtils.unlock(scannerLock);
	}
    }

    /**
     * Scan application for find all {@link javax.ejb.Stateless} beans and
     * {@link Remote} or {@link Local} proxy interfaces
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void scanForBeans(File[] jars) throws IOException {

	List<URL> urlList = new ArrayList<URL>();
	URL url;
	for (File file : jars) {
	    url = file.toURI().toURL();
	    urlList.add(url);
	}
	URL[] archives = CollectionUtils.toArray(urlList, URL.class);
	scanForBeans(archives);
    }

    /**
     * Scans sub files for deploy path
     *
     * @param pathList
     * @param deployment
     */
    private void scanDeployPath(List<String> pathList,
	    DeploymentDirectory deployment) {

	File deployFile = new File(deployment.getPath());
	if (deployment.isScan()) {
	    String[] subDeployments = deployFile.list();
	    if (CollectionUtils.valid(subDeployments)) {
		pathList.addAll(Arrays.asList(subDeployments));
	    }
	}
    }

    /**
     * Deploys paths for EJB beans containing files in one module
     *
     * @param paths
     * @throws IOException
     */
    private void deployPaths(String... paths) throws IOException {

	if (CollectionUtils.invalid(paths)) {
	    if (CollectionUtils.valid(configuration.getDeploymentPath())) {
		Set<DeploymentDirectory> deployments = configuration
			.getDeploymentPath();
		List<String> pathList = new ArrayList<String>();
		for (DeploymentDirectory deployment : deployments) {
		    scanDeployPath(pathList, deployment);
		}
		paths = CollectionUtils.toArray(pathList, String.class);
	    }
	}

	List<URL> urlList = new ArrayList<URL>();
	List<URL> archive;
	for (String path : paths) {
	    archive = FileUtils.toURLWithClasspath(path);
	    urlList.addAll(archive);
	}

	URL[] archives = CollectionUtils.toArray(urlList, URL.class);
	scanForBeans(archives);
    }

    /**
     * Scan application for find all {@link javax.ejb.Stateless} beans and
     * {@link Remote} or {@link Local} proxy interfaces
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void scanForBeans(String... paths) throws IOException {

	if (CollectionUtils.invalid(paths)) {
	    List<String[]> modules = configuration.getDeploymentModules();
	    if (CollectionUtils.valid(modules)) {
		// Clones configuration for next module
		Configuration cloneConfig = MetaContainer.clone(configuration);
		for (String[] module : modules) {
		    scanForBeans(module);
		    this.configuration = cloneConfig;
		}
		this.configuration = null;
	    }
	} else {
	    deployPaths(paths);
	}
    }

    public ClassLoader getCurrent() {
	return current;
    }

    /**
     * Clears locally cached data
     */
    private void clearresources() {

	// Real URL
	if (CollectionUtils.valid(realURL)) {
	    realURL.clear();
	    realURL = null;
	}
	// Aggregated URLs
	if (CollectionUtils.valid(aggregateds)) {
	    synchronized (aggregateds) {
		aggregateds.clear();
	    }
	}
	// Archive URLs
	if (CollectionUtils.valid(archivesURLs)) {
	    archivesURLs.clear();
	    archivesURLs = null;
	}
	// Class owner URLs
	if (CollectionUtils.valid(classOwnersURL)) {
	    classOwnersURL.clear();
	    classOwnersURL = null;
	}
	// Configuration
	configuration = null;
    }

    /**
     * Clears all locally cached data
     */
    public void clear() {

	boolean locked = Boolean.FALSE;

	while (Boolean.FALSE.equals(locked)) {
	    // Tries to lock for avoid concurrent modification
	    locked = ObjectUtils.tryLock(scannerLock);
	    if (locked) {
		try {
		    clearresources();
		} finally {
		    ObjectUtils.unlock(scannerLock);
		}
	    }
	}
    }

    /**
     * Closes all connections clears all caches
     *
     * @throws IOException
     */
    public static void close() throws IOException {
	ShutDown.clearAll();
    }

    /**
     * Builder class to provide properties for lightmare application and
     * initialize {@link MetaCreator} instance
     *
     * @author Levan Tsinadze
     * @since 0.0.45-SNAPSHOT
     */
    public static class Builder {

	private MetaCreator creator;

	public Builder(boolean cloneConf) throws IOException {

	    creator = MetaCreator.get();
	    Configuration config = creator.configuration;
	    if (cloneConf && ObjectUtils.notNull(config)) {
		try {
		    creator.configuration = config.clone();
		} catch (CloneNotSupportedException ex) {
		    throw new IOException(ex);
		}
	    } else {
		creator.configuration = new Configuration();
	    }
	}

	public Builder() throws IOException {
	    this(Boolean.FALSE);
	}

	public Builder(Map<Object, Object> configuration) throws IOException {
	    this();
	    creator.configuration.configure(configuration);
	}

	public Builder(String path) throws IOException {
	    this();
	    creator.configuration.configure(path);
	}

	/**
	 * Configures persistence for cached properties
	 *
	 * @return {@link Map}<code><Object, Object></code>
	 */
	private Map<Object, Object> initPersistenceProperties() {

	    Map<Object, Object> persistenceProperties = creator.configuration
		    .getPersistenceProperties();
	    if (persistenceProperties == null) {
		persistenceProperties = new HashMap<Object, Object>();
		creator.configuration
			.setPersistenceProperties(persistenceProperties);
	    }

	    return persistenceProperties;
	}

	/**
	 * Sets additional persistence properties
	 *
	 * @param properties
	 * @return {@link Builder}
	 */
	public Builder setPersistenceProperties(
		Map<String, String> properties) {

	    if (CollectionUtils.valid(properties)) {
		Map<Object, Object> persistenceProperties = initPersistenceProperties();
		persistenceProperties.putAll(properties);
	    }

	    return this;
	}

	/**
	 * Adds instant persistence property
	 *
	 * @param key
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder addPersistenceProperty(String key, String property) {

	    Map<Object, Object> persistenceProperties = initPersistenceProperties();
	    persistenceProperties.put(key, property);

	    return this;
	}

	/**
	 * Adds property to scan for {@link javax.persistence.Entity} annotated
	 * classes from deployed archives
	 *
	 * @param scanForEnt
	 * @return {@link Builder}
	 */
	public Builder setScanForEntities(boolean scanForEnt) {
	    creator.configuration.setScanForEntities(scanForEnt);
	    return this;
	}

	/**
	 * Adds property to use only {@link org.lightmare.annotations.UnitName}
	 * annotated entities for which
	 * {@link org.lightmare.annotations.UnitName#value()} matches passed
	 * unit name
	 *
	 * @param unitName
	 * @return {@link Builder}
	 */
	public Builder setUnitName(String unitName) {
	    creator.configuration.setAnnotatedUnitName(unitName);
	    return this;
	}

	/**
	 * Sets path for persistence.xml file
	 *
	 * @param path
	 * @return {@link Builder}
	 */
	public Builder setPersXmlPath(String path) {

	    creator.configuration.setPersXmlPath(path);
	    creator.configuration.setScanArchives(Boolean.FALSE);

	    return this;
	}

	/**
	 * Adds path for additional libraries to load at start time
	 *
	 * @param libPaths
	 * @return {@link Builder}
	 */
	public Builder setLibraryPath(String... libPaths) {
	    creator.configuration.setLibraryPaths(libPaths);
	    return this;
	}

	/**
	 * Sets boolean checker to scan persistence.xml files from appropriated
	 * jar files
	 *
	 * @param xmlFromJar
	 * @return {@link Builder}
	 */
	public Builder setXmlFromJar(boolean xmlFromJar) {
	    creator.configuration.setPersXmlFromJar(xmlFromJar);
	    return this;
	}

	/**
	 * Sets boolean checker to swap jta data source value with non jta data
	 * source value
	 *
	 * @param swapDataSource
	 * @return {@link Builder}
	 */
	public Builder setSwapDataSource(boolean swapDataSource) {
	    creator.configuration.setSwapDataSource(swapDataSource);
	    return this;
	}

	/**
	 * Adds path for data source file
	 *
	 * @param dataSourcePath
	 * @return {@link Builder}
	 */
	public Builder addDataSourcePath(String dataSourcePath) {
	    creator.configuration.addDataSourcePath(dataSourcePath);
	    return this;
	}

	/**
	 * This method is deprecated should use
	 * {@link MetaCreator.Builder#addDataSourcePath(String)} instead
	 *
	 * @param dataSourcePath
	 * @return {@link MetaCreator.Builder}
	 */
	@Deprecated
	public Builder setDataSourcePath(String dataSourcePath) {
	    creator.configuration.addDataSourcePath(dataSourcePath);
	    return this;
	}

	/**
	 * Sets single data source configuration
	 *
	 * @param datasource
	 * @return {@link MetaCreator.Builder}
	 */
	public Builder setDataSource(Map<Object, Object> datasource) {
	    creator.configuration.setDataSource(datasource);
	    return this;
	}

	/**
	 * Adds passed data source to configuration
	 *
	 * @param datasource
	 * @return {@link MetaCreator.Builder}
	 */
	public Builder addDataSource(Map<Object, Object> datasource) {
	    creator.configuration.addDataSource(datasource);
	    return this;
	}

	/**
	 * Sets collection of data sources configuration
	 *
	 * @param datasources
	 * @return {@link MetaCreator.Builder}
	 */
	public Builder setDataSources(List<Map<Object, Object>> datasources) {
	    creator.configuration.setDataSources(datasources);
	    return this;
	}

	/**
	 * Sets boolean checker to scan {@link javax.persistence.Entity}
	 * annotated classes from appropriated deployed archive files
	 *
	 * @param scanArchives
	 * @return {@link Builder}
	 */
	public Builder setScanArchives(boolean scanArchives) {
	    creator.configuration.setScanArchives(scanArchives);
	    return this;
	}

	/**
	 * Sets boolean checker to block deployment processes
	 *
	 * @param await
	 * @return {@link Builder}
	 */
	public Builder setAwaitDeploiment(boolean await) {
	    creator.await = await;
	    return this;
	}

	/**
	 * Sets property is server or not in embedded mode
	 *
	 * @param remote
	 * @return {@link Builder}
	 */
	public Builder setRemote(boolean remote) {
	    creator.configuration.setRemote(remote);
	    return this;
	}

	/**
	 * Sets property is application server or just client for other remote
	 * server
	 *
	 * @param server
	 * @return {@link Builder}
	 */
	public Builder setServer(boolean server) {

	    Configuration.setServer(server);
	    creator.configuration.setClient(Boolean.FALSE.equals(server));

	    return this;
	}

	/**
	 * Sets boolean check is application in just client mode or not
	 *
	 * @param client
	 * @return {@link Builder}
	 */
	public Builder setClient(boolean client) {

	    creator.configuration.setClient(client);
	    Configuration.setServer(Boolean.FALSE.equals(client));

	    return this;
	}

	/**
	 * To add any additional property
	 *
	 * @param key
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setProperty(String key, String property) {
	    creator.configuration.putValue(key, property);
	    return this;
	}

	/**
	 * To add remote control check
	 *
	 * @param remoteControl
	 * @return {@link Builder}
	 */
	public Builder setRemoteControl(boolean remoteControl) {
	    Configuration.setRemoteControl(remoteControl);
	    return this;
	}

	/**
	 * File path for administrator user name and password
	 *
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setAdminUsersPth(String property) {
	    Configuration.setAdminUsersPath(property);
	    return this;
	}

	/**
	 * Sets specific IP address in case when application is in remote server
	 * mode
	 *
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setIpAddress(String property) {
	    creator.configuration.putValue(ConfigKeys.IP_ADDRESS.key, property);
	    return this;
	}

	/**
	 * Sets specific port in case when application is in remote server mode
	 *
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setPort(String property) {
	    creator.configuration.putValue(ConfigKeys.PORT.key, property);
	    return this;
	}

	/**
	 * Sets amount for network master threads in case when application is in
	 * remote server mode
	 *
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setMasterThreads(String property) {
	    creator.configuration.putValue(ConfigKeys.BOSS_POOL.key, property);
	    return this;
	}

	/**
	 * Sets amount of worker threads in case when application is in remote
	 * server mode
	 *
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setWorkerThreads(String property) {
	    creator.configuration.putValue(ConfigKeys.WORKER_POOL.key,
		    property);
	    return this;
	}

	/**
	 * Adds deploy file path to application with boolean checker if file is
	 * directory to scan this directory for deployment files list
	 *
	 * @param deploymentPath
	 * @param scan
	 * @return {@link Builder}
	 */
	public Builder addDeploymentPath(String deploymentPath, boolean scan) {

	    String clearPath = WatchUtils.clearPath(deploymentPath);
	    creator.configuration.addDeploymentPath(clearPath, scan);

	    return this;
	}

	/**
	 * Adds deploy file path to application
	 *
	 * @param deploymentPath
	 * @return {@link Builder}
	 */
	public Builder addDeploymentPath(String deploymentPath) {
	    addDeploymentPath(deploymentPath, Boolean.FALSE);
	    return this;
	}

	/**
	 * Adds timeout for connection in case when application is in remote
	 * server or client mode
	 *
	 * @param property
	 * @return {@link Builder}
	 */
	public Builder setTimeout(String property) {
	    creator.configuration.putValue(ConfigKeys.CONNECTION_TIMEOUT.key,
		    property);
	    return this;
	}

	/**
	 * Adds boolean check if application is using pooled data source
	 *
	 * @param dsPooledType
	 * @return {@link Builder}
	 */
	public Builder setDataSourcePooledType(boolean dsPooledType) {
	    Configuration.setDataSourcePooledType(dsPooledType);
	    return this;
	}

	/**
	 * Sets which data source pool provider should use application by
	 * {@link PoolProviderType} parameter
	 *
	 * @param poolProviderType
	 * @return {@link Builder}
	 */
	public Builder setPoolProviderType(PoolProviderType poolProviderType) {
	    Configuration.setPoolProviderType(poolProviderType);
	    return this;
	}

	/**
	 * Sets path for data source pool additional properties
	 *
	 * @param path
	 * @return {@link Builder}
	 */
	public Builder setPoolPropertiesPath(String path) {
	    Configuration.setPoolPropertiesPath(path);
	    return this;
	}

	/**
	 * Sets data source pool additional properties
	 *
	 * @param properties
	 * @return {@link Builder}
	 */
	public Builder setPoolProperties(
		Map<? extends Object, ? extends Object> properties) {
	    Configuration.setPoolProperties(properties);
	    return this;
	}

	/**
	 * Adds instance property for pooled data source
	 *
	 * @param key
	 * @param value
	 * @return {@link Builder}
	 */
	public Builder addPoolProperty(Object key, Object value) {
	    Configuration.addPoolProperty(key, value);
	    return this;
	}

	/**
	 * Sets boolean check is application in hot deployment (with watch
	 * service on deployment directories) or not
	 *
	 * @param hotDeployment
	 * @return {@link Builder}
	 */
	public Builder setHotDeployment(boolean hotDeployment) {
	    creator.configuration.setHotDeployment(hotDeployment);
	    return this;
	}

	/**
	 * Adds additional parameters from passed {@link Map} to existing
	 * configuration
	 *
	 * @param configuration
	 * @return
	 */
	public Builder addConfiguration(Map<Object, Object> configuration) {
	    creator.configuration.configure(configuration);
	    return this;
	}

	public MetaCreator build() throws IOException {

	    creator.configuration.configure();
	    LOG.info("Lightmare application starts working");

	    return creator;
	}
    }
}
