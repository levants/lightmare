package ge.gov.mia.lightmare.ejb.startup;

import static ge.gov.mia.lightmare.ejb.meta.MetaContainer.addMetaData;
import static ge.gov.mia.lightmare.ejb.meta.MetaContainer.checkMetaData;
import static ge.gov.mia.lightmare.ejb.meta.MetaContainer.closeConnections;
import ge.gov.mia.lightmare.annotations.UnitName;
import ge.gov.mia.lightmare.ejb.exceptions.BeanInUseException;
import ge.gov.mia.lightmare.ejb.meta.MetaData;
import ge.gov.mia.lightmare.jpa.ConfigLoader;
import ge.gov.mia.lightmare.jpa.JPAManager;
import ge.gov.mia.lightmare.libraries.LibraryLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.scannotation.AnnotationDB;

/**
 * Determines and saves in cache ejb beans {@link MetaData} on startup
 * 
 * @author Levan
 * 
 */
public class MetaCreator {

	private static AnnotationDB annotationDB;

	private Map<String, String> prop;

	private boolean scanForEntities;

	private String annotatedUnitName;

	private String persXmlPath;

	private String libraryPath;

	private boolean persXmlFromJar;

	private URL currentURL;

	private boolean swapDataSource;

	private String dataSourcePath;

	private static final Logger LOG = Logger.getLogger(MetaCreator.class);

	private MetaCreator() {
	}

	public AnnotationDB getAnnotationDB() {
		return annotationDB;
	}

	private void setPersistenceProperties(Map<String, String> properties) {
		prop = properties;
	}

	private void setScanForEntities(boolean scanForEnt) {
		scanForEntities = scanForEnt;
	}

	private void setUnitName(String unitName) {
		annotatedUnitName = unitName;
	}

	private void setPersXmlPath(String path) {
		persXmlPath = path;
	}

	private void setLibraryPath(String libPath) {
		libraryPath = libPath;
	}

	private void setPersXmlFromJar(boolean persXmlFromJar) {
		this.persXmlFromJar = persXmlFromJar;
	}

	private void setSwapDataSource(boolean swapDataSource) {
		this.swapDataSource = swapDataSource;
	}

	private void setDataSourcePath(String dataSourcePath) {
		this.dataSourcePath = dataSourcePath;
	}

	private boolean checkForUnitName(String className) throws IOException {
		boolean isValid = false;
		Class<?> entityClass;
		try {
			entityClass = Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new IOException(ex);
		}
		UnitName annotation = entityClass.getAnnotation(UnitName.class);
		isValid = annotation.value().equals(annotatedUnitName);

		return isValid;
	}

	private List<String> translateToList(Set<String> classSet) {
		return Arrays.asList(classSet.toArray(new String[classSet.size()]));
	}

	private List<String> filterEntities(Set<String> classSet)
			throws IOException {
		List<String> classes;
		if (annotatedUnitName == null) {
			classes = translateToList(classSet);
		} else {
			Set<String> filtereds = new HashSet<String>();
			for (String className : classSet) {
				if (checkForUnitName(className)) {
					filtereds.add(className);
				}
			}
			classes = translateToList(filtereds);
		}

		return classes;
	}

	private void configureConnection(String unitName) throws IOException {

		JPAManager.Builder builder = new JPAManager.Builder();

		if (persXmlFromJar && currentURL != null) {
			try {
				String jarPath = String.format("%s!/%s", currentURL.toString(),
						ConfigLoader.XML_PATH);
				URL jarURL = new URL("jar", "", jarPath);
				builder.setURL(jarURL);
			} catch (MalformedURLException ex) {
				throw new IOException(ex);
			}
		}
		if (scanForEntities) {
			Set<String> classSet;
			Map<String, Set<String>> annotationIndex = annotationDB
					.getAnnotationIndex();
			classSet = annotationIndex.get(Entity.class.getName());
			if (annotatedUnitName == null) {
				classSet = annotationIndex.get(Entity.class.getName());
			} else if (annotatedUnitName.equals(unitName)) {
				Set<String> unitNamedSet = annotationIndex.get(UnitName.class
						.getName());
				classSet.retainAll(unitNamedSet);
			}
			List<String> classes = filterEntities(classSet);
			builder.setClasses(classes);
		}
		builder.setPath(persXmlPath).setProperties(prop)
				.setSwapDataSource(swapDataSource)
				.setDataSourcePath(dataSourcePath).build()
				.setConnection(unitName);
	}

	/**
	 * Creates {@link MetaData} for bean class
	 * 
	 * @param beanClass
	 * @throws ClassNotFoundException
	 */
	private MetaData createMeta(Class<?> beanClass) throws IOException {
		Field[] fields = beanClass.getDeclaredFields();
		PersistenceContext context;
		String unitName;
		MetaData metaData = new MetaData();
		metaData.setBeanClass(beanClass);

		for (Field field : fields) {
			context = field.getAnnotation(PersistenceContext.class);
			if (context != null) {
				metaData.setConnectorField(field);
				unitName = context.unitName();
				configureConnection(unitName);
				metaData.setUnitName(unitName);
				break;
			}
		}

		return metaData;
	}

	private void createBeanClass(String name) throws IOException {
		if (checkMetaData(name)) {
			throw new BeanInUseException(String.format(
					"bean % is alredy in use", name));
		} else {
			try {
				Class<?> beanClass = Class.forName(name);
				MetaData metaData = createMeta(beanClass);
				Stateless annotation = beanClass.getAnnotation(Stateless.class);
				String beanName = annotation.name();
				if (beanName == null || beanName.isEmpty()) {
					beanName = beanClass.getSimpleName();
				}
				addMetaData(beanName, metaData);
			} catch (ClassNotFoundException ex) {
				throw new IOException(ex);
			}
		}
	}

	private void addURLToList(Enumeration<URL> urlEnum, List<URL> urlList) {
		while (urlEnum.hasMoreElements()) {
			urlList.add(urlEnum.nextElement());
		}
	}

	/**
	 * Scan application for find all {@link Stateless} beans and {@link Remote}
	 * or {@link Local} proxy interfaces
	 * 
	 * @param archives
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void scanForBeans(URL[] archives) throws IOException {

		// Loads libraries from specified path
		if (libraryPath != null) {
			LibraryLoader.loadLibraries(libraryPath);
		}
		annotationDB = new AnnotationDB();
		annotationDB.setScanFieldAnnotations(false);
		annotationDB.setScanParameterAnnotations(false);
		annotationDB.setScanMethodAnnotations(false);
		annotationDB.scanArchives(archives);
		Set<String> beanNames = annotationDB.getAnnotationIndex().get(
				Stateless.class.getName());
		for (String name : beanNames) {
			LOG.info(String.format("deploing bean %s", name));
			try {
				if (archives.length == 1) {
					currentURL = archives[0];
				}
				createBeanClass(name);
				LOG.info(String.format("bean %s deployed", name));
			} catch (IOException ex) {
				LOG.error(String.format("Could not deploy bean %s", name), ex);
			} catch (Exception ex) {
				LOG.error(String.format("Could not deploy bean %s", name), ex);
			}
		}
	}

	/**
	 * Scan application for find all {@link Stateless} beans and {@link Remote}
	 * or {@link Local} proxy interfaces
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void scanForBeans(File[] jars) throws IOException,
			ClassNotFoundException {
		List<URL> urlList = new ArrayList<URL>();
		URL url;
		for (File file : jars) {
			url = file.toURI().toURL();
			urlList.add(url);
		}
		URL[] archives = urlList.toArray(new URL[urlList.size()]);
		scanForBeans(archives);
	}

	/**
	 * Scan application for find all {@link Stateless} beans and {@link Remote}
	 * or {@link Local} proxy interfaces
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void scanForBeans(String... paths) throws ClassNotFoundException,
			IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String defaultPath = "";
		if (paths.length == 0) {
			paths = new String[1];
			paths[0] = defaultPath;
		}
		List<URL> urlList = new ArrayList<URL>();
		Enumeration<URL> urlEnum;
		for (String resourcePath : paths) {
			urlEnum = loader.getResources(resourcePath);
			addURLToList(urlEnum, urlList);
		}
		URL[] archives = urlList.toArray(new URL[urlList.size()]);
		scanForBeans(archives);
	}

	/**
	 * Closes all existing connections
	 */
	public static void closeAllConnections() {
		closeConnections();
	}

	/**
	 * Builder class to provide properties for lightmare
	 * 
	 * @author levan
	 * 
	 */
	public static class Builder {

		private MetaCreator creator;

		public Builder() {
			creator = new MetaCreator();
		}

		public Builder setPersistenceProperties(Map<String, String> properties) {
			creator.setPersistenceProperties(properties);
			return this;
		}

		public Builder setScanForEntities(boolean scanForEnt) {
			creator.setScanForEntities(scanForEnt);
			return this;
		}

		public Builder setUnitName(String unitName) {
			creator.setUnitName(unitName);
			return this;
		}

		public Builder setPersXmlPath(String path) {
			creator.setPersXmlPath(path);
			return this;
		}

		public Builder setLibraryPath(String libPath) {
			creator.setLibraryPath(libPath);
			return this;
		}

		public Builder setXmlFromJar(boolean xmlFromJar) {
			creator.setPersXmlFromJar(xmlFromJar);
			return this;
		}

		public Builder setSwapDataSource(boolean swapDataSource) {
			creator.setSwapDataSource(swapDataSource);
			return this;
		}

		public Builder setDataSourcePath(String dataSourcePath) {
			creator.setDataSourcePath(dataSourcePath);
			return this;
		}

		public MetaCreator build() {
			return creator;
		}

	}
}
