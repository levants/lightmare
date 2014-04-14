package org.lightmare.jpa.spring;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.lightmare.utils.CollectionUtils;
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

    private Properties properties;

    public SpringData(DataSource dataSource,
	    PersistenceProvider persistenceProvider) {
	this.dataSource = dataSource;
	this.persistenceProvider = persistenceProvider;
    }

    public SpringData(DataSource dataSource,
	    PersistenceProvider persistenceProvider, Properties properties) {
	this(dataSource, persistenceProvider);
	this.properties = properties;
    }

    private LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

	entityManagerFactoryBean.setDataSource(dataSource);
	entityManagerFactoryBean.setPackagesToScan();
	entityManagerFactoryBean.setPersistenceProvider(persistenceProvider);
	if (CollectionUtils.valid(properties)) {
	    entityManagerFactoryBean.setJpaProperties(properties);
	}

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
}
