package org.lightmare.ejb;

import static org.lightmare.ejb.meta.MetaContainer.getMetaData;
import static org.lightmare.jpa.JPAManager.getConnection;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;

import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.ejb.handlers.BeanLocalHandler;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.ejb.startup.MetaCreator;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.remote.rpc.RPCall;

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
	private <T> T getBeanInstance(MetaData metaData)
			throws InstantiationException, IllegalAccessException {

		Class<? extends T> beanClass = (Class<? extends T>) metaData
				.getBeanClass();

		T beanInstance = beanClass.newInstance();

		return beanInstance;
	}

	/**
	 * Creates {@link InvocationHandler} implementation for server mode
	 * 
	 * @param metaData
	 * @return {@link InvocationHandler}
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T> InvocationHandler getHandler(MetaData metaData)
			throws InstantiationException, IllegalAccessException {

		LibraryLoader.loadCurrentLibraries(metaData.getLoader());

		T beanInstance = getBeanInstance(metaData);

		EntityManagerFactory emf = getEntityManagerFactory(metaData);
		Field connectorField = metaData.getConnectorField();
		InvocationHandler handler = new BeanHandler(connectorField,
				beanInstance, emf);

		return handler;
	}

	/**
	 * Creates custom implementation of bean {@link Class}
	 * 
	 * @param interfaceClass
	 * @return T implementation of bean interface
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> T connectToBean(String beanName, Class<T> interfaceClass)
			throws IOException {
		InvocationHandler handler;
		if (MetaCreator.configuration.isRemote()) {
			handler = new BeanLocalHandler(new RPCall());
		} else {
			MetaData metaData = getMetaData(beanName);
			try {
				handler = getHandler(metaData);
			} catch (InstantiationException ex) {
				throw new IOException(ex);
			} catch (IllegalAccessException ex) {
				throw new IOException(ex);
			}
		}

		Class<?>[] interfaceArray = { interfaceClass };
		T beanInstance = (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), interfaceArray, handler);

		return beanInstance;
	}
}
