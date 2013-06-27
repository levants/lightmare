package org.lightmare.ejb;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;

import org.lightmare.cache.ConnectionData;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.config.Configuration;
import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.ejb.handlers.BeanLocalHandler;
import org.lightmare.jpa.JPAManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.remote.rpc.RPCall;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Connector class for get ejb beans or call remote procedure in ejb bean (RPC)
 * by interface class
 * 
 * @author Levan
 * 
 */
public class EjbConnector {

    private static final int RPC_ARGS_LENGTH = 2;

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
    private void getEntityManagerFactory(ConnectionData connection)
	    throws IOException {

	if (connection.getEmf() == null) {
	    String unitName = connection.getUnitName();

	    if (ObjectUtils.available(unitName)) {
		ConnectionSemaphore semaphore = JPAManager
			.getConnection(unitName);
		connection.setConnection(semaphore);
	    }
	}
    }

    /**
     * Gets connections for {@link Stateless} bean {@link Class} from cache
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */

    private void getEntityManagerFactories(MetaData metaData)
	    throws IOException {

	Collection<ConnectionData> connections = metaData.getConnections();
	if (ObjectUtils.available(connections)) {

	    for (ConnectionData connection : connections) {
		getEntityManagerFactory(connection);
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
    public <T> InvocationHandler getHandler(MetaData metaData)
	    throws IOException {

	T beanInstance = getBeanInstance(metaData);

	getEntityManagerFactories(metaData);

	BeanHandler handler = new BeanHandler(metaData, beanInstance);
	handler.configure();

	return handler;
    }

    /**
     * Instantiates bean with {@link Proxy} utility
     * 
     * @param interfaces
     * @param handler
     * @return <code>T</code> implementation of bean interface
     */
    private <T> T instatiateBean(Class<T>[] interfaces,
	    InvocationHandler handler, ClassLoader loader) {

	if (loader == null) {
	    loader = LibraryLoader.getContextClassLoader();
	}

	@SuppressWarnings("unchecked")
	T beanInstance = (T) Proxy
		.newProxyInstance(loader, interfaces, handler);

	return beanInstance;
    }

    /**
     * Instantiates bean with {@link Proxy} utility
     * 
     * @param interfaceClass
     * @param handler
     * @return <code>T</code> implementation of bean interface
     */
    private <T> T instatiateBean(Class<T> interfaceClass,
	    InvocationHandler handler, ClassLoader loader) {

	Class<?>[] interfaceArray = { interfaceClass };
	if (loader == null) {
	    loader = LibraryLoader.getContextClassLoader();
	}

	@SuppressWarnings("unchecked")
	T beanInstance = (T) Proxy.newProxyInstance(loader, interfaceArray,
		handler);

	return beanInstance;
    }

    private Class<?>[] chooseInterface(MetaData metaData) {

	Class<?>[] interfaceClasses;
	Class<?> interfaceClass = metaData.getInterfaceClass();
	if (interfaceClass == null) {
	    interfaceClasses = metaData.getLocalInterfaces();
	    if (interfaceClasses == null) {
		interfaceClasses = metaData.getRemoteInterfaces();
	    }
	} else {
	    interfaceClasses = new Class[] { interfaceClass };
	}

	return interfaceClasses;
    }

    /**
     * 
     * @param metaData
     * @param rpcArgs
     * @return <code>T</code> implementation of bean interface
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public <T> T connectToBean(MetaData metaData, Object... rpcArgs)
	    throws IOException {

	InvocationHandler handler = getHandler(metaData);
	Class<?>[] interfaces = chooseInterface(metaData);
	ClassLoader loader = metaData.getLoader();

	T beanInstance = (T) instatiateBean((Class<T>[]) interfaces, handler,
		loader);

	return beanInstance;
    }

    /**
     * Creates custom implementation of bean {@link Class} by class name and its
     * proxy interface {@link Class} instance
     * 
     * @param interfaceClass
     * @return <code>T</code> implementation of bean interface
     * @throws IOException
     */

    public <T> T connectToBean(String beanName, Class<T> interfaceClass,
	    Object... rpcArgs) throws IOException {
	InvocationHandler handler;
	Configuration configuration = MetaContainer.CONFIG;
	ClassLoader loader;
	if (configuration.isServer()) {

	    MetaData metaData = getMeta(beanName);
	    metaData.setInterfaceClass(interfaceClass);
	    handler = getHandler(metaData);
	    loader = metaData.getLoader();

	} else {
	    if (rpcArgs.length != RPC_ARGS_LENGTH) {
		throw new IOException(
			"Could not resolve host and port arguments");
	    }
	    String host = (String) rpcArgs[0];
	    int port = (Integer) rpcArgs[1];
	    handler = new BeanLocalHandler(new RPCall(host, port));
	    loader = null;
	}

	T beanInstance = (T) instatiateBean(interfaceClass, handler, loader);

	return beanInstance;
    }

    /**
     * Creates custom implementation of bean {@link Class} by class name and its
     * proxy interface name
     * 
     * @param beanName
     * @param interfaceName
     * @param rpcArgs
     * @return <code>T</code> implementation of bean interface
     * @throws IOException
     */
    public <T> T connectToBean(String beanName, String interfaceName,
	    Object... rpcArgs) throws IOException {

	MetaData metaData = getMeta(beanName);

	if (metaData.getInterfaceClass() == null) {
	    ClassLoader loader = metaData.getLoader();

	    @SuppressWarnings("unchecked")
	    Class<T> interfaceClass = (Class<T>) MetaUtils.classForName(
		    interfaceName, Boolean.FALSE, loader);
	    metaData.setInterfaceClass(interfaceClass);
	}

	@SuppressWarnings("unchecked")
	T beanInstance = (T) connectToBean(metaData);

	return beanInstance;
    }
}
