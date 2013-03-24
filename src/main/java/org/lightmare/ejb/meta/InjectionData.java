package org.lightmare.ejb.meta;

import java.lang.reflect.Field;

/**
 * Container class to cache bean injections
 * 
 * @author Levan
 * 
 */
public class InjectionData {

    private Class<?> beanClass;

    private Class<?> interfaceClass;

    private String name;

    private String description;

    private String mappedName;

    private MetaData metaData;

    private Field field;

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
