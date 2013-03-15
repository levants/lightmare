package org.lightmare.ejb.meta;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

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

    private Field connectorField;

    private Field transactionField;

    private Field unitField;

    private String unitName;

    private String jndiName;

    private ConnectionSemaphore connection;

    private EntityManagerFactory emf;

    private ClassLoader loader;

    private AtomicBoolean inProgress = new AtomicBoolean();

    private boolean transactional;

    private TransactionAttributeType transactionAttrType;

    private TransactionManagementType transactionManType;

    private Set<Object> injects;

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

    public Field getConnectorField() {
	return connectorField;
    }

    public void setConnectorField(Field connectorField) {
	this.connectorField = connectorField;
    }

    public Field getTransactionField() {
	return transactionField;
    }

    public void setTransactionField(Field transactionField) {
	this.transactionField = transactionField;
    }

    public Field getUnitField() {
	return unitField;
    }

    public void setUnitField(Field unitField) {
	this.unitField = unitField;
    }

    public String getUnitName() {
	return unitName;
    }

    public void setUnitName(String unitName) {
	this.unitName = unitName;
    }

    public String getJndiName() {
	return jndiName;
    }

    public void setJndiName(String jndiName) {
	this.jndiName = jndiName;
    }

    public EntityManagerFactory getEmf() {
	return emf;
    }

    public ConnectionSemaphore getConnection() {
	return connection;
    }

    public void setConnection(ConnectionSemaphore connection) {
	this.connection = connection;
	if (connection != null) {
	    emf = connection.getEmf();
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

    public Set<Object> getInjects() {
	return injects;
    }

    public void addInject(Object inject) {

	if (injects == null) {
	    injects = new HashSet<Object>();
	}
	injects.add(inject);
    }
}
