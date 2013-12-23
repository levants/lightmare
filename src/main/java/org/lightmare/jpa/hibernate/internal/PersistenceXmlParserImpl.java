package org.lightmare.jpa.hibernate.internal;

import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

public class PersistenceXmlParserImpl extends PersistenceXmlParser {

    public PersistenceXmlParserImpl(ClassLoaderService classLoaderService,
	    PersistenceUnitTransactionType defaultTransactionType) {
	super(classLoaderService, defaultTransactionType);
    }
}
