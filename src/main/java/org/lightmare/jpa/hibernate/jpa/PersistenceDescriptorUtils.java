package org.lightmare.jpa.hibernate.jpa;

import java.util.List;

import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.lightmare.jpa.MetaConfig;
import org.lightmare.jpa.hibernate.internal.PersistenceUnitSwapDescriptor;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Utility class to make changes for JPA configuration
 * 
 * @author Levan Tsinadze
 * 
 */
public class PersistenceDescriptorUtils {

    private static void setTransactionType(
	    PersistenceUnitDescriptor persistenceUnit,
	    PersistenceUnitTransactionType type) {

	if (persistenceUnit instanceof ParsedPersistenceXmlDescriptor) {
	    ObjectUtils.cast(persistenceUnit,
		    ParsedPersistenceXmlDescriptor.class).setTransactionType(
		    type);
	} else if (persistenceUnit instanceof PersistenceUnitSwapDescriptor) {
	    ObjectUtils.cast(persistenceUnit,
		    PersistenceUnitSwapDescriptor.class).setTransactionType(
		    type);
	}
    }

    private static void setNonJtaDataSource(
	    PersistenceUnitDescriptor persistenceUnit, Object dataSource) {

	if (persistenceUnit instanceof ParsedPersistenceXmlDescriptor) {
	    ObjectUtils.cast(persistenceUnit,
		    ParsedPersistenceXmlDescriptor.class).setNonJtaDataSource(
		    dataSource);
	} else if (persistenceUnit instanceof PersistenceUnitSwapDescriptor) {
	    ObjectUtils.cast(persistenceUnit,
		    PersistenceUnitSwapDescriptor.class).setNonJtaDataSource(
		    dataSource);
	}
    }

    private static void addClasses(PersistenceUnitDescriptor persistenceUnit,
	    List<String> classes) {

	if (persistenceUnit instanceof ParsedPersistenceXmlDescriptor) {
	    ObjectUtils.cast(persistenceUnit,
		    ParsedPersistenceXmlDescriptor.class).addClasses(classes);
	} else if (persistenceUnit instanceof PersistenceUnitSwapDescriptor) {
	    ObjectUtils.cast(persistenceUnit,
		    PersistenceUnitSwapDescriptor.class).addClasses(classes);
	}
    }

    /**
     * Resolved which transaction type should be set from {@link MetaConfig}
     * object
     * 
     * @param persistenceUnit
     * @param MetaConfig
     *            metaConfig)
     */
    public static void resolveTransactionType(
	    PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	if (MetaConfig.isSwapDataSource(metaConfig)) {
	    setTransactionType(persistenceUnit,
		    PersistenceUnitTransactionType.RESOURCE_LOCAL);
	}
    }

    /**
     * Resolves data source from {@link MetaConfig} object
     * 
     * @param persistenceUnit
     * @param MetaConfig
     *            metaConfig)
     */
    public static void resolveDataSource(
	    PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	Object dataSource = persistenceUnit.getJtaDataSource();
	if (MetaConfig.isSwapDataSource(metaConfig)) {
	    setNonJtaDataSource(persistenceUnit, dataSource);
	}
    }

    /**
     * Resolves entity classes from {@link MetaConfig} object
     * 
     * @param persistenceUnit
     * @param MetaConfig
     *            metaConfig)
     */
    public static void resolveEntities(
	    PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	List<String> classes = MetaConfig.getClasses(metaConfig);
	if (CollectionUtils.valid(classes)) {
	    addClasses(persistenceUnit, classes);
	}
    }
}
