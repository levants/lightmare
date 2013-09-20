package org.lightmare.deploy.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.RestContainer;
import org.lightmare.config.Configuration;
import org.lightmare.jpa.datasource.FileParsers;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.LogUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;
import org.lightmare.utils.fs.WatchUtils;

/**
 * Deployment manager, {@link Watcher#deployFile(URL)},
 * {@link Watcher#undeployFile(URL)}, {@link Watcher#listDeployments()} and
 * {@link File} modification event handler for deployments if java version is
 * 1.7 or above
 * 
 * @author levan
 * 
 */
public class Watcher implements Runnable {

    private static final String DEPLOY_THREAD_NAME = "watch_thread";

    private static final int DEPLOY_POOL_PRIORITY = Thread.MAX_PRIORITY - 5;

    private static final long SLEEP_TIME = 5500L;

    private static final ExecutorService DEPLOY_POOL = Executors
	    .newSingleThreadExecutor(new ThreadFactoryUtil(DEPLOY_THREAD_NAME,
		    DEPLOY_POOL_PRIORITY));

    private Set<DeploymentDirectory> deployments;

    private Set<String> dataSources;

    private static final Logger LOG = Logger.getLogger(Watcher.class);

    /**
     * Defines file types for watch service
     * 
     * @author Levan
     * 
     */
    private static enum WatchFileType {

	DATA_SOURCE, DEPLOYMENT, NONE;
    }

    /**
     * To filter only deployed sub files from directory
     * 
     * @author levan
     * 
     */
    private static class DeployFiletr implements FileFilter {

	@Override
	public boolean accept(File file) {

	    boolean accept;
	    try {
		URL url = file.toURI().toURL();
		url = WatchUtils.clearURL(url);
		accept = MetaContainer.chackDeployment(url);
	    } catch (MalformedURLException ex) {
		LOG.error(ex.getMessage(), ex);
		accept = false;
	    } catch (IOException ex) {
		LOG.error(ex.getMessage(), ex);
		accept = false;
	    }

	    return accept;
	}

    }

    private Watcher() {
	deployments = getDeployDirectories();
	dataSources = getDataSourcePaths();
    }

    private static URL getAppropriateURL(String fileName) throws IOException {

	File file = new File(fileName);
	URL url = file.toURI().toURL();
	url = WatchUtils.clearURL(url);

	return url;
    }

    private static Set<DeploymentDirectory> getDeployDirectories() {

	Collection<Configuration> configs = MetaContainer.CONFIGS.values();
	Set<DeploymentDirectory> deploymetDirss = new HashSet<DeploymentDirectory>();
	Set<DeploymentDirectory> deploymetDirssCurrent;
	for (Configuration config : configs) {
	    deploymetDirssCurrent = config.getDeploymentPath();
	    if (config.isWatchStatus()
		    && CollectionUtils.available(deploymetDirssCurrent)) {
		deploymetDirss.addAll(deploymetDirssCurrent);
	    }
	}

	return deploymetDirss;
    }

    private static Set<String> getDataSourcePaths() {

	Collection<Configuration> configs = MetaContainer.CONFIGS.values();
	Set<String> paths = new HashSet<String>();
	Set<String> pathsCurrent;
	for (Configuration config : configs) {
	    pathsCurrent = config.getDataSourcePath();
	    if (config.isWatchStatus()
		    && CollectionUtils.available(pathsCurrent)) {
		paths.addAll(pathsCurrent);
	    }
	}

	return paths;
    }

    private static WatchFileType checkType(String fileName) {

	WatchFileType type;
	File file = new File(fileName);
	String path = file.getPath();
	String filePath = WatchUtils.clearPath(path);
	path = file.getParent();
	String parentPath = WatchUtils.clearPath(path);

	Set<DeploymentDirectory> apps = getDeployDirectories();
	Set<String> dss = getDataSourcePaths();

	if (CollectionUtils.available(apps)) {

	    String deploymantPath;
	    Iterator<DeploymentDirectory> iterator = apps.iterator();
	    boolean notDeployment = Boolean.TRUE;
	    DeploymentDirectory deployment;
	    while (iterator.hasNext() && notDeployment) {
		deployment = iterator.next();
		deploymantPath = deployment.getPath();
		notDeployment = ObjectUtils.notEquals(deploymantPath,
			parentPath);
	    }

	    if (notDeployment) {
		type = WatchFileType.NONE;
	    } else {
		type = WatchFileType.DEPLOYMENT;
	    }
	} else if (CollectionUtils.available(dss) && dss.contains(filePath)) {
	    type = WatchFileType.DATA_SOURCE;
	} else {
	    type = WatchFileType.NONE;
	}

	return type;
    }

    private static void fillFileList(File[] files, List<File> list) {

	if (ObjectUtils.available(files)) {
	    for (File file : files) {
		list.add(file);
	    }
	}
    }

    /**
     * Lists all deployed {@link File}s
     * 
     * @return {@link List}<File>
     */
    public static List<File> listDeployments() {

	Collection<Configuration> configs = MetaContainer.CONFIGS.values();
	Set<DeploymentDirectory> deploymetDirss = new HashSet<DeploymentDirectory>();
	Set<DeploymentDirectory> deploymetDirssCurrent;
	for (Configuration config : configs) {
	    deploymetDirssCurrent = config.getDeploymentPath();
	    if (ObjectUtils.available(deploymetDirssCurrent)) {
		deploymetDirss.addAll(deploymetDirssCurrent);
	    }
	}
	File[] files;
	List<File> list = new ArrayList<File>();
	if (ObjectUtils.available(deploymetDirss)) {
	    String path;
	    DeployFiletr filter = new DeployFiletr();
	    for (DeploymentDirectory deployment : deploymetDirss) {
		path = deployment.getPath();
		files = new File(path).listFiles(filter);
		fillFileList(files, list);
	    }
	}

	return list;
    }

    /**
     * Lists all data source {@link File}s
     * 
     * @return {@link List}<File>
     */
    public static List<File> listDataSources() {

	Collection<Configuration> configs = MetaContainer.CONFIGS.values();
	Set<String> paths = new HashSet<String>();
	Set<String> pathsCurrent;
	for (Configuration config : configs) {
	    pathsCurrent = config.getDataSourcePath();
	    if (ObjectUtils.available(pathsCurrent)) {
		paths.addAll(pathsCurrent);
	    }
	}
	File file;
	List<File> list = new ArrayList<File>();
	if (ObjectUtils.available(paths)) {
	    for (String path : paths) {
		file = new File(path);
		list.add(file);
	    }
	}

	return list;
    }

    public static void deployFile(String fileName) throws IOException {

	WatchFileType type = checkType(fileName);
	if (type.equals(WatchFileType.DATA_SOURCE)) {
	    FileParsers fileParsers = new FileParsers();
	    fileParsers.parseStandaloneXml(fileName);
	} else if (type.equals(WatchFileType.DEPLOYMENT)) {
	    URL url = getAppropriateURL(fileName);
	    deployFile(url);
	}
    }

    public static void deployFile(URL url) throws IOException {

	URL[] archives = { url };
	MetaContainer.getCreator().scanForBeans(archives);
    }

    public static void undeployFile(URL url) throws IOException {

	boolean valid = MetaContainer.undeploy(url);
	if (valid && RestContainer.hasRest()) {
	    RestProvider.reload();
	}
    }

    public static void undeployFile(String fileName) throws IOException {

	WatchFileType type = checkType(fileName);
	if (type.equals(WatchFileType.DATA_SOURCE)) {
	    Initializer.undeploy(fileName);
	} else if (type.equals(WatchFileType.DEPLOYMENT)) {
	    URL url = getAppropriateURL(fileName);
	    undeployFile(url);
	}
    }

    public static void redeployFile(String fileName) throws IOException {

	undeployFile(fileName);
	deployFile(fileName);
    }

    private void handleEvent(Path dir, WatchEvent<Path> currentEvent)
	    throws IOException {

	if (currentEvent == null) {
	    return;
	}
	Path prePath = currentEvent.context();
	Path path = dir.resolve(prePath);
	String fileName = path.toString();
	int count = currentEvent.count();
	Kind<?> kind = currentEvent.kind();
	if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
	    LogUtils.info(LOG, "Modify: %s, count: %s\n", fileName, count);
	    redeployFile(fileName);
	} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
	    LogUtils.info(LOG, "Delete: %s, count: %s\n", fileName, count);
	    undeployFile(fileName);
	} else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
	    LogUtils.info(LOG, "Create: %s, count: %s\n", fileName, count);
	    redeployFile(fileName);
	}
    }

    private void runService(WatchService watch) throws IOException {

	Path dir;
	boolean toRun = true;
	boolean valid;
	while (toRun) {
	    try {
		WatchKey key;
		key = watch.take();
		List<WatchEvent<?>> events = key.pollEvents();
		WatchEvent<?> currentEvent = null;
		WatchEvent<Path> typedCurrentEvent;
		int times = 0;
		dir = (Path) key.watchable();
		for (WatchEvent<?> event : events) {
		    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
			continue;
		    }
		    if (times == 0 || event.count() > currentEvent.count()) {
			currentEvent = event;
		    }
		    times++;
		    valid = key.reset();
		    toRun = valid && key.isValid();
		    if (toRun) {
			Thread.sleep(SLEEP_TIME);
			typedCurrentEvent = ObjectUtils.cast(currentEvent);
			handleEvent(dir, typedCurrentEvent);
		    }
		}
	    } catch (InterruptedException ex) {
		throw new IOException(ex);
	    }
	}
    }

    private void registerPath(FileSystem fs, String path, WatchService watch)
	    throws IOException {

	Path deployPath = fs.getPath(path);
	deployPath.register(watch, StandardWatchEventKinds.ENTRY_CREATE,
		StandardWatchEventKinds.ENTRY_MODIFY,
		StandardWatchEventKinds.OVERFLOW,
		StandardWatchEventKinds.ENTRY_DELETE);
	runService(watch);
    }

    private void registerPaths(File[] files, FileSystem fs, WatchService watch)
	    throws IOException {

	String path;
	for (File file : files) {
	    path = file.getPath();
	    registerPath(fs, path, watch);
	}
    }

    private void registerPaths(Collection<DeploymentDirectory> deploymentDirss,
	    FileSystem fs, WatchService watch) throws IOException {

	String path;
	boolean scan;
	File directory;
	File[] files;
	for (DeploymentDirectory deployment : deploymentDirss) {
	    path = deployment.getPath();
	    scan = deployment.isScan();
	    if (scan) {
		directory = new File(path);
		files = directory.listFiles();
		if (ObjectUtils.available(files)) {
		    registerPaths(files, fs, watch);
		}
	    } else {
		registerPath(fs, path, watch);
	    }
	}
    }

    private void registerDsPaths(Collection<String> paths, FileSystem fs,
	    WatchService watch) throws IOException {

	for (String path : paths) {
	    registerPath(fs, path, watch);
	}
    }

    @Override
    public void run() {

	try {
	    FileSystem fs = FileSystems.getDefault();
	    WatchService watch = null;
	    try {
		watch = fs.newWatchService();
	    } catch (IOException ex) {
		LOG.error(ex.getMessage(), ex);
		throw ex;
	    }
	    if (ObjectUtils.available(deployments)) {
		registerPaths(deployments, fs, watch);
	    }
	    if (ObjectUtils.available(dataSources)) {
		registerDsPaths(dataSources, fs, watch);
	    }
	} catch (IOException ex) {
	    LOG.fatal(ex.getMessage(), ex);
	    LOG.fatal("system going to shut down cause of hot deployment");
	    try {
		ConnectionContainer.closeConnections();
	    } catch (IOException iex) {
		LOG.fatal(iex.getMessage(), iex);
	    }
	    System.exit(-1);
	} finally {
	    DEPLOY_POOL.shutdown();
	}
    }

    public static void startWatch() {

	Watcher watcher = new Watcher();
	DEPLOY_POOL.submit(watcher);
    }
}
