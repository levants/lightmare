package org.lightmare.ejb.meta;

import static org.lightmare.jpa.JPAManager.closeEntityManagerFactories;

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

	private static ConcurrentMap<String, MetaData> ejbs = new ConcurrentHashMap<String, MetaData>();

	public static void addMetaData(String beanName, MetaData metaData) {
		ejbs.put(beanName, metaData);
	}

	public static boolean checkMetaData(String beanName) {
		return ejbs.containsKey(beanName);
	}

	public static MetaData getMetaData(String beanName) {
		return ejbs.get(beanName);
	}

	public static EntityManagerFactory getConnection(String unitName) {
		return JPAManager.getConnection(unitName);
	}

	public static void closeConnections() {
		closeEntityManagerFactories();
	}
}
