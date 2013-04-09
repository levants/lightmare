package org.lightmare.deploy;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Test;
import org.lightmare.utils.ObjectUtils;

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

    public void watchTest() {

    }
}
