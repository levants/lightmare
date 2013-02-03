package org.lightmare.ejb;

import static org.lightmare.ejb.meta.MetaContainer.getMetaData;
import static org.lightmare.jpa.JPAManager.getConnection;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;

import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.ejb.startup.MetaCreator;
import org.lightmare.libraries.LibraryLoader;

/**
 * Connector class for get ejb beans by interface class
 * 
 * @author Levan
 * 
 */
public class EjbConnector {

	/**
	 * Gets connection for {@link Stateless} bean {@link Class} from cache
	 * 
	 * @param unitName
	 * @return {@link EntityManagerFactory}
	 */
	private EntityManagerFactory getEntityManagerFactory(MetaData metaData) {

		String unitName = metaData.getUnitName();

		if (unitName == null || unitName.isEmpty()) {
			return null;
		}
		EntityManagerFactory emf = getConnection(unitName);
		return emf;
	}

	@SuppressWarnings("unchecked")
	private <T> T getBeanInstance(MetaData metaData, boolean remote)
			throws InstantiationException, IllegalAccessException {

		if (!remote) {
			LibraryLoader.loadCurrentLibraries(metaData.getLoader());
		}

		Class<? extends T> beanClass = (Class<? extends T>) metaData
				.getBeanClass();

		T beanInstance = beanClass.newInstance();

		return beanInstance;
	}

	/**
	 * Creates custom implementation of bean {@link Class}
	 * 
	 * @param interfaceClass
	 * @return T implementation of bean interface
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T connectToBean(String beanName, Class<T> interfaceClass)
			throws InstantiationException, IllegalAccessException {
		MetaData metaData = getMetaData(beanName);
		T beanInstance = null;
		if (MetaCreator.configuration.isRemote()) {
		} else {
			LibraryLoader.loadCurrentLibraries(metaData.getLoader());

			Class<? extends T> beanClass = (Class<? extends T>) metaData
					.getBeanClass();

			beanInstance = beanClass.newInstance();

			EntityManagerFactory emf = getEntityManagerFactory(metaData);
			Field connectorField = metaData.getConnectorField();
			BeanHandler handler = new BeanHandler(connectorField, beanInstance,
					emf);
			Class<?>[] interfaceArray = { interfaceClass };
			beanInstance = (T) Proxy.newProxyInstance(Thread.currentThread()
					.getContextClassLoader(), interfaceArray, handler);
		}
		return beanInstance;
	}
}
