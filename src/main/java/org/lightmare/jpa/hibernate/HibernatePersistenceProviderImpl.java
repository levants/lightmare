package org.lightmare.jpa.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.jboss.logging.Logger;
import org.lightmare.jpa.hibernate.internal.PersistenceXmlParserImpl;

public class HibernatePersistenceProviderImpl extends
	HibernatePersistenceProvider {

    private static final Logger LOG = Logger
	    .getLogger(HibernatePersistenceProvider.class);

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
	    units = PersistenceXmlParserImpl
		    .locatePersistenceUnits(integration);
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
}
