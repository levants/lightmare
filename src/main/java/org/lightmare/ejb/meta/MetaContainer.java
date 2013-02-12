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
			synchronized (metaData) {
				check = metaData.isInProgress();
			}
		}
		return check;
	}

	public boolean checkBean(String beanName) {
		return EJBS.containsKey(beanName);
	}

	public static MetaData getMetaData(String beanName) {
		return EJBS.get(beanName);
	}

	public static void removeMeta(String beanName) {
		EJBS.remove(beanName);
	}

	public static EntityManagerFactory getConnection(String unitName)
			throws IOException {
		return JPAManager.getConnection(unitName);
	}

	public static void closeConnections() {
		closeEntityManagerFactories();
	}

	public static Iterator<MetaData> getBeanClasses() {
		return EJBS.values().iterator();
	}
}
