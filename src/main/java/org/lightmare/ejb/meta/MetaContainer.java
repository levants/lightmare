package org.lightmare.ejb.meta;

import static org.lightmare.jpa.JPAManager.closeEntityManagerFactories;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;

import org.lightmare.jpa.JPAManager;

/**
 * Container class to save {@link MetaData} for bean interface {@link Class} and
 * connections {@link EntityManagerFactory}es for unit names
 * 
 * @author Levan
 * 
 */
public class MetaContainer {

	// Cached bean meta data
	private static final ConcurrentMap<String, MetaData> EJBS = new ConcurrentHashMap<String, MetaData>();

	public static MetaData addMetaData(String beanName, MetaData metaData) {
		return EJBS.putIfAbsent(beanName, metaData);
	}

	public static boolean checkMetaData(String beanName) {
		boolean check;
		MetaData metaData = EJBS.get(beanName);
		check = metaData == null;
		if (!check) {
			check = metaData.isInProgress();
		}
		return check;
	}

	public boolean checkBean(String beanName) {
		return EJBS.containsKey(beanName);
	}

	public static void awaitMetaData(MetaData metaData) throws IOException {
		boolean inProgress = metaData.isInProgress();
		if (inProgress) {
			synchronized (metaData) {
				while (inProgress) {
					try {
						metaData.wait();
						inProgress = metaData.isInProgress();
					} catch (InterruptedException ex) {
						throw new IOException(ex);
					}
				}
			}
		}
	}

	public static MetaData getMetaData(String beanName) {
		return EJBS.get(beanName);
	}

	public static MetaData getSyncMetaData(String beanName) throws IOException {
		MetaData metaData = getMetaData(beanName);
		if (metaData == null) {
			throw new IOException(String.format("Bean %s is not deployed",
					beanName));
		}
		awaitMetaData(metaData);

		return metaData;
	}

	public static void removeMeta(String beanName) {
		EJBS.remove(beanName);
	}

	public static EntityManagerFactory getConnection(String unitName)
			throws IOException {
		return JPAManager.getEntityManagerFactory(unitName);
	}

	public static void closeConnections() {
		closeEntityManagerFactories();
	}

	public static Iterator<MetaData> getBeanClasses() {
		return EJBS.values().iterator();
	}
}
