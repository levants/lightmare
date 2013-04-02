package org.lightmare.ejb.meta;

import java.lang.reflect.Field;

import javax.persistence.EntityManagerFactory;

import org.lightmare.utils.ObjectUtils;

/**
 * Container class to cache connection in {@link org.lightmare.MetaData} for trn
 * 
 * @author levan
 * 
 */
public class ConnectionData {

    private Field connectionField;

    private Field unitField;

    private EntityManagerFactory emf;

    private String unitName;

    private String jndiName;

    private ConnectionSemaphore connection;

    public Field getConnectionField() {
	return connectionField;
    }

    public void setConnectionField(Field connectionField) {
	this.connectionField = connectionField;
    }

    public Field getUnitField() {
	return unitField;
    }

    public void setUnitField(Field unitField) {
	this.unitField = unitField;
    }

    public EntityManagerFactory getEmf() {
	return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
	this.emf = emf;
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

    public ConnectionSemaphore getConnection() {
	return connection;
    }

    public void setConnection(ConnectionSemaphore connection) {
	this.connection = connection;
	if (ObjectUtils.notNull(connection)) {
	    emf = connection.getEmf();
	}
    }
}
