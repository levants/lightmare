package org.lightmare.jpa.jta;

/**
 * Properties to use JTA for Hibernate configuration
 * 
 * @author levan
 * 
 */
public enum HibernateConfig {

    // JTA configuration for Hibernate deployment
    PLATFORM("hibernate.transaction.jta.platform",
	    "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform"), // JTA
										// platform

    FACTORY("hibernate.transaction.factory_class",
	    "org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory"); // factory
										    // class

    public String key;

    public String value;

    private HibernateConfig(String key, String value) {
	this.key = key;
	this.value = value;
    }
}
