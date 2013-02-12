package org.lightmare.ejb.meta;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

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

	private Field connectorField;

	private Field transactionField;

	private Field unitField;

	private String unitName;

	private String jndiName;

	private EntityManagerFactory emf;

	private ClassLoader loader;

	private AtomicBoolean inProgress = new AtomicBoolean();

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
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

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
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
}
