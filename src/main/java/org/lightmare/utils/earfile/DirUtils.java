package org.lightmare.utils.earfile;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.IOUtils;

public class DirUtils extends IOUtils {

	public DirUtils(String path) {
		super(path);
	}

	public DirUtils(File file) {
		super(file);
	}

	public DirUtils(URL url) throws URISyntaxException {
		super(url);
	}

	@Override
	public InputStream earReader() throws IOException {

		String xmlPath = "META-INF/application.xml";
		String appXmlPath;
		if (path.endsWith("/")) {
			appXmlPath = String.format("%s%s", path, xmlPath);
		} else {
			appXmlPath = String.format("%s/%s", path, xmlPath);
		}
		File xmlFile = new File(appXmlPath);
		InputStream stream = new FileInputStream(xmlFile);

		return stream;
	}

	@Override
	public void getEjbLibs() throws IOException {

		File realFile = new File(path);

		File[] files = realFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith("lib/") && file.isDirectory();
			}
		});
		File[] libJars;
		for (File file : files) {
			libJars = file.listFiles(new FileFilter() {

				@Override
				public boolean accept(File jarFile) {
					return jarFile.getName().endsWith(".jar")
							&& !jarFile.isDirectory();
				}
			});
			for (File libFile : libJars) {
				URL url = libFile.toURI().toURL();
				getLibURLs().add(url);
			}
		}
	}

	@Override
	public boolean checkOnOrm(String jarName) throws IOException {

		@SuppressWarnings("resource")
		JarEntry xmlEntry = new JarFile(jarName)
				.getJarEntry(ConfigLoader.XML_PATH);
		boolean check = (xmlEntry != null);

		return check;
	}

	@Override
	public void extractEjbJars(Set<String> jarNames) throws IOException {
		String xmlPath;
		if (path.endsWith("/")) {
			xmlPath = path;
		} else {
			xmlPath = String.format("%s/", path);
		}

		String fillXmlPath;
		String jarPath;
		URL currentURL;
		boolean check;
		for (String jarName : jarNames) {
			fillXmlPath = String.format("%s%s", xmlPath, jarName);
			check = checkOnOrm(fillXmlPath);
			if (check) {
				currentURL = new File(fillXmlPath).toURI().toURL();
				jarPath = String.format("%s!/%s", currentURL.toString(),
						ConfigLoader.XML_PATH);
				URL jarURL = new URL("jar", "", jarPath);
				getXmlURLs().put(jarName, jarURL);
			}
		}

	}
}
