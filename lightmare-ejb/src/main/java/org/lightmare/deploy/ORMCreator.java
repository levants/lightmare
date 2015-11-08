/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2014, Levan Tsinadze, or third-party contributors as
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
package org.lightmare.deploy;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.persistence.Entity;

import org.lightmare.annotations.UnitName;
import org.lightmare.config.Configuration;
import org.lightmare.jpa.JpaManager;
import org.lightmare.scannotation.AnnotationFinder;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Class to initialize and deploy JPA units
 *
 * @author Levan Tsinadze
 * @since 0.0.80
 * @see org.lightmare.jpa.JpaManager
 */
public class ORMCreator {

    private Map<String, ArchiveUtils> aggregateds;

    private AnnotationFinder annotationFinder;

    private String unitName;

    private String beanName;

    private ClassLoader loader;

    private Configuration configClone;

    private ORMCreator(MetaCreator creator) {
	Map<String, ArchiveUtils> aggregateds = creator.getAggregateds();
	this.aggregateds = new WeakHashMap<String, ArchiveUtils>(aggregateds);
	this.annotationFinder = creator.getAnnotationFinder();
    }

    /**
     * Checks weather {@link javax.persistence.Entity} annotated classes is need
     * to be filtered by {@link org.lightmare.annotations.UnitName} value
     *
     * @param className
     * @return boolean
     * @throws IOException
     */
    private boolean checkForUnitName(String className) throws IOException {

	boolean isValid;

	Class<?> entityClass;
	entityClass = ClassUtils.initClassForName(className);
	UnitName annotation = entityClass.getAnnotation(UnitName.class);
	isValid = annotation.value().equals(configClone.getAnnotatedUnitName());

	return isValid;
    }

    /**
     * Defines belongs or not of {@link javax.persistence.Entity} annotated
     * classes to jar file
     *
     * @param classSet
     * @param fileNameForBean
     */
    private void filterEntitiesForJar(Set<String> classSet, String fileNameForBean) {

	Map<String, String> classOwnersFiles = annotationFinder.getClassOwnersFiles();

	String fileNameForEntity;
	boolean toBeRemoved;
	for (String entityName : classSet) {
	    fileNameForEntity = classOwnersFiles.get(entityName);
	    toBeRemoved = ObjectUtils.notNullNotEquals(fileNameForEntity, fileNameForBean);
	    if (toBeRemoved) {
		classSet.remove(entityName);
	    }
	}
    }

    /**
     * Filters passed {@link Set} of entity classes for unit name
     *
     * @param classes
     * @return {@link Set} of {@link String}
     * @throws IOException
     */
    private Set<String> filterUnitEntities(Set<String> classes) throws IOException {

	Set<String> filtereds = new HashSet<String>();

	for (String className : classes) {
	    if (checkForUnitName(className)) {
		filtereds.add(className);
	    }
	}

	return filtereds;
    }

    /**
     * Filters {@link javax.persistence.Entity} annotated classes by name or by
     * {@link org.lightmare.annotations.UnitName} by configuration
     *
     * @param classSet
     * @return {@link List}<String>
     * @throws IOException
     */
    private List<String> filterEntities(Set<String> classSet) throws IOException {

	List<String> classes;

	if (configClone.getAnnotatedUnitName() == null) {
	    classes = CollectionUtils.translateToList(classSet);
	} else {
	    Set<String> filtereds = filterUnitEntities(classSet);
	    classes = CollectionUtils.translateToList(filtereds);
	}

	return classes;
    }

    /**
     * Gets entity class names from scanning
     *
     * @return
     */
    private Set<String> scanEntities() {

	Set<String> classSet;

	Map<String, Set<String>> annotationIndex = annotationFinder.getAnnotationIndex();
	classSet = annotationIndex.get(Entity.class.getName());
	String annotatedUnitName = configClone.getAnnotatedUnitName();
	if (annotatedUnitName == null) {
	    classSet = annotationIndex.get(Entity.class.getName());
	} else if (annotatedUnitName.equals(unitName)) {
	    Set<String> unitNamedSet = annotationIndex.get(UnitName.class.getName());
	    // Intersects entities with unit name annotated classes
	    classSet.retainAll(unitNamedSet);
	}

	return classSet;
    }

    /**
     * Scans and filters entities
     *
     * @param ioUtils
     * @param classOwnersFiles
     * @return {@link Set} of {@link String}s
     */
    private Set<String> scanAndFilterEntities(ArchiveUtils ioUtils, Map<String, String> classOwnersFiles) {

	Set<String> classSet = scanEntities();

	if (ObjectUtils.notNull(ioUtils)) {
	    String fileNameForBean = classOwnersFiles.get(beanName);
	    filterEntitiesForJar(classSet, fileNameForBean);
	}

	return classSet;
    }

    /**
     * Scans classes for entities
     *
     * @param ioUtils
     * @param classOwnersFiles
     * @param builder
     * @throws IOException
     */
    private void scanEntities(ArchiveUtils ioUtils, Map<String, String> classOwnersFiles, JpaManager.Builder builder)
	    throws IOException {

	Set<String> classSet = scanAndFilterEntities(ioUtils, classOwnersFiles);
	List<String> classes = filterEntities(classSet);
	builder.setClasses(classes);
    }

    /**
     * Adds JAR file {@link URL} to cache for read and deployment
     *
     * @param builder
     * @param ioUtils
     * @param classOwnersFiles
     */
    private void chackAndSetURL(JpaManager.Builder builder, ArchiveUtils ioUtils,
	    Map<String, String> classOwnersFiles) {

	if (ObjectUtils.notNull(ioUtils)) {
	    URL jarURL = ioUtils.getAppropriatedURL(classOwnersFiles, beanName);
	    builder.setURL(jarURL);
	}
    }

    /**
     * Checks configuration and scans entities from appropriated files
     *
     * @param builder
     * @param ioUtils
     * @param classOwnersFiles
     * @throws IOException
     */
    private void scanEntities(JpaManager.Builder builder, ArchiveUtils ioUtils, Map<String, String> classOwnersFiles)
	    throws IOException {

	if (configClone.isScanForEntities()) {
	    scanEntities(ioUtils, classOwnersFiles, builder);
	}
    }

    /**
     * Creates connection associated with unit name if such connection does not
     * exists yet
     *
     * @throws IOException
     */
    public void configureConnection() throws IOException {

	JpaManager.Builder builder = new JpaManager.Builder();
	Map<String, String> classOwnersFiles = annotationFinder.getClassOwnersFiles();
	ArchiveUtils ioUtils = aggregateds.get(beanName);
	chackAndSetURL(builder, ioUtils, classOwnersFiles);
	scanEntities(builder, ioUtils, classOwnersFiles);
	// Find data source name for thins unit name
	String dataSourceName = configClone.getDataSourceName(unitName);
	builder.dataSourceName(dataSourceName);
	// Builds connection for appropriated persistence unit name
	builder.configure(configClone).setClassLoader(loader).build().create(unitName);
    }

    /**
     * Instantiates {@link ORMCreator} with parameters
     *
     * @author Levan Tsinadze
     * @since 0.0.85
     *
     */
    public static class Builder {

	// ORMCreator instance to initialize
	private ORMCreator creator;

	/**
	 * Constructor with necessary {@link MetaCreator} instance
	 *
	 * @param creator
	 */
	public Builder(MetaCreator creator) {
	    this.creator = new ORMCreator(creator);
	}

	/**
	 * Sets unit name for connection
	 *
	 * @param unitName
	 * @return {@link Builder}
	 */
	public Builder setUnitName(String unitName) {
	    creator.unitName = unitName;
	    return this;
	}

	/**
	 * Sets EJB bean name
	 *
	 * @param beanName
	 * @return {@link Builder}
	 */
	public Builder setBeanName(String beanName) {
	    creator.beanName = beanName;
	    return this;
	}

	/**
	 * Sets {@link ClassLoader} for this bean initialization and use
	 *
	 * @param loader
	 * @return {@link Builder}
	 */
	public Builder setClassLoader(ClassLoader loader) {
	    creator.loader = loader;
	    return this;
	}

	/**
	 * Sets clone for {@link Configuration} instance
	 *
	 * @param configClone
	 * @return {@link Builder}
	 */
	public Builder setConfiguration(Configuration configClone) {
	    creator.configClone = configClone;
	    return this;
	}

	/**
	 * Returns initialized instance of {@link ORMCreator} class
	 *
	 * @return {@link ORMCreator} instance
	 */
	public ORMCreator build() {
	    return this.creator;
	}
    }
}
