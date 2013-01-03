package ge.gov.mia.lightmare;

import ge.gov.mia.lightmare.jpa.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Test;

public class JarFileReaderTest {

	public static final String JAR_PATH = "./lib/loader-tester-0.0.1-SNAPSHOT.jar";
	private JarFile jarFile;
	private Scanner scanner;

	@Test
	public void readEntryTest() {
		try {
			jarFile = new JarFile(JAR_PATH);
			JarEntry xmlEntry = jarFile.getJarEntry(ConfigLoader.XML_PATH);
			System.out.println(xmlEntry);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				System.out.println(entries.nextElement());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void readEntryStreamTest() {
		try {
			jarFile = new JarFile(JAR_PATH);
			JarEntry xmlEntry = jarFile.getJarEntry(ConfigLoader.XML_PATH);
			if (xmlEntry != null) {
				InputStream stream = jarFile.getInputStream(xmlEntry);
				scanner = new Scanner(stream);
				while (scanner.hasNextLine()) {
					System.out.println(scanner.nextLine());
				}
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void readUrlTest() {
		File file = new File(JAR_PATH);
		String filePath = file.getPath();
		System.out.println(filePath);
		try {
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
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
