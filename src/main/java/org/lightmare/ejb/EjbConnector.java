package org.lightmare.ejb;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;

import org.lightmare.config.Configuration;
import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.ejb.handlers.BeanLocalHandler;
import org.lightmare.ejb.meta.ConnectionSemaphore;
import org.lightmare.ejb.meta.MetaContainer;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.ejb.startup.MetaCreator;
import org.lightmare.jpa.JPAManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Connector class for get ejb beans or call remote procedure in ejb bean (RPC)
 * by interface class
 * 
 * @author Levan
 * 
 */
public class EjbConnector {

	/**
	 * Gets {@link MetaData} from {@link MetaContainer} and waits while
	 * {@link MetaData#isInProgress()}
	 * 
	 * @param beanName
	 * @return {@link MetaData}
	 * @throws IOException
	 */
	private MetaData getMeta(String beanName) throws IOException {

		MetaData metaData = MetaContainer.getSyncMetaData(beanName);

		return metaData;
	}

	/**
	 * Gets connection for {@link Stateless} bean {@link Class} from cache
	 * 
	 * @param unitName
	 * @return {@link EntityManagerFactory}
	 * @throws IOException
	 */
	private void getEntityManagerFactory(MetaData metaData) throws IOException {

		if (metaData.getEmf() == null) {
			String unitName = metaData.getUnitName();

			if (unitName != null && !unitName.isEmpty()) {
				ConnectionSemaphore semaphore = JPAManager
						.getConnection(unitName);
				metaData.setConnection(semaphore);
			}
		}
	}

	/**
	 * Instantiates bean by class
	 * 
	 * @param metaData
	 * @return Bean instance
	 * @throws IOException
	 */
	private <T> T getBeanInstance(MetaData metaData) throws IOException {

		@SuppressWarnings("unchecked")
		Class<? extends T> beanClass = (Class<? extends T>) metaData
				.getBeanClass();

		T beanInstance = MetaUtils.instantiate(beanClass);

		return beanInstance;
	}

	/**
	 * Creates {@link InvocationHandler} implementation for server mode
	 * 
	 * @param metaData
	 * @return {@link InvocationHandler}
	 * @throws IOException
	 */
	private <T> InvocationHandler getHandler(MetaData metaData)
			throws IOException {

		LibraryLoader.loadCurrentLibraries(metaData.getLoader());

		T beanInstance = getBeanInstance(metaData);

		getEntityManagerFactory(metaData);

		InvocationHandler handler = new BeanHandler(metaData, beanInstance);

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

	public <T> T connectToBean(String beanName, Class<T> interfaceClass,
			Object... rpcArgs) throws IOException {
		InvocationHandler handler;
		Configuration configuration = MetaCreator.CONFIG;
		if (configuration.isServer()) {

			MetaData metaData = getMeta(beanName);
			handler = getHandler(metaData);

		} else {
			if (rpcArgs.length != 2) {
				throw new IOException(
						"Could not resolve host and port arguments");
			}
			String host = (String) rpcArgs[0];
			int port = (Integer) rpcArgs[1];
			handler = new BeanLocalHandler(new RPCall(host, port));
		}

		Class<?>[] interfaceArray = { interfaceClass };

		@SuppressWarnings("unchecked")
		T beanInstance = (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), interfaceArray, handler);

		return beanInstance;
	}
}
