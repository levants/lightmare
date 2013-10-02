package org.lightmare.jpa.jta;

/**
 * Properties to use JTA in Hibernate environment
 * 
 * @author levan
 * @since 0.0.34-SNAPSHOT
 */
public enum HibernateConfig {

    // JTA configuration for Hibernate deployment - JTA platform
    PLATFORM("hibernate.transaction.jta.platform",
	    "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform"),

    // factory class
    FACTORY("hibernate.transaction.factory_class",
	    "org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory");

    public String key;

    public String value;

    private HibernateConfig(String key, String value) {
	this.key = key;
	this.value = value;
    }
}
