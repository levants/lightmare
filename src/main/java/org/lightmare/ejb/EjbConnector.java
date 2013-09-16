package org.lightmare.ejb;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;

import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.ConnectionData;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.config.Configuration;
import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.ejb.handlers.BeanHandlerFactory;
import org.lightmare.ejb.handlers.BeanLocalHandlerFactory;
import org.lightmare.ejb.handlers.RestHandler;
import org.lightmare.ejb.handlers.ResthandlerFactory;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;
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
     * Gets {@link MetaData} from {@link MetaContainer} if it is not locked or
     * waits while {@link MetaData#isInProgress()} is true
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
     * Gets connection for {@link javax.ejb.Stateless} bean {@link Class} from
     * cache
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    private void setEntityManagerFactory(ConnectionData connection)
	    throws IOException {

	if (connection.getEmf() == null) {
	    String unitName = connection.getUnitName();

	    if (ObjectUtils.available(unitName)) {
		ConnectionSemaphore semaphore = ConnectionContainer
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
    private void setEntityManagerFactories(MetaData metaData)
	    throws IOException {

	Collection<ConnectionData> connections = metaData.getConnections();
	if (ObjectUtils.available(connections)) {

	    for (ConnectionData connection : connections) {
		setEntityManagerFactory(connection);
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

	Class<? extends T> beanClass = ObjectUtils
		.cast(metaData.getBeanClass());

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
    private <T> BeanHandler getBeanHandler(MetaData metaData)
	    throws IOException {

	T beanInstance = getBeanInstance(metaData);

	// Caches EnriryManagerFactory instances in MetaData if they are not
	// cached yet
	setEntityManagerFactories(metaData);

	// Initializes BeanHandler instance and caches it in MetaData if it was
	// not cached yet
	BeanHandler handler = BeanHandlerFactory.get(metaData, beanInstance);

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
	} else {
	    LibraryLoader.loadCurrentLibraries(loader);
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

	@SuppressWarnings("unchecked")
	Class<T>[] interfaceArray = (Class<T>[]) new Class<?>[] { interfaceClass };

	T beanInstance = instatiateBean(interfaceArray, handler, loader);

	return beanInstance;
    }

    /**
     * Initializes and caches all interfaces for bean class from passed
     * {@link MetaData} instance if it is not already cached
     * 
     * @param metaData
     * @return {@link Class}[]
     */
    private Class<?>[] setInterfaces(MetaData metaData) {

	Class<?>[] interfaceClasses = metaData.getInterfaceClasses();
	if (ObjectUtils.notAvailable(interfaceClasses)) {

	    List<Class<?>> interfacesList = new ArrayList<Class<?>>();
	    Class<?>[] interfaces = metaData.getLocalInterfaces();
	    if (ObjectUtils.available(interfaces)) {
		interfacesList.addAll(Arrays.asList(interfaces));
	    }

	    interfaces = metaData.getRemoteInterfaces();
	    if (ObjectUtils.available(interfaces)) {
		interfacesList.addAll(Arrays.asList(interfaces));
	    }

	    int size = interfacesList.size();
	    interfaceClasses = interfacesList.toArray(new Class[size]);
	    metaData.setInterfaceClasses(interfaceClasses);
	}

	return interfaceClasses;
    }

    /**
     * Creates appropriate bean {@link Proxy} instance by passed
     * {@link MetaData} parameter
     * 
     * @param metaData
     * @param rpcArgs
     * @return <code>T</code> implementation of bean interface
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public <T> T connectToBean(MetaData metaData) throws IOException {

	InvocationHandler handler = getBeanHandler(metaData);
	Class<?>[] interfaces = setInterfaces(metaData);
	ClassLoader loader = metaData.getLoader();

	T beanInstance = (T) instatiateBean((Class<T>[]) interfaces, handler,
		loader);

	return beanInstance;
    }

    /**
     * Creates custom implementation of bean {@link Class} by class name and its
     * {@link Proxy} interface {@link Class} instance
     * 
     * @param interfaceClass
     * @return <code>T</code> implementation of bean interface
     * @throws IOException
     */
    public <T> T connectToBean(String beanName, Class<T> interfaceClass,
	    Object... rpcArgs) throws IOException {

	InvocationHandler handler;
	ClassLoader loader;

	if (Configuration.isServer()) {
	    MetaData metaData = getMeta(beanName);
	    setInterfaces(metaData);
	    handler = getBeanHandler(metaData);
	    loader = metaData.getLoader();
	} else {
	    if (rpcArgs.length == RpcUtils.RPC_ARGS_LENGTH) {
		handler = BeanLocalHandlerFactory.get(rpcArgs);
		loader = null;
	    } else {
		throw new IOException(RpcUtils.RPC_ARGS_ERROR);
	    }
	}

	T beanInstance = (T) instatiateBean(interfaceClass, handler, loader);

	return beanInstance;
    }

    /**
     * Creates custom implementation of bean {@link Class} by class name and its
     * {@link Proxy} interface name
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
	ClassLoader loader = metaData.getLoader();

	Class<?> classForName = MetaUtils.classForName(interfaceName,
		Boolean.FALSE, loader);
	Class<T> interfaceClass = ObjectUtils.cast(classForName);

	T beanInstance = (T) connectToBean(beanName, interfaceClass, rpcArgs);

	return beanInstance;
    }

    /**
     * Creates {@link RestHandler} instance for invoking bean methods by REST
     * services
     * 
     * @param metaData
     * @return {@link RestHandler}
     * @throws IOException
     */
    public <T> RestHandler<T> createRestHandler(MetaData metaData)
	    throws IOException {

	BeanHandler handler = getBeanHandler(metaData);

	Class<T> beanClass = ObjectUtils.cast(metaData.getBeanClass());

	T beanInstance = MetaUtils.instantiate(beanClass);

	RestHandler<T> restHandler = ResthandlerFactory.get(handler,
		beanInstance);

	return restHandler;
    }
}
