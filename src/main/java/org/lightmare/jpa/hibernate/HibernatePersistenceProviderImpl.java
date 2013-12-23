package org.lightmare.jpa.hibernate;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.jboss.logging.Logger;
import org.lightmare.jpa.hibernate.internal.PersistenceXmlParserImpl;

public class HibernatePersistenceProviderImpl extends
	HibernatePersistenceProvider {

    private String persistenceUnitName;
    private String cfgXmlResource;

    private Configuration cfg;
    // made transient and not restored in deserialization on purpose, should no
    // longer be called after restoration
    private PersistenceUnitTransactionType transactionType;
    private boolean discardOnClose;
    // made transient and not restored in deserialization on purpose, should no
    // longer be called after restoration
    private transient ClassLoader overridenClassLoader;
    private boolean isConfigurationProcessed = false;

    // arguments from lightmare
    private List<String> classes;
    private Enumeration<URL> xmls;
    private boolean swapDataSource;
    private boolean scanArchives;

    private String shortPath = "/META-INF/persistence.xml";

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

    public static class Builder {

	private HibernatePersistenceProviderImpl target;

	public Builder() {
	    target = new HibernatePersistenceProviderImpl();
	}

	public Builder setClasses(List<String> classes) {
	    target.classes = classes;

	    return this;
	}

	public Builder setXmls(Enumeration<URL> xmls) {
	    target.xmls = xmls;

	    return this;
	}

	public Builder setShortPath(String shortPath) {
	    target.shortPath = shortPath;

	    return this;
	}

	public Builder setSwapDataSource(boolean swapDataSource) {
	    target.swapDataSource = swapDataSource;

	    return this;
	}

	public Builder setScanArchives(boolean scanArchives) {
	    target.scanArchives = scanArchives;

	    return this;
	}

	public Builder setOverridenClassLoader(ClassLoader overridenClassLoader) {
	    target.overridenClassLoader = overridenClassLoader;

	    return this;
	}

	public HibernatePersistenceProviderImpl build() {

	    return target;
	}
    }
}
