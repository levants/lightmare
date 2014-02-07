/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.cache;

import java.lang.reflect.Field;

import javax.persistence.EntityManagerFactory;

import org.lightmare.utils.ObjectUtils;

/**
 * Container class to cache connection with descriptors ( {@link Field}s, unit
 * name, JNDI name and {@link ConnectionSemaphore}) in
 * {@link org.lightmare.cache.MetaData} for each EJB bean
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 * @see org.lightmare.deploy.BeanLoader#loadBean(org.lightmare.deploy.BeanLoader.BeanParameters)
 * 
 */
public class ConnectionData {

    // EJB beans's field to set connection
    private Field connectionField;

    // EJB bean's field to set EntityManagerFactory instance
    private Field unitField;

    // EntityManagerFactory instance for appropriated persistence unit
    private EntityManagerFactory emf;

    // Persistence unit name
    private String unitName;

    // JNDI name
    private String jndiName;

    // ConnectionSemaphore instance for appropriated EntityManagerFactory
    // instance
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
