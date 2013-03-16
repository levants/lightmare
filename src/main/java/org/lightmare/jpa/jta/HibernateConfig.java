package org.lightmare.jpa.jta;

/**
 * Properties to use for hibernate
 * 
 * @author levan
 * 
 */
public class HibernateConfig {

    public static final String PLATFORM_KEY = "hibernate.transaction.jta.platform";

    public static final String PLATFORM_VALUE = "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform";

    public static final String FACTORY_KEY = "hibernate.transaction.factory_class";

    public static final String FACTORY_VALUE = "org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory";
}
