package org.lightmare.jpa.spring;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

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

    private DataSource dataSource;

    private PersistenceProvider persistenceProvider;

    private String unitName;

    private Properties properties;

    private ClassLoader loader;

    private boolean swapDataSources;

    private SpringData(DataSource dataSource,
	    PersistenceProvider persistenceProvider, String unitName) {
	this.dataSource = dataSource;
	this.persistenceProvider = persistenceProvider;
	this.unitName = unitName;
    }

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

    public JpaTransactionManager transactionManager() {

	JpaTransactionManager transactionManager = new JpaTransactionManager();

	LocalContainerEntityManagerFactoryBean emfBean = entityManagerFactory();
	EntityManagerFactory emf = emfBean.getObject();
	transactionManager.setEntityManagerFactory(emf);

	return transactionManager;

    }

    public EntityManagerFactory getEmf() {

	EntityManagerFactory emf;

	JpaTransactionManager transactionManager = transactionManager();
	emf = transactionManager.getEntityManagerFactory();

	return emf;
    }

    public static class Builder {

	private SpringData springData;

	public Builder(DataSource dataSource,
		PersistenceProvider persistenceProvider, String unitName) {
	    this.springData = new SpringData(dataSource, persistenceProvider,
		    unitName);
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
