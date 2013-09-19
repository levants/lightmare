package org.lightmare.jpa.jta;

/**
 * Properties to use for Hibernate configuration
 * 
 * @author levan
 * 
 */
public enum HibernateConfig {

    // JTA configuration for Hibernate
    PLATFORM("hibernate.transaction.jta.platform",
	    "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform"), // JTA
										// platform
    FACTORY("hibernate.transaction.factory_class",
	    "org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory"); // JTA
										    // factory
										    // class

    public String key;

    public String value;

    private HibernateConfig(String key, String value) {
	this.key = key;
	this.value = value;
    }
}
