package ge.gov.mia.lightmare.ejb;

import static ge.gov.mia.lightmare.ejb.meta.MetaContainer.getMetaData;
import static ge.gov.mia.lightmare.jpa.JPAManager.getConnection;
import ge.gov.mia.lightmare.ejb.handlers.BeanHandler;
import ge.gov.mia.lightmare.ejb.meta.MetaData;
import ge.gov.mia.lightmare.libraries.LibraryLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;

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
			throws InstantiationException, IllegalAccessException {
		MetaData metaData = getMetaData(beanName);
		Class<? extends T> beanClass = (Class<? extends T>) metaData
				.getBeanClass();

		LibraryLoader.loadCurrentLibraries(metaData.getLoader());

		T beanInstance = beanClass.newInstance();

		EntityManagerFactory emf = getEntityManagerFactory(metaData);
		Field connectorField = metaData.getConnectorField();
		BeanHandler handler = new BeanHandler(connectorField, beanInstance, emf);
		Class<?>[] interfaceArray = { interfaceClass };
		beanInstance = (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), interfaceArray, handler);
		return beanInstance;
	}
}
