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

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.jboss.logging.Logger;
import org.lightmare.jpa.MetaConfig;
import org.lightmare.jpa.hibernate.internal.PersistenceUnitSwapDescriptor;
import org.lightmare.jpa.hibernate.internal.PersistenceXmlParserImpl;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of {@link HibernatePersistenceProvider} with additional
 * configuration
 * 
 * @author Steve Ebersole, Levan Tsinadze
 * @Since 0.1.0
 */
public class HibernatePersistenceProviderExt extends
	HibernatePersistenceProvider {

    // Additional configuration for extension
    private MetaConfig metaConfig;

    private static final Logger LOG = Logger
	    .getLogger(HibernatePersistenceProviderExt.class);

    /**
     * Constructor with {@link MetaConfig} as additional JPA properties
     * 
     * @param metaConfig
     */
    private HibernatePersistenceProviderExt(MetaConfig metaConfig) {
	this.metaConfig = metaConfig;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public EntityManagerFactory createEntityManagerFactory(
	    String persistenceUnitName, Map properties) {

	EntityManagerFactory emf;

	LOG.tracef(
		"Starting createEntityManagerFactory for persistenceUnitName %s",
		persistenceUnitName);
	try {
	    final EntityManagerFactoryBuilder builder = getEntityManagerFactoryBuilderOrNull(
		    persistenceUnitName, properties);
	    if (builder == null) {
		LOG.trace("Could not obtain matching EntityManagerFactoryBuilder, returning null");
		emf = null;
	    } else {
		emf = builder.build();
	    }
	} catch (PersistenceException pe) {
	    throw pe;
	} catch (Exception ex) {
	    LOG.debug("Unable to build entity manager factory", ex);
	    throw new PersistenceException(
		    "Unable to build entity manager factory", ex);
	}

	return emf;
    }

    /**
     * Enriches and configures passed {@link PersistenceUnitInfo} wrapper
     * {@inheritDoc}
     * 
     * @param info
     * @return {@link PersistenceUnitDescriptor}
     */
    protected PersistenceUnitDescriptor getPersistenceUnitDescriptor(
	    PersistenceUnitInfo info) {

	PersistenceUnitDescriptor descriptor = new PersistenceUnitSwapDescriptor(
		info);
	PersistenceDescriptorUtils.resolve(descriptor, metaConfig);

	return descriptor;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Improved with transaction and data source swapping properties
     */
    @SuppressWarnings("rawtypes")
    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(
	    PersistenceUnitInfo info, Map properties) {

	EntityManagerFactory emf;

	LOG.tracef("Starting createContainerEntityManagerFactory : %s",
		info.getPersistenceUnitName());

	PersistenceUnitDescriptor descriptor = getPersistenceUnitDescriptor(info);
	emf = Bootstrap.getEntityManagerFactoryBuilder(descriptor, properties)
		.build();

	return emf;
    }

    /**
     * Generates schema from {@link PersistenceUnitInfo} instance
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void generateSchema(PersistenceUnitInfo info, Map map) {
	LOG.tracef("Starting generateSchema : PUI.name=%s",
		info.getPersistenceUnitName());

	PersistenceUnitDescriptor descriptor = getPersistenceUnitDescriptor(info);
	final EntityManagerFactoryBuilder builder = Bootstrap
		.getEntityManagerFactoryBuilder(descriptor, map);
	builder.generateSchema();
    }

    /**
     * Generates schema from passed persistence unit name and {@link Map} of
     * properties
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean generateSchema(String persistenceUnitName, Map map) {
	LOG.tracef("Starting generateSchema for persistenceUnitName %s",
		persistenceUnitName);

	boolean valid;

	final EntityManagerFactoryBuilder builder = getEntityManagerFactoryBuilderOrNull(
		persistenceUnitName, map);
	if (builder == null) {
	    LOG.trace("Could not obtain matching EntityManagerFactoryBuilder, returning false");
	    valid = Boolean.FALSE;
	} else {
	    builder.generateSchema();
	    valid = Boolean.TRUE;
	}

	return valid;
    }

    /**
     * Gets {@link EntityManagerFactoryBuilder} for passed unit name and
     * {@link Map} of properties
     */
    @SuppressWarnings("rawtypes")
    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(
	    String persistenceUnitName, Map properties) {

	EntityManagerFactoryBuilder emfBuilder;

	ClassLoader loader = MetaConfig.getOverridenClassLoader(metaConfig);
	emfBuilder = getEntityManagerFactoryBuilderOrNull(persistenceUnitName,
		properties, loader);

	return emfBuilder;
    }

    /**
     * Creates {@link PersistenceUnitDescriptor} for passed persistence unit
     * name {@link Map} of properties and {@link ClassLoader} instance
     * 
     * @param persistenceUnitName
     * @param properties
     * @param providedClassLoader
     * @return {@link PersistenceUnitDescriptor}
     */
    @SuppressWarnings({ "rawtypes" })
    public PersistenceUnitDescriptor getPersistenceXmlDescriptor(
	    String persistenceUnitName, Map properties,
	    ClassLoader providedClassLoader) {

	ParsedPersistenceXmlDescriptor descriptor = null;

	final Map integration = wrap(properties);
	final List<ParsedPersistenceXmlDescriptor> units;

	try {
	    units = PersistenceXmlParserImpl.locatePersistenceUnits(
		    integration, metaConfig);
	} catch (Exception ex) {
	    LOG.debug("Unable to locate persistence units", ex);
	    throw new PersistenceException(
		    "Unable to locate persistence units", ex);
	}

	LOG.debugf("Located and parsed %s persistence units; checking each",
		units.size());

	if (persistenceUnitName == null
		&& units.size() > CollectionUtils.SINGLTON_LENGTH) {
	    // no persistence-unit name to look for was given and we found
	    // multiple persistence-units
	    throw new PersistenceException(
		    "No name provided and multiple persistence units found");
	}

	boolean notMatches = Boolean.TRUE;
	Iterator<ParsedPersistenceXmlDescriptor> descriptorIterator = units
		.iterator();
	ParsedPersistenceXmlDescriptor persistenceUnit;
	while (descriptorIterator.hasNext() && notMatches) {
	    persistenceUnit = descriptorIterator.next();
	    LOG.debugf(
		    "Checking persistence-unit [name=%s, explicit-provider=%s] against incoming persistence unit name [%s]",
		    persistenceUnit.getName(),
		    persistenceUnit.getProviderClassName(), persistenceUnitName);

	    final boolean matches = (persistenceUnitName == null || persistenceUnitName
		    .equals(persistenceUnit.getName()));
	    notMatches = ObjectUtils.notTrue(matches);
	    if (notMatches) {
		LOG.debug("Excluding from consideration due to name mis-match");
		// See if we (Hibernate) are the persistence provider
	    } else if (ObjectUtils.notTrue(ProviderChecker.isProvider(
		    persistenceUnit, properties))) {
		LOG.debug("Excluding from consideration due to provider mis-match");
	    } else {
		descriptor = persistenceUnit;
	    }
	}

	return descriptor;
    }

    @SuppressWarnings({ "rawtypes" })
    public PersistenceUnitDescriptor getPersistenceUnitDescriptor(
	    String persistenceUnitName, Map properties) {

	PersistenceUnitDescriptor persistenceUnit;

	ClassLoader loader = MetaConfig.getOverridenClassLoader(metaConfig);
	persistenceUnit = getPersistenceXmlDescriptor(persistenceUnitName,
		properties, loader);

	return persistenceUnit;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(
	    String persistenceUnitName, Map properties,
	    ClassLoader providedClassLoader) {

	EntityManagerFactoryBuilder builder;

	LOG.tracef(
		"Attempting to obtain correct EntityManagerFactoryBuilder for persistenceUnitName : %s",
		persistenceUnitName);

	final Map integration = wrap(properties);
	PersistenceUnitDescriptor persistenceUnit = getPersistenceXmlDescriptor(
		persistenceUnitName, properties, providedClassLoader);
	if (persistenceUnit == null) {
	    LOG.debug("Found no matching persistence units");
	    builder = null;
	} else {
	    builder = Bootstrap.getEntityManagerFactoryBuilder(persistenceUnit,
		    integration, providedClassLoader);
	}

	return builder;
    }

    /**
     * Builder class to instantiate {@link HibernatePersistenceProviderExt}
     * class
     * 
     * @author Levan Tsinadze
     * @since 0.1.0
     * 
     */
    public static class Builder {

	private MetaConfig target;

	public Builder() {
	    target = new MetaConfig();
	}

	public Builder setClasses(List<String> classes) {
	    target.setClasses(classes);
	    return this;
	}

	public Builder setXmls(List<URL> xmls) {
	    target.setXmls(xmls);
	    return this;
	}

	public Builder setShortPath(String shortPath) {
	    target.setShortPath(shortPath);
	    return this;
	}

	public Builder setSwapDataSource(boolean swapDataSource) {
	    target.setSwapDataSource(swapDataSource);
	    return this;
	}

	public Builder setScanArchives(boolean scanArchives) {
	    target.setScanArchives(scanArchives);
	    return this;
	}

	public Builder setOverridenClassLoader(ClassLoader overridenClassLoader) {
	    target.setOverridenClassLoader(overridenClassLoader);
	    return this;
	}

	public HibernatePersistenceProviderExt build() {
	    return new HibernatePersistenceProviderExt(target);
	}
    }
}
