package org.lightmare.ejb.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.lightmare.utils.ObjectUtils;

/**
 * Container class to save bean {@link Field} with annotation
 * {@link PersistenceContext} and bean class
 * 
 * @author Levan
 * 
 */
public class MetaData {

    private Class<?> beanClass;

    private Class<?> interfaceClass;

    private Field transactionField;

    private Collection<ConnectionData> connections;

    private ClassLoader loader;

    private AtomicBoolean inProgress = new AtomicBoolean();

    private boolean transactional;

    private TransactionAttributeType transactionAttrType;

    private TransactionManagementType transactionManType;

    private List<InjectionData> injects;

    public Class<?> getBeanClass() {
	return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
	this.beanClass = beanClass;
    }

    public Class<?> getInterfaceClass() {
	return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
	this.interfaceClass = interfaceClass;
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

    public void addConnection(ConnectionData connection) {

	if (connections == null) {
	    connections = new ArrayList<ConnectionData>();
	}

	connections.add(connection);
    }

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

	if (ObjectUtils.availableAll(connections, unitFields)) {
	    String unitName;
	    for (Field unitField : unitFields) {
		unitName = unitField.getAnnotation(PersistenceUnit.class)
			.unitName();
		addUnitField(unitName, unitField);
	    }
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

    public void addInject(InjectionData inject) {

	if (injects == null) {
	    injects = new ArrayList<InjectionData>();
	}
	injects.add(inject);
    }
}
