package org.lightmare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.ejb.meta.TmpResources;
import org.lightmare.jpa.ConfigLoader;
import org.lightmare.utils.IOUtils;
import org.lightmare.utils.earfile.EarUtils;

public class EarFileReaderTest {

	private static final String EAR_PATH = "./lib/loader-tester.ear";

	public static final String JAR_IN_EAR_PATH = "loader-tester.jar";

	private JarFile jarFile;

	private Scanner scanner;

	@Test
	public void readEntryTest() {
		try {
			jarFile = new JarFile(EAR_PATH);
			JarEntry xmlEntry = jarFile.getJarEntry(JAR_IN_EAR_PATH);
			System.out.println(xmlEntry);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				System.out.println(entries.nextElement());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void readEntryAttributesTest() {
		try {
			jarFile = new JarFile(EAR_PATH);
			Enumeration<JarEntry> entries = jarFile.entries();
			Object key;
			Object value;
			while (entries.hasMoreElements()) {
				Attributes attributes = entries.nextElement().getAttributes();
				if (attributes == null) {
					continue;
				}
				for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
					key = entry.getKey();
					value = entry.getValue();
					System.out.format("keyClass %s : valueClass %s\n",
							key.getClass(), value.getClass());
					System.out.format("key %s : value %s\n", key, value);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void readEntryLibTest() {
		try {
			jarFile = new JarFile(EAR_PATH);
			Enumeration<JarEntry> entries = jarFile.entries();
			File file = new File(EAR_PATH);
			URL earURL = file.toURI().toURL();
			String earPath;
			JarEntry libEntry;
			String libPath;
			List<URL> urlList = new ArrayList<URL>();
			while (entries.hasMoreElements()) {
				libEntry = entries.nextElement();
				libPath = libEntry.toString();
				if ((libPath.startsWith("lib/") && !libPath.endsWith("lib/"))
						|| libPath.endsWith(".jar")) {
					System.out.println(libPath);
					earPath = String.format("%s!/%s", earURL.toString(),
							libPath);
					URL url = new URL("jar", "", earPath);
					urlList.add(url);
					System.out.println(url);
				}
			}
			URL[] urls = urlList.toArray(new URL[urlList.size()]);
			System.out.println(urls.length);
			ClassLoader currentLoader = Thread.currentThread()
					.getContextClassLoader();
			ClassLoader loader = URLClassLoader
					.newInstance(urls, currentLoader);
			System.out.println(loader);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void urlTests() {
		try {
			File zipFile = new File(EAR_PATH);
			URL earURL = zipFile.toURI().toURL();
			String jarFileParh = String.format("%s!/%s", earURL.toString(),
					JAR_IN_EAR_PATH);
			URL jarInEarURL = new URL("jar", "", jarFileParh);
			System.out.println(jarInEarURL);
			String manifestPath = String.format("%s!/%s", jarInEarURL,
					ConfigLoader.XML_PATH);
			URL url = new URL(manifestPath);
			System.out.println(url);

			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();

			scanner = new Scanner(stream);
			while (scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}
			stream.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void readUrlTest() {
		File file = null;
		try {
			ZipFile zipFile = new ZipFile(EAR_PATH);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			ZipEntry zipEntry = null;
			InputStream jarStream = null;
			String entryName;
			while (entries.hasMoreElements()) {
				zipEntry = entries.nextElement();
				entryName = zipEntry.getName();
				if (entryName.endsWith(".jar") && !entryName.startsWith("lib/")) {
					jarStream = zipFile.getInputStream(zipEntry);
					break;
				}
			}
			if (jarStream == null) {
				return;
			}
			file = File.createTempFile(UUID.randomUUID().toString(), ".jar");

			FileOutputStream output = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = jarStream.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
			output.close();
			System.out.println(file.getPath());
			String filePath = file.getPath();
			System.out.println(filePath);
			URL jarURL = file.toURI().toURL();
			String jarPath = String.format("%s!/%s", jarURL.toString(),
					ConfigLoader.XML_PATH);
			System.out.println(jarPath);
			URL url = new URL("jar", "", jarPath);
			System.out.println(url);
			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();

			scanner = new Scanner(stream);
			while (scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}
			jarStream.close();
			stream.close();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	@Test
	public void appXmlParseTest() {
		IOUtils ioUtils = new EarUtils(EAR_PATH);
		try {
			InputStream stream = ioUtils.earReader();
			Set<String> apps = ioUtils.appXmlParser(stream);
			Assert.assertTrue("could not find ejb applications",
					apps.size() > 0);
			for (String app : apps) {
				System.out.println(app);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void getEjbLibsTest() {
		IOUtils ioUtils = new EarUtils(EAR_PATH);
		try {
			ioUtils.getEjbLibs();
			URL[] urls = ioUtils.getLibs();
			Assert.assertTrue("could not find ejb applications", urls != null
					&& urls.length > 0);
			for (URL url : urls) {
				System.out.println(url);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void extractEjbJarTest() {
		IOUtils ioUtils = new EarUtils(EAR_PATH);
		try {
			InputStream stream = ioUtils.earReader();
			Set<String> apps = ioUtils.appXmlParser(stream);
			Assert.assertTrue("could not find ejb applications",
					apps.size() > 0);
			ioUtils.extractEjbJars(apps);
			Assert.assertTrue("could not extract jar files",
					TmpResources.tmpFiles.size() > 0);
			Map<URL, URL> xmls = ioUtils.getXmlURLs();
			Scanner scanner;
			for (Map.Entry<URL, URL> entry : xmls.entrySet()) {

				stream = entry.getValue().openStream();
				scanner = new Scanner(stream);
				System.out
						.println("==========================\n==========================\n==========================");
				System.out.format("\t\t\t\t\t%s\n", entry.getKey());
				while (scanner.hasNextLine()) {
					System.out.println(scanner.nextLine());
					stream.close();
				}
				System.out
						.println("==========================\n==========================\n==========================");

			}
			Thread.sleep(100);
			TmpResources.removeTempFiles();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void getAppropriateTypeTest() {
		try {
			IOUtils ioUtils = IOUtils.getAppropriateType(new File(EAR_PATH)
					.toURI().toURL());
			Assert.assertTrue("Could not get appropriate type",
					ioUtils instanceof EarUtils);
			System.out.println(ioUtils);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
