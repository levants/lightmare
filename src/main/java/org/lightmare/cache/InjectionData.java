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

/**
 * Container class to cache EJB bean injections
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class InjectionData {

    // Bean class which should be injected
    private Class<?> beanClass;

    // Appropriate interface class for bean
    private Class<?>[] interfaceClasses;

    // Bean name
    private String name;

    private String description;

    // Bean JNDI name
    private String mappedName;

    // MetaData for injected bean
    private MetaData metaData;

    // Field for injection
    private Field field;

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

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getMappedName() {
	return mappedName;
    }

    public void setMappedName(String mappedName) {
	this.mappedName = mappedName;
    }

    public MetaData getMetaData() {
	return metaData;
    }

    public void setMetaData(MetaData metaData) {
	this.metaData = metaData;
    }

    public Field getField() {
	return field;
    }

    public void setField(Field field) {
	this.field = field;
    }
}
