package org.lightmare.jpa;


import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.FileParsers;

/**
 * Creates and caches {@link EntityManagerFactory} for each ejb bean
 * {@link Class}'s appropriate field (annotated by @PersistenceContext)
 * 
 * @author Levan
 * 
 */
public class JPAManager {

	// Keeps unique EntityManagerFactories builded by unit names
	private static ConcurrentMap<String, EntityManagerFactory> connections = new ConcurrentHashMap<String, EntityManagerFactory>();

	private List<String> classes;

	private String path;

	private URL url;

	private Map<?, ?> properties;

	private boolean swapDataSource;

	private String dataSourcePath;

	private JPAManager() {
	}

	/**
	 * Creates {@link EntityManagerFactory} by hibernate or by extended builder
	 * {@link Ejb3ConfigurationImpl} if entity classes or persistence.xml file
	 * path are provided
	 * 
	 * @see Ejb3ConfigurationImpl#configure(String, Map) and
	 *      Ejb3ConfigurationImpl#createEntityManagerFactory()
	 * 
	 * @param unitName
	 * @return {@link EntityManagerFactory}
	 */
	@SuppressWarnings("deprecation")
	private EntityManagerFactory buildEntityManagerFactory(String unitName)
			throws IOException {
		EntityManagerFactory emf;
		Ejb3ConfigurationImpl cfg;

		boolean checkForPath = checkForPath();
		boolean checkForURL = checkForURL();
		boolean checkForClasses = checkForClasses();
		if (checkForPath || checkForURL) {
			Enumeration<URL> xmls;
			ConfigLoader configLoader = new ConfigLoader();
			if (checkForPath) {
				try {
					xmls = configLoader.readFile(path);
				} catch (IOException ex) {
					throw new PersistenceException(ex);
				}
			} else {
				xmls = configLoader.readURL(url);
			}
			if (checkForClasses) {
				cfg = new Ejb3ConfigurationImpl(classes, xmls);
			} else {
				cfg = new Ejb3ConfigurationImpl(xmls);
			}
			cfg.setShortPath(configLoader.getShortPath());
		} else {
			cfg = new Ejb3ConfigurationImpl(classes);
		}

		cfg.setSwapDataSource(swapDataSource);
		if (checkForDataSource()
				&& !DataSourceInitializer.checkDSPath(dataSourcePath)) {
			FileParsers parsers = new FileParsers();
			parsers.parseStandaloneXml(dataSourcePath);
		}

		Ejb3ConfigurationImpl configured = cfg.configure(unitName, properties);

		emf = configured != null ? configured.buildEntityManagerFactory()
				: null;
		return emf;
	}

	/**
	 * Checks if entity classes are provided
	 * 
	 * @return boolean
	 */
	private boolean checkForClasses() {
		return classes != null && !classes.isEmpty();
	}

	/**
	 * Checks if entity persistence.xml path is provided
	 * 
	 * @return boolean
	 */
	private boolean checkForPath() {
		return path != null && !path.isEmpty();
	}

	/**
	 * Checks if entity persistence.xml {@link URL} is provided
	 * 
	 * @return boolean
	 */
	private boolean checkForURL() {
		return url != null && !url.toString().isEmpty();
	}

	/**
	 * Checks if DataSource path is provided
	 * 
	 * @return boolean
	 */
	private boolean checkForDataSource() {
		return dataSourcePath != null && !dataSourcePath.isEmpty();
	}

	/**
	 * Checks if entity classes or persistence.xml path are provided
	 * 
	 * @param classes
	 * @return boolean
	 */
	private boolean checkForBuild() {
		return checkForClasses() || checkForPath() || checkForURL()
				|| checkForDataSource();
	}

	/**
	 * Checks if entity classes or persistence.xml file path are provided to
	 * create {@link EntityManagerFactory}
	 * 
	 * @see #buildEntityManagerFactory(String, String, Map, List)
	 * 
	 * @param unitName
	 * @param properties
	 * @param path
	 * @param classes
	 * @return {@link EntityManagerFactory}
	 * @throws IOException
	 */
	private EntityManagerFactory createEntityManagerFactory(String unitName)
			throws IOException {
		EntityManagerFactory emf;
		if (checkForBuild()) {
			emf = buildEntityManagerFactory(unitName);
		} else if (properties == null) {
			emf = Persistence.createEntityManagerFactory(unitName);
		} else {
			emf = Persistence.createEntityManagerFactory(unitName, properties);
		}

		return emf;
	}

	public void setConnection(String unitName) throws IOException {
		if (!connections.containsKey(unitName)) {
			EntityManagerFactory emf = createEntityManagerFactory(unitName);
			connections.put(unitName, emf);
		}
	}

	public static EntityManagerFactory getConnection(String unitName) {
		return connections.get(unitName);
	}

	/**
	 * Closes all existing {@link EntityManagerFactory} instances kept in cache
	 */
	public static void closeEntityManagerFactories() {
		Collection<EntityManagerFactory> emfs = connections.values();
		for (EntityManagerFactory emf : emfs) {
			emf.close();
		}
	}

	public static class Builder {

		private JPAManager manager;

		public Builder() {
			manager = new JPAManager();
		}

		public Builder setClasses(List<String> classes) {
			manager.classes = classes;
			return this;
		}

		public Builder setURL(URL url) {
			manager.url = url;
			return this;
		}

		public Builder setPath(String path) {
			manager.path = path;
			return this;
		}

		public Builder setProperties(Map<?, ?> properties) {
			manager.properties = properties;
			return this;
		}

		public Builder setSwapDataSource(boolean swapDataSource) {
			manager.swapDataSource = swapDataSource;
			return this;
		}

		public Builder setDataSourcePath(String dataSourcePath) {
			manager.dataSourcePath = dataSourcePath;
			return this;
		}

		public JPAManager build() {
			return manager;
		}
	}

}
