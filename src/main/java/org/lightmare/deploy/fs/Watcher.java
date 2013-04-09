package org.lightmare.deploy.fs;

import java.io.File;
import java.io.IOException;
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

    private void handleEvent(WatchEvent<?> currentEvent) throws Exception {
	if (currentEvent == null) {
	    return;
	}
	String fileName = currentEvent.context().toString();
	int count = currentEvent.count();
	Kind<?> kind = currentEvent.kind();
	if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
	    LOG.info(String.format("Modify: %s, count: %s\n", fileName, count));
	    // redeployFile(fileName);
	} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
	    LOG.info(String.format("Delete: %s, count: %s\n", fileName, count));
	    // undeployFile(fileName);
	} else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
	    LOG.info(String.format("Create: %s, count: %s\n", fileName, count));
	    // redeployFile(fileName);
	}
    }

    private void runService(WatchService watch) throws Exception {

	while (true) {
	    WatchKey key = watch.take();
	    List<WatchEvent<?>> events = key.pollEvents();
	    String fileName;
	    WatchEvent<?> currentEvent = null;
	    int times = 0;
	    for (WatchEvent<?> event : events) {
		fileName = event.context().toString();
		if (event.kind() == StandardWatchEventKinds.OVERFLOW
			|| !(fileName.endsWith(".jar") || fileName
				.endsWith("-ds.xml"))) {
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
