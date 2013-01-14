package org.lightmare.ejb.startup;

import static org.lightmare.ejb.meta.MetaContainer.closeConnections;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Entity;

import org.apache.log4j.Logger;
import org.lightmare.annotations.UnitName;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.ejb.meta.TmpResources;
import org.lightmare.jpa.JPAManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.scannotation.AnnotationDB;
import org.lightmare.utils.AbstractIOUtils;

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

	private boolean swapDataSource;

	private String dataSourcePath;

	private Map<String, AbstractIOUtils> aggregateds = new HashMap<String, AbstractIOUtils>();

	private static final Logger LOG = Logger.getLogger(MetaCreator.class);

	private MetaCreator() {
	}

	public AnnotationDB getAnnotationDB() {
		return annotationDB;
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

	private void filterEntitiesForJar(Set<String> classSet,
			String fileNameForBean) {

		Map<String, String> classOwnersFiles = annotationDB
				.getClassOwnersFiles();

		String fileNameForEntity;
		for (String entityName : classSet) {
			fileNameForEntity = classOwnersFiles.get(entityName);
			if (fileNameForEntity != null && fileNameForBean != null
					&& !fileNameForEntity.equals(fileNameForBean)) {
				classSet.remove(entityName);
			}
		}
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

	protected void configureConnection(String unitName, String beanName,
			String jndiName) throws IOException {

		JPAManager.Builder builder = new JPAManager.Builder();
		Map<String, String> classOwnersFiles = annotationDB
				.getClassOwnersFiles();
		AbstractIOUtils ioUtils = aggregateds.get(beanName);

		if (ioUtils != null) {
			URL jarURL = ioUtils.getAppropriatedURL(classOwnersFiles, beanName);
			builder.setURL(jarURL);
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
			if (ioUtils != null) {
				String fileNameForBean = classOwnersFiles.get(beanName);
				filterEntitiesForJar(classSet, fileNameForBean);
			}
			List<String> classes = filterEntities(classSet);
			builder.setClasses(classes);
		}
		builder.setPath(persXmlPath).setProperties(prop)
				.setSwapDataSource(swapDataSource)
				.setDataSourcePath(dataSourcePath).build()
				.setConnection(unitName, jndiName);
	}

	private void addURLToList(Enumeration<URL> urlEnum, List<URL> urlList) {
		while (urlEnum.hasMoreElements()) {
			urlList.add(urlEnum.nextElement());
		}
	}

	private URL[] getFullArchives(URL[] archives,
			Map<URL, AbstractIOUtils> archivesURLs) throws IOException {
		List<URL> modifiedArchives = new ArrayList<URL>();
		AbstractIOUtils ioUtils;
		List<URL> ejbURLs;
		for (URL archive : archives) {
			ioUtils = AbstractIOUtils.getAppropriatedType(archive);
			if (ioUtils != null) {
				ioUtils.scan(persXmlFromJar);
				ejbURLs = ioUtils.getEjbURLs();
				modifiedArchives.addAll(ejbURLs);
				if (ejbURLs.isEmpty()) {
					archivesURLs.put(archive, ioUtils);
				} else {
					for (URL ejbURL : ejbURLs) {
						archivesURLs.put(ejbURL, ioUtils);
					}
				}
			}
		}

		return modifiedArchives.toArray(new URL[modifiedArchives.size()]);
	}

	private void deployBean(String beanName, Map<String, URL> classOwnersURL,
			Map<URL, AbstractIOUtils> archivesURLs) throws IOException {
		URL currentURL = classOwnersURL.get(beanName);
		AbstractIOUtils ioUtils = archivesURLs.get(currentURL);
		if (ioUtils == null) {
			ioUtils = AbstractIOUtils.getAppropriatedType(currentURL);
		}
		ClassLoader loader = null;
		if (ioUtils != null) {
			if (!ioUtils.isExecuted()) {
				ioUtils.scan(persXmlFromJar);
			}
			URL[] libURLs = ioUtils.getURLs();
			loader = LibraryLoader.getEnrichedLoader(libURLs);
			aggregateds.put(beanName, ioUtils);
		}
		Boolean future = BeanLoader.loadBean(this, beanName, loader);
		if (future) {
			LOG.info(String.format("bean %s deployed", beanName));
		} else {
			LOG.error(String.format("Could not deploy bean %s", beanName));
		}
	}

	private void deployBeans(Set<String> beanNames,
			Map<String, URL> classOwnersURL,
			Map<URL, AbstractIOUtils> archivesURLs) {
		for (String beanName : beanNames) {
			LOG.info(String.format("deploing bean %s", beanName));
			try {
				deployBean(beanName, classOwnersURL, archivesURLs);
			} catch (IOException ex) {
				LOG.error(String.format("Could not deploy bean %s", beanName),
						ex);
			} catch (Exception ex) {
				LOG.error(String.format("Could not deploy bean %s", beanName),
						ex);
			}
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

		try {
			// Loads libraries from specified path
			if (libraryPath != null) {
				LibraryLoader.loadLibraries(libraryPath);
			}
			Map<URL, AbstractIOUtils> archivesURLs = new HashMap<URL, AbstractIOUtils>();
			URL[] fullArchives = getFullArchives(archives, archivesURLs);
			annotationDB = new AnnotationDB();
			annotationDB.setScanFieldAnnotations(false);
			annotationDB.setScanParameterAnnotations(false);
			annotationDB.setScanMethodAnnotations(false);
			annotationDB.scanArchives(fullArchives);
			Set<String> beanNames = annotationDB.getAnnotationIndex().get(
					Stateless.class.getName());
			Map<String, URL> classOwnersURL = annotationDB.getClassOwnersURLs();
			deployBeans(beanNames, classOwnersURL, archivesURLs);
		} finally {
			// gets read from all created temporary files
			TmpResources.removeTempFiles();
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
	public void scanForBeans(String... applicationPaths)
			throws ClassNotFoundException, IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String defaultPath = "";
		if (applicationPaths.length == 0) {
			applicationPaths = new String[1];
			applicationPaths[0] = defaultPath;
		}
		List<URL> urlList = new ArrayList<URL>();
		Enumeration<URL> urlEnum;
		for (String resourcePath : applicationPaths) {
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
			creator.prop = properties;
			return this;
		}

		public Builder setScanForEntities(boolean scanForEnt) {
			creator.scanForEntities = scanForEnt;
			return this;
		}

		public Builder setUnitName(String unitName) {
			creator.annotatedUnitName = unitName;
			return this;
		}

		public Builder setPersXmlPath(String path) {
			creator.persXmlPath = path;
			return this;
		}

		public Builder setLibraryPath(String libPath) {
			creator.libraryPath = libPath;
			return this;
		}

		public Builder setXmlFromJar(boolean xmlFromJar) {
			creator.persXmlFromJar = xmlFromJar;
			return this;
		}

		public Builder setSwapDataSource(boolean swapDataSource) {
			creator.swapDataSource = swapDataSource;
			return this;
		}

		public Builder setDataSourcePath(String dataSourcePath) {
			creator.dataSourcePath = dataSourcePath;
			return this;
		}

		public MetaCreator build() {
			return creator;
		}

	}
}
