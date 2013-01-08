package org.lightmare.utils.earfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.lightmare.ejb.meta.TmpResources;
import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.IOUtils;

/**
 * Utility class for checking jar, ear and zip files read contents and etc.
 * 
 * @author levan
 * 
 */
public class EarUtils extends IOUtils {

	public EarUtils(String path) {
		super(path);
	}

	public EarUtils(File file) {
		super(file);
	}

	public EarUtils(URL url) throws URISyntaxException {
		super(url);
	}

	@Override
	public InputStream earReader() throws IOException {
		InputStream xmlStream;
		ZipFile zipFile = getEarFile();
		ZipEntry entry = zipFile.getEntry("META-INF/application.xml");
		if (entry == null) {
			xmlStream = null;
		} else {
			xmlStream = zipFile.getInputStream(entry);
		}

		return xmlStream;
	}

	@Override
	public void getEjbLibs() throws IOException {

		URL earURL = realFile.toURI().toURL();

		Enumeration<? extends ZipEntry> entries = getEarFile().entries();
		String earPath;
		ZipEntry libEntry;
		String libPath;
		while (entries.hasMoreElements()) {
			libEntry = entries.nextElement();
			libPath = libEntry.toString();
			if ((libPath.startsWith("lib/") && !libPath.endsWith("lib/"))
					|| libPath.endsWith(".jar")) {
				earPath = String.format("%s!/%s", earURL.toString(), libPath);
				URL url = new URL("jar", "", earPath);
				getLibURLs().add(url);
			}
		}
	}

	/**
	 * Writes ejb jar {@link File} to temporal file to keep {@link URL} from
	 * persistence.xml
	 * 
	 * @param entry
	 * @return {@link URL}
	 * @throws IOException
	 */
	public URL extractEjbJar(ZipEntry entry) throws IOException {
		URL url = null;
		File tmpFile;
		InputStream jarStream = getEarFile().getInputStream(entry);
		FileOutputStream output = null;
		try {
			if (jarStream != null) {
				tmpFile = File.createTempFile(UUID.randomUUID().toString(),
						".jar");
				TmpResources.tmpFiles.add(tmpFile);
				output = new FileOutputStream(tmpFile);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = jarStream.read(buffer)) != -1) {
					output.write(buffer, 0, len);
				}
				URL jarURL = tmpFile.toURI().toURL();
				String jarPath = String.format("%s!/%s", jarURL.toString(),
						ConfigLoader.XML_PATH);
				url = new URL("jar", "", jarPath);
			}

			return url;
		} finally {
			if (jarStream != null) {
				jarStream.close();
			}

			if (output != null) {
				output.close();
			}
		}
	}

	@Override
	public boolean checkOnOrm(String jarName) throws IOException {
		ZipFile zipFile = getEarFile();
		ZipEntry jarEntry = zipFile.getEntry(jarName);
		InputStream stream = zipFile.getInputStream(jarEntry);
		ZipInputStream zipStream = new ZipInputStream(stream);
		ZipEntry xmlEntry = zipStream.getNextEntry();
		boolean check = false;
		while (xmlEntry != null && !check) {
			check = xmlEntry.getName().equals(ConfigLoader.XML_PATH);
			if (!check) {
				xmlEntry = zipStream.getNextEntry();
			}
		}

		return check;
	}

	@Override
	public void extractEjbJars(Set<String> jarNames) throws IOException {
		ZipFile earFile = getEarFile();
		URL url;
		ZipEntry jarEntry;
		String earPath = realFile.toURI().toURL().toString();
		String jarPath;
		URL jarURL;
		boolean checkOnOrm;
		for (String jarName : jarNames) {
			checkOnOrm = checkOnOrm(jarName);
			if (checkOnOrm) {
				jarPath = String.format("%s!/%s", earPath, jarName);
				jarURL = new URL("jar", "", jarPath);
				getEjbURLs().add(jarURL);
				jarEntry = earFile.getEntry(jarName);
				url = extractEjbJar(jarEntry);
				getXmlFiles().put(jarName, jarURL);
				getXmlURLs().put(jarURL, url);
			}
		}
	}

	public void checkFile() throws IOException {
		if (path.endsWith(".ear") && !isDirectory) {
			getEjbLibs();
		}
	}
}
