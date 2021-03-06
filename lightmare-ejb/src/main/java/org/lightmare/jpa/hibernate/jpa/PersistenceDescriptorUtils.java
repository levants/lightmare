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
package org.lightmare.jpa.hibernate.jpa;

import java.util.List;

import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.lightmare.jpa.MetaConfig;
import org.lightmare.jpa.hibernate.internal.PersistenceUnitSwapDescriptor;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class to make changes for JPA configuration
 * 
 * @author Levan Tsinadze
 * @since 0.1.2
 */
public class PersistenceDescriptorUtils {

    private static void setTransactionType(PersistenceUnitDescriptor persistenceUnit,
	    PersistenceUnitTransactionType type) {

	if (persistenceUnit instanceof ParsedPersistenceXmlDescriptor) {
	    ObjectUtils.cast(persistenceUnit, ParsedPersistenceXmlDescriptor.class).setTransactionType(type);
	} else if (persistenceUnit instanceof PersistenceUnitSwapDescriptor) {
	    ObjectUtils.cast(persistenceUnit, PersistenceUnitSwapDescriptor.class).setTransactionType(type);
	}
    }

    private static void setNonJtaDataSource(PersistenceUnitDescriptor persistenceUnit, Object dataSource) {

	if (persistenceUnit instanceof ParsedPersistenceXmlDescriptor) {
	    ObjectUtils.cast(persistenceUnit, ParsedPersistenceXmlDescriptor.class).setNonJtaDataSource(dataSource);
	} else if (persistenceUnit instanceof PersistenceUnitSwapDescriptor) {
	    ObjectUtils.cast(persistenceUnit, PersistenceUnitSwapDescriptor.class).setNonJtaDataSource(dataSource);
	}
    }

    private static void addClasses(PersistenceUnitDescriptor persistenceUnit, List<String> classes) {

	if (persistenceUnit instanceof ParsedPersistenceXmlDescriptor) {
	    ObjectUtils.cast(persistenceUnit, ParsedPersistenceXmlDescriptor.class).addClasses(classes);
	} else if (persistenceUnit instanceof PersistenceUnitSwapDescriptor) {
	    ObjectUtils.cast(persistenceUnit, PersistenceUnitSwapDescriptor.class).addClasses(classes);
	}
    }

    /**
     * Resolved which transaction type should be set from {@link MetaConfig}
     * object
     * 
     * @param persistenceUnit
     * @param metaConfig
     */
    public static void resolveTransactionType(PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	if (MetaConfig.isSwapDataSource(metaConfig)) {
	    setTransactionType(persistenceUnit, PersistenceUnitTransactionType.RESOURCE_LOCAL);
	}
    }

    /**
     * Resolves data source from {@link MetaConfig} object
     * 
     * @param persistenceUnit
     * @param metaConfig
     */
    public static void resolveDataSource(PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	Object dataSource = persistenceUnit.getJtaDataSource();
	if (MetaConfig.isSwapDataSource(metaConfig)) {
	    setNonJtaDataSource(persistenceUnit, dataSource);
	}
    }

    /**
     * Resolves entity classes from {@link MetaConfig} object
     * 
     * @param persistenceUnit
     * @param metaConfig
     */
    public static void resolveEntities(PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	List<String> classes = MetaConfig.getClasses(metaConfig);
	if (CollectionUtils.valid(classes)) {
	    addClasses(persistenceUnit, classes);
	}
    }

    /**
     * Changes JPA configuration and resolves additional data from
     * {@link MetaConfig} parameter
     * 
     * @param persistenceUnit
     * @param metaConfig
     */
    public static void resolve(PersistenceUnitDescriptor persistenceUnit, MetaConfig metaConfig) {

	PersistenceDescriptorUtils.resolveTransactionType(persistenceUnit, metaConfig);
	PersistenceDescriptorUtils.resolveDataSource(persistenceUnit, metaConfig);
	PersistenceDescriptorUtils.resolveEntities(persistenceUnit, metaConfig);
    }
}
