package org.lightmare.jpa.hibernate.jpa;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.jboss.logging.Logger;
import org.lightmare.jpa.MetaConfig;
import org.lightmare.jpa.hibernate.internal.PersistenceXmlParserImpl;

/**
 * Implementation of {@link HibernatePersistenceProvider} with additional
 * configuration
 * 
 * @author Levan Tsinadze
 * @Since 0.0.56=SNAPSHOT
 */
public class HibernatePersistenceProviderImpl extends
	HibernatePersistenceProvider {

    private MetaConfig metaConfig;

    private static final Logger LOG = Logger
	    .getLogger(HibernatePersistenceProvider.class);

    private HibernatePersistenceProviderImpl(MetaConfig metaConfig) {
	this.metaConfig = metaConfig;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public EntityManagerFactory createEntityManagerFactory(
	    String persistenceUnitName, Map properties) {
	LOG.tracef(
		"Starting createEntityManagerFactory for persistenceUnitName %s",
		persistenceUnitName);

	try {
	    final EntityManagerFactoryBuilder builder = getEntityManagerFactoryBuilderOrNull(
		    persistenceUnitName, properties);
	    if (builder == null) {
		LOG.trace("Could not obtain matching EntityManagerFactoryBuilder, returning null");
		return null;
	    } else {
		return builder.build();
	    }
	} catch (PersistenceException pe) {
	    throw pe;
	} catch (Exception e) {
	    LOG.debug("Unable to build entity manager factory", e);
	    throw new PersistenceException(
		    "Unable to build entity manager factory", e);
	}
    }

    @SuppressWarnings("rawtypes")
    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(
	    String persistenceUnitName, Map properties) {

	EntityManagerFactoryBuilder emfBuilder;

	ClassLoader loader = MetaConfig.getOverridenClassLoader(metaConfig);
	if (loader == null) {
	    emfBuilder = getEntityManagerFactoryBuilderOrNull(
		    persistenceUnitName, properties, null);
	} else {
	    emfBuilder = getEntityManagerFactoryBuilderOrNull(
		    persistenceUnitName, properties, loader);
	}

	return emfBuilder;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Map wrap(Map properties) {
	return properties == null ? Collections.emptyMap() : Collections
		.unmodifiableMap(properties);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(
	    String persistenceUnitName, Map properties,
	    ClassLoader providedClassLoader) {
	LOG.tracef(
		"Attempting to obtain correct EntityManagerFactoryBuilder for persistenceUnitName : %s",
		persistenceUnitName);

	final Map integration = wrap(properties);
	final List<ParsedPersistenceXmlDescriptor> units;
	try {
	    units = PersistenceXmlParserImpl.locatePersistenceUnits(
		    integration, metaConfig);
	} catch (Exception e) {
	    LOG.debug("Unable to locate persistence units", e);
	    throw new PersistenceException(
		    "Unable to locate persistence units", e);
	}

	LOG.debugf("Located and parsed %s persistence units; checking each",
		units.size());

	if (persistenceUnitName == null && units.size() > 1) {
	    // no persistence-unit name to look for was given and we found
	    // multiple persistence-units
	    throw new PersistenceException(
		    "No name provided and multiple persistence units found");
	}

	for (ParsedPersistenceXmlDescriptor persistenceUnit : units) {
	    LOG.debugf(
		    "Checking persistence-unit [name=%s, explicit-provider=%s] against incoming persistence unit name [%s]",
		    persistenceUnit.getName(),
		    persistenceUnit.getProviderClassName(), persistenceUnitName);

	    final boolean matches = persistenceUnitName == null
		    || persistenceUnit.getName().equals(persistenceUnitName);
	    if (!matches) {
		LOG.debug("Excluding from consideration due to name mis-match");
		continue;
	    }

	    // See if we (Hibernate) are the persistence provider
	    if (!ProviderChecker.isProvider(persistenceUnit, properties)) {
		LOG.debug("Excluding from consideration due to provider mis-match");
		continue;
	    }

	    return Bootstrap.getEntityManagerFactoryBuilder(persistenceUnit,
		    integration, providedClassLoader);
	}

	LOG.debug("Found no matching persistence units");
	return null;
    }

    /**
     * Builder class to instantiate {@link HibernatePersistenceProviderImpl}
     * class
     * 
     * @author Levan Tsinadze
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

	public HibernatePersistenceProviderImpl build() {
	    return new HibernatePersistenceProviderImpl(target);
	}
    }
}
