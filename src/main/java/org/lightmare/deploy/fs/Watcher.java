package org.lightmare.deploy.fs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.lightmare.cache.MetaContainer;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.FileParsers;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;
import org.lightmare.utils.fs.WatchUtils;

/**
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

    private static final Logger LOG = Logger.getLogger(Watcher.class);

    private MetaCreator creator;

    public Watcher(MetaCreator creator) {
	this.creator = creator;
    }

    private URL getAppropriateURL(String fileName) throws IOException {

	File file = new File(fileName);
	URL url = file.toURI().toURL();
	url = WatchUtils.clearURL(url);

	return url;
    }

    private void deployFile(String fileName) throws IOException {
	if (fileName.endsWith(".xml")) {
	    FileParsers fileParsers = new FileParsers();
	    fileParsers.parseStandaloneXml(fileName);
	} else {
	    URL url = getAppropriateURL(fileName);
	    deployFile(url);
	}
    }

    private void deployFile(URL url) throws IOException {

	URL[] archives = { url };
	creator.scanForBeans(archives);
    }

    private void undeployFile(URL url) throws IOException {

	MetaContainer.undeploy(url);
	creator.clear();
    }

    private void undeployFile(String fileName) throws IOException {

	if (fileName.endsWith(".xml")) {
	    DataSourceInitializer.undeploy(fileName);
	} else {
	    URL url = getAppropriateURL(fileName);
	    undeployFile(url);
	}
    }

    private void redeployFile(String fileName) throws IOException {

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
	    Path deployPath;
	    Set<String> deployments = MetaCreator.CONFIG.getDeploymentPath();
	    if (ObjectUtils.available(deployments)) {
		for (String path : deployments) {
		    deployPath = fs.getPath(path);
		    deployPath.register(watch,
			    StandardWatchEventKinds.ENTRY_CREATE,
			    StandardWatchEventKinds.ENTRY_MODIFY,
			    StandardWatchEventKinds.OVERFLOW,
			    StandardWatchEventKinds.ENTRY_DELETE);
		    runService(watch);
		}
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

    public static void startWatch(MetaCreator creator) {

	Watcher watcher = new Watcher(creator);
	DEPLOY_POOL.submit(watcher);
    }
}
