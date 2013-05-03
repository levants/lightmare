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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.cache.MetaContainer;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.FileParsers;
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
	deployments = MetaContainer.CONFIG.getDeploymentPath();
	dataSources = MetaContainer.CONFIG.getDataSourcePath();
    }

    private static URL getAppropriateURL(String fileName) throws IOException {

	File file = new File(fileName);
	URL url = file.toURI().toURL();
	url = WatchUtils.clearURL(url);

	return url;
    }

    private static WatchFileType checkType(String fileName) {

	WatchFileType type;
	File file = new File(fileName);
	String path = file.getPath();
	String filePath = WatchUtils.clearPath(path);
	path = file.getParent();
	String parentPath = WatchUtils.clearPath(path);

	Set<DeploymentDirectory> apps = MetaContainer.CONFIG.getDeploymentPath();
	Set<String> dss = MetaContainer.CONFIG.getDataSourcePath();

	if (ObjectUtils.available(apps) && apps.contains(parentPath)) {
	    type = WatchFileType.DEPLOYMENT;
	} else if (ObjectUtils.available(dss) && dss.contains(filePath)) {
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

	Set<DeploymentDirectory> deploymetDirss = MetaContainer.CONFIG
		.getDeploymentPath();
	File[] files;
	List<File> list = new ArrayList<File>();
	if (ObjectUtils.available(deploymetDirss)) {
	    String path;
	    for (DeploymentDirectory deployment : deploymetDirss) {
		path = deployment.getPath();
		files = new File(path).listFiles(new DeployFiletr());
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

	Set<String> paths = MetaContainer.CONFIG.getDataSourcePath();
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

	MetaContainer.undeploy(url);
    }

    public static void undeployFile(String fileName) throws IOException {

	WatchFileType type = checkType(fileName);
	if (type.equals(WatchFileType.DATA_SOURCE)) {
	    DataSourceInitializer.undeploy(fileName);
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
	    LOG.info(String.format("Modify: %s, count: %s\n", fileName, count));
	    redeployFile(fileName);
	} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
	    LOG.info(String.format("Delete: %s, count: %s\n", fileName, count));
	    undeployFile(fileName);
	} else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
	    LOG.info(String.format("Create: %s, count: %s\n", fileName, count));
	    redeployFile(fileName);
	}
    }

    @SuppressWarnings("unchecked")
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
			handleEvent(dir, (WatchEvent<Path>) currentEvent);
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
		    for (File file : files) {
			path = file.getPath();
			registerPath(fs, path, watch);
		    }
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
	    MetaCreator.closeAllConnections();
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
