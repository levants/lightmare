package org.lightmare.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.utils.CollectionUtils;

/**
 * Container class to save bean {@link Field} with annotation
 * {@link PersistenceContext} and bean class
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 * @see org.lightmare.deploy.BeanLoader#loadBean(org.lightmare.deploy.BeanLoader.BeanParameters)
 * @see org.lightmare.ejb.EjbConnector#connectToBean(MetaData)
 * @see org.lightmare.ejb.EjbConnector#createRestHandler(MetaData)
 * @see org.lightmare.ejb.handlers.BeanHandler
 */
public class MetaData {

    // EJB bean class
    private Class<?> beanClass;

    // EJB bean implementation interfaces
    private Class<?>[] interfaceClasses;

    // All EJB local interfaces
    private Class<?>[] localInterfaces;

    // All EJB remote interfaces
    private Class<?>[] remoteInterfaces;

    // EJB bean's Field to set JTA UserTransaction instance
    private Field transactionField;

    // All connections for appropriated EJB bean
    private Collection<ConnectionData> connections;

    // Appropriated ClassLoader with loaded EJB bean class
    private ClassLoader loader;

    // Check if this object is in deployment progress
    private AtomicBoolean inProgress = new AtomicBoolean();

    private boolean transactional;

    private TransactionAttributeType transactionAttrType;

    private TransactionManagementType transactionManType;

    private List<InjectionData> injects;

    private Collection<Field> unitFields;

    private Queue<InterceptorData> interceptors;

    private BeanHandler handler;

    public Class<?> getBeanClass() {
	return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
	this.beanClass = beanClass;
    }

    public Class<?>[] getInterfaceClasses() {
	return interfaceClasses;
    }

    public void setInterfaceClasses(Class<?>[] interfaceClasses) {
	this.interfaceClasses = interfaceClasses;
    }

    public Class<?>[] getLocalInterfaces() {
	return localInterfaces;
    }

    public void setLocalInterfaces(Class<?>[] localInterfaces) {
	this.localInterfaces = localInterfaces;
    }

    public Class<?>[] getRemoteInterfaces() {
	return remoteInterfaces;
    }

    public void setRemoteInterfaces(Class<?>[] remoteInterfaces) {
	this.remoteInterfaces = remoteInterfaces;
    }

    public Field getTransactionField() {
	return transactionField;
    }

    public void setTransactionField(Field transactionField) {
	this.transactionField = transactionField;
    }

    public Collection<ConnectionData> getConnections() {
	return connections;
    }

    public void setConnections(Collection<ConnectionData> connections) {
	this.connections = connections;
    }

    /**
     * Caches passed connection information
     * 
     * @param connection
     */
    public void addConnection(ConnectionData connection) {

	if (connections == null) {
	    connections = new ArrayList<ConnectionData>();
	}

	connections.add(connection);
    }

    /**
     * Caches {@link javax.persistence.PersistenceUnit} annotated field and unit
     * name to cache
     * 
     * @param unitName
     * @param unitField
     */
    private void addUnitField(String unitName, Field unitField) {

	for (ConnectionData connection : connections) {
	    if (unitName.equals(connection.getUnitName())) {
		connection.setUnitField(unitField);
	    }
	}
    }

    /**
     * Adds {@link javax.ejb.PersistenceUnit} annotated field to
     * {@link MetaData} for cache
     * 
     * @param unitFields
     */
    public void addUnitFields(Collection<Field> unitFields) {

	if (CollectionUtils.validAll(connections, unitFields)) {
	    String unitName;
	    for (Field unitField : unitFields) {
		unitName = unitField.getAnnotation(PersistenceUnit.class)
			.unitName();
		addUnitField(unitName, unitField);
	    }

	    this.unitFields = unitFields;
	}
    }

    public ClassLoader getLoader() {
	return loader;
    }

    public void setLoader(ClassLoader loader) {
	this.loader = loader;
    }

    public boolean isInProgress() {
	return inProgress.get();
    }

    public void setInProgress(boolean inProgress) {
	this.inProgress.getAndSet(inProgress);
    }

    public boolean isTransactional() {
	return transactional;
    }

    public void setTransactional(boolean transactional) {
	this.transactional = transactional;
    }

    public TransactionAttributeType getTransactionAttrType() {
	return transactionAttrType;
    }

    public void setTransactionAttrType(
	    TransactionAttributeType transactionAttrType) {
	this.transactionAttrType = transactionAttrType;
    }

    public TransactionManagementType getTransactionManType() {
	return transactionManType;
    }

    public void setTransactionManType(
	    TransactionManagementType transactionManType) {
	this.transactionManType = transactionManType;
    }

    public List<InjectionData> getInjects() {
	return injects;
    }

    /**
     * Adds passed {@link InjectionData} to cache
     * 
     * @param inject
     */
    public void addInject(InjectionData inject) {

	if (injects == null) {
	    injects = new ArrayList<InjectionData>();
	}

	injects.add(inject);
    }

    public Collection<Field> getUnitFields() {
	return this.unitFields;
    }

    /**
     * Offers {@link InterceptorData} to {@link Stack} to process request by
     * order
     * 
     * @param interceptor
     */
    public void addInterceptor(InterceptorData interceptor) {

	if (interceptors == null) {
	    interceptors = new LinkedList<InterceptorData>();
	}

	interceptors.offer(interceptor);
    }

    public Collection<InterceptorData> getInterceptors() {

	return interceptors;
    }

    public BeanHandler getHandler() {
	return handler;
    }

    public void setHandler(BeanHandler handler) {
	this.handler = handler;
    }
}
