package org.lightmare.jpa.spring;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.lightmare.config.ConfigKeys;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * To initialize spring based connection
 * 
 * @author Levan Tsinadze
 * 
 */
public class SpringData {

    private String dataSourceName;

    private DataSource dataSource;

    private PersistenceProvider persistenceProvider;

    private String unitName;

    private Properties properties;

    private ClassLoader loader;

    private boolean swapDataSources;

    private SpringData(String dataSourceName,
	    PersistenceProvider persistenceProvider, String unitName) {
	this.dataSourceName = dataSourceName;
	this.persistenceProvider = persistenceProvider;
	this.unitName = unitName;
    }

    /**
     * Initializes data source name from properties
     */
    private void initDataSourceName() {

	if (dataSourceName == null || dataSourceName.isEmpty()) {
	    Properties nameProperties = new Properties();
	    nameProperties.putAll(properties);
	    dataSourceName = nameProperties
		    .getProperty(ConfigKeys.SPRING_DS_NAME_KEY.key);
	    properties.remove(ConfigKeys.SPRING_DS_NAME_KEY.key);
	}
    }

    /**
     * Resolves data source JNDI name from persistence.xml file
     */
    private void initDataSourceFromUnit() {

	ParsedPersistenceXmlDescriptor persistenceUnit = ObjectUtils.cast(
		persistenceProvider, HibernatePersistenceProviderExt.class)
		.getPersistenceXmlDescriptor(unitName, properties);

	Object dataSourceValue;
	if (swapDataSources) {
	    dataSourceValue = persistenceUnit.getNonJtaDataSource();
	} else {
	    dataSourceValue = persistenceUnit.getJtaDataSource();
	}

	if (dataSourceValue == null) {
	    dataSourceName = null;
	} else if (dataSourceValue instanceof String) {
	    dataSourceName = ObjectUtils.cast(dataSourceValue, String.class);
	} else {
	    dataSourceName = dataSourceValue.toString();
	}
    }

    /**
     * Gets {@link DataSource} by its JNDI name for Spring data configuration
     * 
     * @return {@link DataSource}
     * @throws IOException
     */
    private void initDataSource() throws IOException {

	initDataSourceName();
	if (dataSourceName == null || dataSourceName.isEmpty()) {
	    if (persistenceProvider instanceof HibernatePersistenceProviderExt) {
		initDataSourceFromUnit();
	    }
	}

	dataSource = JndiManager.lookup(dataSourceName);
    }

    /**
     * Creates LocalContainerEntityManagerFactoryBean for container scoped use
     * 
     * @return {@link LocalContainerEntityManagerFactoryBean}
     */
    private LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

	entityManagerFactoryBean.setPersistenceUnitName(unitName);
	if (swapDataSources) {
	    entityManagerFactoryBean.setDataSource(dataSource);
	} else {
	    entityManagerFactoryBean.setJtaDataSource(dataSource);
	}

	if (ObjectUtils.notNull(loader)) {
	    entityManagerFactoryBean.setBeanClassLoader(loader);
	}

	entityManagerFactoryBean.setPackagesToScan();
	entityManagerFactoryBean.setPersistenceProvider(persistenceProvider);
	if (CollectionUtils.valid(properties)) {
	    entityManagerFactoryBean.setJpaProperties(properties);
	}

	entityManagerFactoryBean.afterPropertiesSet();

	return entityManagerFactoryBean;
    }

    /**
     * Creates JpaTransactionManager for container scoped use
     * 
     * @return {@link JpaTransactionManager}
     */
    public JpaTransactionManager transactionManager() {

	JpaTransactionManager transactionManager = new JpaTransactionManager();

	LocalContainerEntityManagerFactoryBean emfBean = entityManagerFactory();
	EntityManagerFactory emf = emfBean.getObject();
	transactionManager.setEntityManagerFactory(emf);

	return transactionManager;

    }

    public EntityManagerFactory getEmf() throws IOException {

	EntityManagerFactory emf;

	initDataSource();
	JpaTransactionManager transactionManager = transactionManager();
	emf = transactionManager.getEntityManagerFactory();

	return emf;
    }

    public static class Builder {

	private SpringData springData;

	public Builder(String dataSourceName,
		PersistenceProvider persistenceProvider, String unitName) {
	    this.springData = new SpringData(dataSourceName,
		    persistenceProvider, unitName);
	}

	public Builder properties(Properties properties) {
	    springData.properties = properties;
	    return this;
	}

	public Builder properties(Map<Object, Object> properties) {

	    if (CollectionUtils.valid(properties)) {
		springData.properties = new Properties();
		springData.properties.putAll(properties);
	    }

	    return this;
	}

	public Builder classLoader(ClassLoader loader) {
	    springData.loader = loader;
	    return this;
	}

	public Builder swapDataSource(boolean swapDataSources) {
	    springData.swapDataSources = swapDataSources;
	    return this;
	}

	public SpringData build() {
	    return this.springData;
	}
    }
}
