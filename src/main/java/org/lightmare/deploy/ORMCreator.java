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
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Class to initialize and deploy JPA units
 * 
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
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
	this.aggregateds = new WeakHashMap<String, ArchiveUtils>(
		creator.getAggregateds());
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
	entityClass = MetaUtils.initClassForName(className);
	UnitName annotation = entityClass.getAnnotation(UnitName.class);
	isValid = annotation.value().equals(configClone.getAnnotatedUnitName());

	return isValid;
    }

    /**
     * Defines belongs or not of {@link javax.persistence.Entity} annotated
     * classes to jar file
     * 
     * @param classSet
     * @return {@link List}<String>
     */
    private void filterEntitiesForJar(Set<String> classSet,
	    String fileNameForBean) {

	Map<String, String> classOwnersFiles = annotationFinder
		.getClassOwnersFiles();

	String fileNameForEntity;
	boolean toBeRemoved;
	for (String entityName : classSet) {
	    fileNameForEntity = classOwnersFiles.get(entityName);
	    toBeRemoved = ObjectUtils.notNullNotEquals(fileNameForEntity,
		    fileNameForBean);
	    if (toBeRemoved) {
		classSet.remove(entityName);
	    }
	}
    }

    /**
     * Filters {@link javax.persistence.Entity} annotated classes by name or by
     * {@link org.lightmare.annotations.UnitName} by configuration
     * 
     * @param classSet
     * @return {@link List}<String>
     * @throws IOException
     */
    private List<String> filterEntities(Set<String> classSet)
	    throws IOException {

	List<String> classes;

	if (configClone.getAnnotatedUnitName() == null) {
	    classes = CollectionUtils.translateToList(classSet);
	} else {

	    Set<String> filtereds = new HashSet<String>();
	    boolean valid;
	    for (String className : classSet) {
		valid = checkForUnitName(className);
		if (valid) {
		    filtereds.add(className);
		}
	    }
	    classes = CollectionUtils.translateToList(filtereds);
	}

	return classes;
    }

    /**
     * Creates connection associated with unit name if such connection does not
     * exists yet
     * 
     * @param unitName
     * @param beanName
     * @throws IOException
     */
    protected void configureConnection() throws IOException {

	JpaManager.Builder builder = new JpaManager.Builder();
	Map<String, String> classOwnersFiles = annotationFinder
		.getClassOwnersFiles();
	ArchiveUtils ioUtils = aggregateds.get(beanName);

	if (ObjectUtils.notNull(ioUtils)) {
	    URL jarURL = ioUtils.getAppropriatedURL(classOwnersFiles, beanName);
	    builder.setURL(jarURL);
	}

	if (configClone.isScanForEntities()) {

	    Set<String> classSet;
	    Map<String, Set<String>> annotationIndex = annotationFinder
		    .getAnnotationIndex();
	    classSet = annotationIndex.get(Entity.class.getName());
	    String annotatedUnitName = configClone.getAnnotatedUnitName();

	    if (annotatedUnitName == null) {
		classSet = annotationIndex.get(Entity.class.getName());
	    } else if (annotatedUnitName.equals(unitName)) {
		Set<String> unitNamedSet = annotationIndex.get(UnitName.class
			.getName());
		// Intersects entities with unit name annotated classes
		classSet.retainAll(unitNamedSet);
	    }

	    if (ObjectUtils.notNull(ioUtils)) {
		String fileNameForBean = classOwnersFiles.get(beanName);
		filterEntitiesForJar(classSet, fileNameForBean);
	    }

	    List<String> classes = filterEntities(classSet);
	    builder.setClasses(classes);
	}

	// Builds connection for appropriated persistence unit name
	builder.configure(configClone).setClassLoader(loader).build()
		.create(unitName);
    }

    /**
     * Instantiates {@link ORMCreator} with parameters
     * 
     * @author Levan Tsinadze
     * @since 0.0.85-SNAPSHOT
     * 
     */
    protected static class Builder {

	// ORMCreator instance to initialize
	private ORMCreator ormCreator;

	/**
	 * Constructor with necessary {@link MetaCreator} instance
	 * 
	 * @param creator
	 */
	protected Builder(MetaCreator creator) {
	    this.ormCreator = new ORMCreator(creator);
	}

	/**
	 * Sets unit name for connection
	 * 
	 * @param unitName
	 * @return {@link Builder}
	 */
	protected Builder setUnitName(String unitName) {

	    ormCreator.unitName = unitName;

	    return this;
	}

	/**
	 * Sets EJB bean name
	 * 
	 * @param beanName
	 * @return {@link Builder}
	 */
	protected Builder setBeanName(String beanName) {

	    ormCreator.beanName = beanName;

	    return this;
	}

	/**
	 * Sets {@link ClassLoader} for this bean initialization and use
	 * 
	 * @param loader
	 * @return {@link Builder}
	 */
	protected Builder setClassLoader(ClassLoader loader) {

	    ormCreator.loader = loader;

	    return this;
	}

	/**
	 * Sets clone for {@link Configuration} instance
	 * 
	 * @param configClone
	 * @return {@link Builder}
	 */
	protected Builder setConfiguration(Configuration configClone) {

	    ormCreator.configClone = configClone;

	    return this;
	}

	/**
	 * Returns initialized instance of {@link ORMCreator} class
	 * 
	 * @return
	 */
	public ORMCreator build() {
	    return this.ormCreator;
	}
    }
}
