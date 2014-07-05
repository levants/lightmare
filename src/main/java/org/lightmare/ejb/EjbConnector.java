/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
import org.lightmare.ejb.handlers.RestHandlerFactory;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.reflect.ClassUtils;
import org.lightmare.utils.remote.RpcUtils;

/**
 * Connector class for get EJB beans or call remote procedure in this bean (RPC)
 * by interface class
 * 
 * @author Levan Tsinadze
 * @since 0.0.15-SNAPSHOT
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
	return MetaContainer.getSyncMetaData(beanName);
    }

    /**
     * Gets connection for {@link javax.ejb.Stateless} EJB bean {@link Class}
     * from cache
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    private void setEntityManagerFactory(ConnectionData connection)
	    throws IOException {

	if (connection.getEmf() == null) {
	    String unitName = connection.getUnitName();
	    if (StringUtils.valid(unitName)) {
		ConnectionSemaphore semaphore = ConnectionContainer
			.getConnection(unitName);
		connection.setConnection(semaphore);
	    }
	}
    }

    /**
     * Gets connections for {@link Stateless} EJB bean {@link Class} from cache
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    private void setEntityManagerFactories(MetaData metaData)
	    throws IOException {

	Collection<ConnectionData> connections = metaData.getConnections();
	if (CollectionUtils.valid(connections)) {
	    for (ConnectionData connection : connections) {
		setEntityManagerFactory(connection);
	    }
	}
    }

    /**
     * Instantiates bean by class
     * 
     * @param metaData
     * @return <code>T</code> EJB Bean instance
     * @throws IOException
     */
    private <T> T getBeanInstance(MetaData metaData) throws IOException {

	T beanInstance;

	Class<? extends T> beanClass = ObjectUtils
		.cast(metaData.getBeanClass());
	beanInstance = ClassUtils.instantiate(beanClass);

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

	BeanHandler handler;

	T beanInstance = getBeanInstance(metaData);
	// Caches EnriryManagerFactory instances in MetaData if they are not
	// cached yet
	setEntityManagerFactories(metaData);
	// Initializes BeanHandler instance and caches it in MetaData if it was
	// not cached yet
	handler = BeanHandlerFactory.get(metaData, beanInstance);

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

	T beanInstance;

	if (loader == null) {
	    loader = LibraryLoader.getContextClassLoader();
	} else {
	    LibraryLoader.loadCurrentLibraries(loader);
	}

	Object instance = Proxy.newProxyInstance(loader, interfaces, handler);
	beanInstance = ObjectUtils.cast(instance);

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

	T beanInstance;

	Class<T>[] interfaceArray = ObjectUtils
		.cast(new Class<?>[] { interfaceClass });
	beanInstance = instatiateBean(interfaceArray, handler, loader);

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

	if (CollectionUtils.invalid(interfaceClasses)) {
	    List<Class<?>> interfacesList = new ArrayList<Class<?>>();
	    Class<?>[] interfaces = metaData.getLocalInterfaces();

	    if (CollectionUtils.valid(interfaces)) {
		interfacesList.addAll(Arrays.asList(interfaces));
	    }

	    interfaces = metaData.getRemoteInterfaces();
	    if (CollectionUtils.valid(interfaces)) {
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
    public <T> T connectToBean(MetaData metaData) throws IOException {

	T beanInstance;

	InvocationHandler handler = getBeanHandler(metaData);
	Class<?>[] interfaces = setInterfaces(metaData);
	Class<T>[] typedInterfaces = ObjectUtils.cast(interfaces);
	ClassLoader loader = metaData.getLoader();
	beanInstance = instatiateBean(typedInterfaces, handler, loader);

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

	T beanInstance;

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

	beanInstance = instatiateBean(interfaceClass, handler, loader);

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

	T beanInstance;

	MetaData metaData = getMeta(beanName);
	ClassLoader loader = metaData.getLoader();
	Class<?> classForName = ClassUtils.classForName(interfaceName,
		Boolean.FALSE, loader);
	Class<T> interfaceClass = ObjectUtils.cast(classForName);
	beanInstance = connectToBean(beanName, interfaceClass, rpcArgs);

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

	RestHandler<T> restHandler;

	BeanHandler handler = getBeanHandler(metaData);
	Class<T> beanClass = ObjectUtils.cast(metaData.getBeanClass());
	T beanInstance = ClassUtils.instantiate(beanClass);
	restHandler = RestHandlerFactory.get(handler, beanInstance);

	return restHandler;
    }
}
