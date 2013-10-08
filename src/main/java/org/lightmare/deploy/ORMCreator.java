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
import org.lightmare.scannotation.AnnotationDB;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Class to initialize and deploy JPA units
 * 
 * @author levan
 * @since 0.0.80-SNAPSHOT
 */
public class ORMCreator {

    private Map<String, ArchiveUtils> aggregateds;

    private AnnotationDB annotationDB;

    private ORMCreator(MetaCreator creator) {

	this.aggregateds = new WeakHashMap<String, ArchiveUtils>(
		creator.getAggregateds());
	this.annotationDB = creator.getAnnotationDB();
    }

    /**
     * Checks weather {@link javax.persistence.Entity} annotated classes is need
     * to be filtered by {@link org.lightmare.annotations.UnitName} value
     * 
     * @param className
     * @return boolean
     * @throws IOException
     */
    private boolean checkForUnitName(String className, Configuration cloneConfig)
	    throws IOException {

	boolean isValid;

	Class<?> entityClass;
	entityClass = MetaUtils.initClassForName(className);
	UnitName annotation = entityClass.getAnnotation(UnitName.class);
	isValid = annotation.value().equals(cloneConfig.getAnnotatedUnitName());

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

	Map<String, String> classOwnersFiles = annotationDB
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
    private List<String> filterEntities(Set<String> classSet,
	    Configuration configClone) throws IOException {

	List<String> classes;

	if (configClone.getAnnotatedUnitName() == null) {
	    classes = CollectionUtils.translateToList(classSet);
	} else {

	    Set<String> filtereds = new HashSet<String>();
	    boolean valid;
	    for (String className : classSet) {
		valid = checkForUnitName(className, configClone);
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
    protected void configureConnection(String unitName, String beanName,
	    ClassLoader loader, Configuration configClone) throws IOException {

	JpaManager.Builder builder = new JpaManager.Builder();
	Map<String, String> classOwnersFiles = annotationDB
		.getClassOwnersFiles();
	ArchiveUtils ioUtils = aggregateds.get(beanName);

	if (ObjectUtils.notNull(ioUtils)) {
	    URL jarURL = ioUtils.getAppropriatedURL(classOwnersFiles, beanName);
	    builder.setURL(jarURL);
	}

	if (configClone.isScanForEntities()) {

	    Set<String> classSet;
	    Map<String, Set<String>> annotationIndex = annotationDB
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

	    List<String> classes = filterEntities(classSet, configClone);
	    builder.setClasses(classes);
	}

	// Builds connection for appropriated persistence unit name
	builder.configure(configClone).setClassLoader(loader).build()
		.create(unitName);
    }

    /**
     * Instantiates {@link ORMCreator} by constructor
     * 
     * @param creator
     * @return {@link ORMCreator}
     */
    protected static ORMCreator get(MetaCreator creator) {

	return new ORMCreator(creator);
    }
}
