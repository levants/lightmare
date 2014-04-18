/*
 * Lightmare, Embeddable EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2014, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.jpa.spring;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.lightmare.config.ConfigKeys;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.jpa.jta.HibernateConfig;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * To initialize spring based connection
 * 
 * @author Levan Tsinadze
 * @since 0.1.2
 */
public class SpringORM {

    private String dataSourceName;

    private DataSource dataSource;

    private PersistenceProvider persistenceProvider;

    private String unitName;

    private Properties properties;

    private ClassLoader loader;

    private boolean swapDataSources;

    private PersistenceUnitDescriptor persistenceUnit;

    private SpringORM(String dataSourceName,
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
     * Adds additional configuration to properties
     */
    private void initProperties() {

	if (persistenceProvider instanceof HibernatePersistenceProviderExt) {
	    persistenceUnit = ObjectUtils.cast(persistenceProvider,
		    HibernatePersistenceProviderExt.class)
		    .getPersistenceXmlDescriptor(unitName, properties);
	    properties.putAll(persistenceUnit.getProperties());
	}
    }

    /**
     * Adds default transaction properties for Spring implementation of JTA data
     * sources
     */
    private void addTransactionManager() {

	CollectionUtils.putIfAbscent(properties, HibernateConfig.FACTORY.key,
		HibernateConfig.FACTORY.value);
	CollectionUtils.putIfAbscent(properties, HibernateConfig.PLATFORM.key,
		JtaTransactionManager.class.getName());
    }

    /**
     * Adds JTA transaction configuration and appropriated data source
     * 
     * @param entityManagerFactoryBean
     */
    private void addJtaDatasource(
	    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {

	addTransactionManager();
	entityManagerFactoryBean.setJtaDataSource(dataSource);
    }

    /**
     * Creates {@link LocalContainerEntityManagerFactoryBean} for container
     * scoped use
     * 
     * @return {@link LocalContainerEntityManagerFactoryBean}
     */
    private LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

	entityManagerFactoryBean.setPersistenceUnitName(unitName);
	// Checks data source type
	if (swapDataSources) {
	    entityManagerFactoryBean.setDataSource(dataSource);
	} else {
	    addJtaDatasource(entityManagerFactoryBean);
	}

	if (ObjectUtils.notNull(loader)) {
	    entityManagerFactoryBean.setBeanClassLoader(loader);
	}

	// entityManagerFactoryBean.setPackagesToScan();
	entityManagerFactoryBean.setPersistenceProvider(persistenceProvider);
	if (CollectionUtils.valid(properties)) {
	    entityManagerFactoryBean.setJpaProperties(properties);
	}

	// Configures JPA ORM system for use
	entityManagerFactoryBean.afterPropertiesSet();

	return entityManagerFactoryBean;
    }

    /**
     * Creates {@link JpaTransactionManager} for container scoped use
     * 
     * @return {@link JpaTransactionManager}
     */
    private JpaTransactionManager transactionManager() {

	JpaTransactionManager transactionManager = new JpaTransactionManager();

	LocalContainerEntityManagerFactoryBean emfBean = entityManagerFactory();
	EntityManagerFactory emf = emfBean.getObject();
	transactionManager.setEntityManagerFactory(emf);

	return transactionManager;

    }

    /**
     * Initializes and builds {@link EntityManagerFactory} from configuration
     * 
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    public EntityManagerFactory getEmf() throws IOException {

	EntityManagerFactory emf;

	initProperties();
	initDataSource();
	JpaTransactionManager transactionManager = transactionManager();
	emf = transactionManager.getEntityManagerFactory();

	return emf;
    }

    /**
     * Builder class for {@link SpringORM} initialization
     * 
     * @author Levan Tsinadze
     * @since 0.1.2
     */
    public static class Builder {

	private SpringORM springORM;

	public Builder(String dataSourceName,
		PersistenceProvider persistenceProvider, String unitName) {
	    this.springORM = new SpringORM(dataSourceName, persistenceProvider,
		    unitName);
	}

	public Builder properties(Properties properties) {
	    springORM.properties = properties;
	    return this;
	}

	public Builder properties(Map<Object, Object> properties) {

	    if (CollectionUtils.valid(properties)) {
		springORM.properties = new Properties();
		springORM.properties.putAll(properties);
	    }

	    return this;
	}

	public Builder classLoader(ClassLoader loader) {
	    springORM.loader = loader;
	    return this;
	}

	public Builder swapDataSource(boolean swapDataSources) {
	    springORM.swapDataSources = swapDataSources;
	    return this;
	}

	public SpringORM build() {
	    return this.springORM;
	}
    }
}
