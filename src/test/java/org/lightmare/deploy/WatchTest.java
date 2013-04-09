package org.lightmare.deploy;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Test;

public class WatchTest {

    @Test
    public void pathTest(){
	
	FileSystem fs = FileSystems.getDefault();
	Path deployPath = fs.getPath("./lib");
	String fileName = deployPath.getFileName().toString();
	String pathString = deployPath.toString();
	System.out.println(fileName);
	System.out.println(pathString);
    }
}
