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
package org.lightmare.ejb.handlers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

import org.lightmare.cache.ConnectionData;
import org.lightmare.cache.InjectionData;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.ejb.interceptors.InterceptorHandler;
import org.lightmare.jpa.jta.BeanTransactions;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Implementation of {@link InvocationHandler} interface to intercept bean
 * method calls to provide database transactions
 *
 * @author Levan Tsinadze
 * @since 0.0.16-SNAPSHOT
 */
public class BeanHandler implements InvocationHandler, Cloneable {

    // EJB bean instance
    private Object bean;

    // EJB bean class
    private final Class<?> beanClass;

    // Bean's field with Resource annotation
    private final Field transactionField;

    // Connections for given EJB bean instance
    private final Collection<ConnectionData> connectionDatas;

    // Injections from given EJB bean instance
    private final Collection<InjectionData> injectionDatas;

    // Interceptor handler for given bean instance
    private final InterceptorHandler interceptorHandel;

    // EJB meta data for given bean instance
    private final MetaData metaData;

    protected BeanHandler(final MetaData metaData) {
	this.beanClass = metaData.getBeanClass();
	this.transactionField = metaData.getTransactionField();
	this.connectionDatas = metaData.getConnections();
	this.injectionDatas = metaData.getInjects();
	this.interceptorHandel = new InterceptorHandler(metaData);
	this.metaData = metaData;
    }

    public MetaData getMetaData() {
	return metaData;
    }

    public Object getBean() {
	return bean;
    }

    /**
     * Sets bean instance to handler, should be called only after cloning
     *
     * @param bean
     */
    protected void setBean(final Object bean) {
	this.bean = bean;
    }

    /**
     * Sets passed {@link Object} as beans {@link Field} value
     *
     * @param field
     * @param value
     * @throws IOException
     */
    private void setFieldValue(Field field, Object value) throws IOException {
	ClassUtils.setFieldValue(field, bean, value);
    }

    /**
     * Invokes passed bean {@link Method} for handlers EJB bean instance
     *
     * @param method
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    private Object invokeMethod(Method method, Object... arguments) throws IOException {
	return ClassUtils.invoke(method, bean, arguments);
    }

    /**
     * Sets {@link EntityManager} as handlers EJB beans's annotated
     * {@link Field} value
     *
     * @param em
     * @throws IOException
     */
    private void setConnection(Field connectionField, EntityManager em) throws IOException {
	setFieldValue(connectionField, em);
    }

    /**
     * Initializes injection for EJB bean
     *
     * @param injectionData
     * @return {@link MetaData} for injected EJB bean
     * @throws IOException
     */
    private MetaData initInjection(InjectionData injectionData) throws IOException {

	MetaData injectMetaData = injectionData.getMetaData();

	if (injectMetaData == null) {
	    String beanName;
	    String mappedName = injectionData.getMappedName();
	    if (mappedName == null || mappedName.isEmpty()) {
		beanName = injectionData.getName();
	    } else {
		beanName = injectionData.getMappedName();
	    }
	    // Fills injection meta data parameters
	    injectMetaData = MetaContainer.getSyncMetaData(beanName);
	    injectMetaData.setInterfaceClasses(injectionData.getInterfaceClasses());
	    injectionData.setMetaData(injectMetaData);
	}

	return injectMetaData;
    }

    /**
     * Sets each injected EJB bean as value to annotated field respectively for
     * passed {@link InjectionData} object
     *
     * @throws IOException
     */
    private void configureInjection(InjectionData injectionData) throws IOException {

	MetaData injectMetaData = injectionData.getMetaData();
	if (injectMetaData == null) {
	    injectMetaData = initInjection(injectionData);
	}

	EjbConnector ejbConnector = new EjbConnector();
	Object injectBean = ejbConnector.connectToBean(injectMetaData);

	setFieldValue(injectionData.getField(), injectBean);
    }

    /**
     * Sets injected EJB bean as values to {@link javax.ejb.EJB} annotated
     * fields respectively
     *
     * @throws IOException
     */
    private void configureInjects() throws IOException {

	if (CollectionUtils.valid(injectionDatas)) {
	    for (InjectionData inject : injectionDatas) {
		configureInjection(inject);
	    }
	}
    }

    /**
     * Method to configure (injections {@link javax.ejb.EJB} or
     * {@link PersistenceUnit} annotated fields and etc.) {@link BeanHandler}
     * after initialization
     *
     * @throws IOException
     */
    public void configure() throws IOException {
	// TODO Add other configurations
	configureInjects();
    }

    /**
     * Method to set bean field and to configure (injections
     * {@link javax.ejb.EJB} or {@link PersistenceUnit} annotated fields and
     * etc.) {@link BeanHandler} after initialization
     *
     * @param bean
     * @throws IOException
     */
    public void configure(final Object bean) throws IOException {
	setBean(bean);
	configure();
    }

    /**
     * Creates and caches {@link UserTransaction} per caller thread
     *
     * @param em
     * @return {@link UserTransaction}
     */
    private UserTransaction getTransaction(Collection<EntityManager> ems) {
	return BeanTransactions.getTransaction(ems);
    }

    /**
     * Sets transaction as handlers EJB bean's {@link javax.annotation.Resource}
     * annotated {@link Field}'s value
     *
     * @param ems
     * @throws IOException
     */
    private void setTransactionField(Collection<EntityManager> ems) throws IOException {

	if (ObjectUtils.notNull(transactionField)) {
	    UserTransaction transaction = getTransaction(ems);
	    setFieldValue(transactionField, transaction);
	}
    }

    /**
     * Initializes {@link EntityManager} end sets appropriated field
     *
     * @param emf
     * @param unitField
     * @return {@link EntityManager}
     * @throws IOException
     */
    private EntityManager createEntityManager(EntityManagerFactory emf, Field unitField) throws IOException {

	EntityManager em = emf.createEntityManager();

	if (ObjectUtils.notNull(unitField)) {
	    setFieldValue(unitField, emf);
	}

	return em;
    }

    /**
     * Creates {@link EntityManager} if passed {@link EntityManagerFactory} is
     * not null
     *
     * @return {@link EntityManager}
     * @throws IOException
     */
    private EntityManager createEntityManager(ConnectionData connection) throws IOException {

	EntityManager em;

	EntityManagerFactory emf = connection.getEmf();
	Field connectionField = connection.getConnectionField();
	Field unitField = connection.getUnitField();
	if (ObjectUtils.notNull(emf)) {
	    em = createEntityManager(emf, unitField);
	    setConnection(connectionField, em);
	} else {
	    em = null;
	}

	return em;
    }

    /**
     * Creates {@link EntityManager}s to set as bean's appropriate {@link Field}
     * values
     *
     * @return {@link Collection}<code><EntityManager></code>
     * @throws IOException
     */
    private Collection<EntityManager> createEntityManagers() throws IOException {

	Collection<EntityManager> ems;

	if (CollectionUtils.valid(connectionDatas)) {
	    ems = new ArrayList<EntityManager>();
	    for (ConnectionData connection : connectionDatas) {
		EntityManager em = createEntityManager(connection);
		ems.add(em);
	    }
	} else {
	    ems = null;
	}

	return ems;
    }

    /**
     * Closes {@link EntityManager} if there is not
     * {@link javax.annotation.Resource} annotation in current bean
     *
     * @param transaction
     * @param em
     * @throws IOException
     */
    private void close(Method method) throws IOException {

	try {
	    if (ObjectUtils.notNull(method)) {
		if (transactionField == null) {
		    BeanTransactions.commitTransaction(this, method);
		} else {
		    BeanTransactions.closeEntityManagers();
		}
	    }
	} finally {
	    BeanTransactions.remove(this, method);
	}
    }

    /**
     * Calls {@link BeanTransactions#rollbackTransaction(BeanHandler, Method))}
     * is case of {@link Throwable} is thrown at passed {@link Method} execution
     * time
     *
     * @param method
     * @throws IOException
     */
    private void rollback(Method method) throws IOException {

	try {
	    if (ObjectUtils.notNull(method)) {
		BeanTransactions.rollbackTransaction(this, method);
	    }
	} catch (Throwable th) {
	    close(method);
	    throw new IOException(th);
	}
    }

    /**
     * Invokes method surrounded with {@link UserTransaction} begin and commit
     *
     * @param em
     * @param method
     * @param arguments
     * @return Object
     * @throws IOException
     */
    private Object invokeBeanMethod(final Collection<EntityManager> ems, final Method method, Object[] arguments)
	    throws IOException {

	if (transactionField == null) {
	    BeanTransactions.addTransaction(this, method, ems);
	} else {
	    setTransactionField(ems);
	}
	// Calls interceptors for this method or bean instance
	Object[] intercepteds = interceptorHandel.callInterceptors(method, arguments);
	// Calls for bean method with "intercepted" parameters
	Object value = invokeMethod(method, intercepteds);

	return value;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {

	Object value;

	Collection<EntityManager> ems = createEntityManagers();
	Method realMethod = null;
	try {
	    String methodName = method.getName();
	    Class<?>[] parameterTypes = method.getParameterTypes();
	    // Gets real method of bean class
	    realMethod = ClassUtils.getDeclaredMethod(beanClass, methodName, parameterTypes);
	    value = invokeBeanMethod(ems, realMethod, arguments);
	} catch (Throwable th) {
	    rollback(realMethod);
	    throw th;
	} finally {
	    close(realMethod);
	}

	return value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
	return super.clone();
    }
}
