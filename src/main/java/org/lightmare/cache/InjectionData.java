package org.lightmare.cache;

import java.lang.reflect.Field;

/**
 * Container class to cache bean injections
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
