package ge.gov.mia.lightmare.ejb.meta;

import java.lang.reflect.Field;

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

	private String unitName;

	private ClassLoader loader;

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

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public ClassLoader getLoader() {
		return loader;
	}

	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
}
