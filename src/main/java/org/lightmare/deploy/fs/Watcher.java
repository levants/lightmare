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

import org.apache.log4j.Logger;
import org.lightmare.cache.MetaContainer;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.utils.ObjectUtils;

/**
 * {@link File} modification event handler for deployments if java version is
 * 1.7 or above
 * 
 * @author levan
 * 
 */
public class Watcher implements Runnable {

    private static final Logger LOG = Logger.getLogger(Watcher.class);

    private MetaCreator creator;

    public Watcher(MetaCreator creator) {
	this.creator = creator;
    }

    private URL getAppropriateURL(String fileName) throws IOException {

	File file = new File(fileName);
	URL url = file.toURI().toURL();

	return url;
    }

    private void deployFile(URL url) throws IOException {

	URL[] archives = { url };
	creator.scanForBeans(archives);
    }

    private void undeployFile(URL url) throws IOException {

	MetaContainer.undeploy(url);
    }

    private void undeployFile(String fileName) throws IOException {

	URL url = getAppropriateURL(fileName);
	undeployFile(url);
    }

    private void redeployFile(String fileName) throws IOException {

	URL url = getAppropriateURL(fileName);
	undeployFile(url);
	deployFile(url);

    }

    private void handleEvent(WatchEvent<?> currentEvent) throws Exception {
	if (currentEvent == null) {
	    return;
	}
	String fileName = currentEvent.context().toString();
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

    private void runService(WatchService watch) throws Exception {

	while (true) {
	    WatchKey key = watch.take();
	    List<WatchEvent<?>> events = key.pollEvents();
	    WatchEvent<?> currentEvent = null;
	    int times = 0;
	    for (WatchEvent<?> event : events) {
		if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
		    continue;
		}
		if (times == 0 || event.count() > currentEvent.count()) {
		    currentEvent = event;
		}
		times++;
		boolean valid = key.reset();
		if (!valid || !key.isValid()) {
		    break;
		}
		handleEvent(currentEvent);
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
	} catch (Exception ex) {
	    LOG.fatal(ex.getMessage(), ex);
	    LOG.fatal("system going to shut down cause of hot deployment");
	    MetaCreator.closeAllConnections();
	    System.exit(-1);
	}
    }
}
