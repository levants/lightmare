package org.lightmare.deploy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.utils.ObjectUtils;

@Ignore
public class WatchTest {

    @Test
    public void pathTest() {

	try {
	    FileSystem fs = FileSystems.getDefault();
	    Path deployPath = fs.getPath("./lib");
	    String fileName = deployPath.getFileName().toString();
	    String pathString = deployPath.toString();
	    System.out.println(fileName);
	    System.out.println(pathString);
	    System.out.println(deployPath.isAbsolute());
	    Path root = deployPath.getRoot();
	    if (ObjectUtils.notNull(root)) {
		System.out.println(root.toString());
	    }
	    Iterator<Path> iterator = deployPath.iterator();
	    while (iterator.hasNext()) {
		System.out.println(iterator.next());
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @SuppressWarnings("unchecked")
    @Test
    public void watchTest() {

	FileSystem fs = FileSystems.getDefault();
	WatchService watch = null;
	try {
	    watch = fs.newWatchService();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	String deployDir = String.format("%s/watch",
		System.getProperty("user.home"));
	Path deployPath = fs.getPath(deployDir);
	try {
	    deployPath.register(watch, StandardWatchEventKinds.ENTRY_CREATE,
		    StandardWatchEventKinds.ENTRY_MODIFY,
		    StandardWatchEventKinds.OVERFLOW,
		    StandardWatchEventKinds.ENTRY_DELETE);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	while (true) {
	    try {
		WatchKey key;
		key = watch.take();
		Path dir = (Path) key.watchable();
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
		    Path prePath = ((WatchEvent<Path>) currentEvent).context();
		    Path path = dir.resolve(prePath);
		    System.out.println(path.toString());
		    File file = path.toFile();
		    URL url = path.toUri().toURL();
		    System.out.println(url);
		    System.out.println(file);
		}
	    } catch (InterruptedException ex) {
		ex.printStackTrace();
	    } catch (MalformedURLException ex) {
		ex.printStackTrace();
	    }
	}
    }
}
