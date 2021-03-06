/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
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
package org.lightmare.jpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.apache.log4j.Logger;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.jpa.jta.HibernateConfig;
import org.lightmare.jpa.spring.SpringORM;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.namimg.NamingUtils;

/**
 * Creates and caches {@link EntityManagerFactory} for each EJB bean
 * {@link Class}'s appropriate {@link Field} (annotated by @PersistenceContext)
 * value
 *
 * @author Levan Tsinadze
 * @since 0.0.79-SNAPSHOT
 */
public class JpaManager {

    // Entity classes
    private List<String> classes;

    // Path for configuration XML file
    private String path;

    // URL for configuration XML file
    private URL url;

    // Additional properties
    private Map<Object, Object> properties;

    // Cache JTA data source with RESOURCE_LOCAL type
    private boolean swapDataSource;

    // Scan archives in classpath to find entities
    private boolean scanArchives;

    // Initialize level class loader
    private ClassLoader loader;

    // Check if JPA is configured by Spring data
    private boolean springPersistence;

    // Data source name for Spring data configuration
    private String dataSourceName;

    // Error message for connection binding to JNDI names
    private static final String COULD_NOT_BIND_JNDI_ERROR = "could not bind connection";

    private static final String NOT_IN_PROG_ERROR = "Connection %s was not in progress";

    private static final Logger LOG = Logger.getLogger(JpaManager.class);

    /**
     * Private constructor to avoid initialization beside
     * {@link JpaManager.Builder} class
     */
    private JpaManager() {
    }

    /**
     * Initializes {@link Map} of additional properties
     *
     * @return {@link Map} additional properties
     */
    private Map<Object, Object> getProperties() {

        if (properties == null) {
            properties = new HashMap<Object, Object>();
        }

        return properties;
    }

    /**
     * Adds JPA JNDI properties to additional configuration
     */
    private void addJndiProperties() {
        getProperties().putAll(JndiManager.JNDIConfigs.INIT.hinbernateConfig);
    }

    /**
     * Gets appropriated prefix for passed key
     *
     * @param key
     * @return {@link Object} key with prefix
     */
    private Object getKeyWithPerfix(Object key) {

        Object validKey;

        if (key instanceof String) {
            String textKey = ObjectUtils.cast(key, String.class);
            validKey = HibernatePrefixes.validKey(textKey);
        } else {
            validKey = key;
        }

        return validKey;
    }

    /**
     * Validates modifies and adds parameters to global JPA configuration
     *
     * @param entry
     * @param config
     */
    private void configure(Map.Entry<Object, Object> entry, Map<Object, Object> config) {

        Object key = entry.getKey();
        Object value = entry.getValue();
        Object validKey = getKeyWithPerfix(key);
        config.put(validKey, value);
    }

    /**
     * Adds appropriated prefixes to JPA configuration properties
     *
     * @param properties
     */
    private Map<Object, Object> configure(Map<Object, Object> properties) {

        Map<Object, Object> config;

        if (properties == null || properties.isEmpty()) {
            config = properties;
        } else {
            config = new HashMap<Object, Object>();
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                configure(entry, config);
            }
        }

        return config;
    }

    /**
     * Checks if entity persistence.xml {@link URL} is provided
     *
     * @return boolean
     */
    private boolean checkForURL() {
        return ObjectUtils.notNull(url) && StringUtils.valid(url.toString());
    }

    /**
     * Adds default transaction properties for JTA data sources
     */
    private void addTransactionManager() {

        HibernateConfig[] hibernateConfigs = HibernateConfig.values();
        Map<Object, Object> configMap = getProperties();
        for (HibernateConfig hibernateConfig : hibernateConfigs) {
            CollectionUtils.putIfAbscent(configMap, hibernateConfig.key, hibernateConfig.value);
        }
    }

    /**
     * Initializes {@link SpringORM} with appropriate configuration for Spring
     * data JPA configuration
     *
     * @param provider
     * @param unitName
     * @return {@link SpringORM}
     * @throws IOException
     */
    private SpringORM getSpringORM(PersistenceProvider provider, String unitName) throws IOException {
        return new SpringORM.Builder(dataSourceName, provider, unitName).properties(properties).classLoader(loader)
                .swapDataSource(swapDataSource).build();
    }

    /**
     * Builds {@link EntityManagerFactory} from Spring ORM module
     *
     * @param provider
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    private EntityManagerFactory getFromSpring(PersistenceProvider provider, String unitName) throws IOException {

        EntityManagerFactory emf;

        SpringORM springORM = getSpringORM(provider, unitName);
        emf = springORM.getEmf();

        return emf;
    }

    /**
     * Gets {@link List} persistence XML configuration {@link URL} addresses
     *
     * @param pathCheck
     * @param configLoader
     * @return {@link List} of {@link URL} addresses
     * @throws IOException
     */
    private List<URL> readFromPath(boolean pathCheck, XMLInitializer configLoader) throws IOException {

        List<URL> xmls;

        if (pathCheck) {
            xmls = configLoader.readFile(path);
        } else {
            xmls = configLoader.readURL(url);
        }

        return xmls;
    }

    /**
     * Initializes persistence.xml file path
     *
     * @param builder
     * @throws IOException
     */
    private void initPersisteceXmlPath(HibernatePersistenceProviderExt.Builder builder) throws IOException {

        boolean pathCheck = StringUtils.valid(path);
        boolean urlCheck = checkForURL();
        if (pathCheck || urlCheck) {
            List<URL> xmls;
            XMLInitializer configLoader = new XMLInitializer();
            xmls = readFromPath(pathCheck, configLoader);
            builder.setXmls(xmls);
            String shortPath = configLoader.getShortPath();
            builder.setShortPath(shortPath);
        }
    }

    /**
     * Initializes {@link ClassLoader} instance
     *
     * @return {@link ClassLoader}
     */
    private ClassLoader getLoader() {

        if (loader == null) {
            loader = LibraryLoader.getContextClassLoader();
        }

        return loader;
    }

    /**
     * Sets entity types
     *
     * @param builder
     * @param overriden
     * @throws IOException
     */
    private void loadEntities(HibernatePersistenceProviderExt.Builder builder, ClassLoader loader) throws IOException {

        if (CollectionUtils.valid(classes)) {
            builder.setClasses(classes);
            // Loads entity classes to current ClassLoader instance
            LibraryLoader.loadClasses(classes, loader);
        }
    }

    /**
     * Configures and adds parameters to
     * {@link HibernatePersistenceProviderExt.Builder} instance
     *
     * @see #buildEntityManagerFactory(String)
     * @see HibernatePersistenceProviderExt
     *
     * @param builder
     * @throws IOException
     */
    private void configureProvider(HibernatePersistenceProviderExt.Builder builder) throws IOException {

        ClassLoader overriden = getLoader();
        loadEntities(builder, overriden);
        // configureProvider
        initPersisteceXmlPath(builder);
        // Sets additional parameters
        builder.setSwapDataSource(swapDataSource);
        builder.setScanArchives(scanArchives);
        builder.setOverridenClassLoader(overriden);
    }

    /**
     * Checks configuration and sets appropriated transaction manager
     */
    private void configureTransactionManager() {

        if (Boolean.FALSE.equals(swapDataSource)) {
            addTransactionManager();
        }
    }

    /**
     * Creates {@link EntityManagerFactory} by
     * <a href="http://hibernate.org">"Hibernate"</a> or by extended builder
     * {@link Ejb3ConfigurationImpl} if entity classes or persistence.xml file
     * path are provided
     *
     * @see Ejb3ConfigurationImpl#configure(String, Map) and
     *      Ejb3ConfigurationImpl#createEntityManagerFactory()
     *
     * @param unitName
     * @return {@link EntityManagerFactory}
     */
    private EntityManagerFactory buildEntityManagerFactory(String unitName) throws IOException {

        EntityManagerFactory emf;

        HibernatePersistenceProvider provider;
        HibernatePersistenceProviderExt.Builder builder = new HibernatePersistenceProviderExt.Builder();
        configureProvider(builder);
        provider = builder.build();
        configureTransactionManager();
        // Adds JNDI properties
        addJndiProperties();
        if (springPersistence) {
            emf = getFromSpring(provider, unitName);
        } else {
            emf = provider.createEntityManagerFactory(unitName, properties);
        }

        return emf;
    }

    /**
     * Checks if entity classes or persistence.xml file path are provided to
     * create {@link EntityManagerFactory}
     *
     * @see #buildEntityManagerFactory(String, String, Map, List)
     *
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    private EntityManagerFactory createEntityManagerFactory(String unitName) throws IOException {
        EntityManagerFactory emf = buildEntityManagerFactory(unitName);
        return emf;
    }

    /**
     * Re-binds JNDI name for JPA unit
     *
     * @param semaphore
     * @param fullJndiName
     * @throws IOException
     */
    private void rebindJNDIName(ConnectionSemaphore semaphore, String fullJndiName) throws IOException {

        if (JndiManager.lookup(fullJndiName) == null) {
            JndiManager.rebind(fullJndiName, semaphore.getEmf());
        }
    }

    /**
     * Binds {@link EntityManagerFactory} from passed
     * {@link ConnectionSemaphore} to appropriate JNDI name
     *
     * @param jndiName
     * @param semaphore
     * @throws IOException
     */
    private void bindJndiName(String jndiName, ConnectionSemaphore semaphore) throws IOException {

        try {
            String fullJndiName = NamingUtils.createJpaJndiName(jndiName);
            rebindJNDIName(semaphore, fullJndiName);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            String errorMessage = StringUtils.concat(COULD_NOT_BIND_JNDI_ERROR, semaphore.getUnitName());
            throw new IOException(errorMessage, ex);
        }
    }

    /**
     * Checks if JDNI is bound and if not bind passed connection for this name
     *
     * @param semaphore
     * @param bound
     * @throws IOException
     */
    private void checkAndBindJNDIName(ConnectionSemaphore semaphore, boolean bound) throws IOException {

        if (Boolean.FALSE.equals(bound)) {
            String jndiName = semaphore.getJndiName();
            if (StringUtils.valid(jndiName)) {
                bindJndiName(jndiName, semaphore);
            }
        }
    }

    /**
     * Binds {@link EntityManagerFactory} to {@link javax.naming.InitialContext}
     *
     * @param semaphore
     * @throws IOException
     */
    private void bindJndiName(ConnectionSemaphore semaphore) throws IOException {

        boolean bound = semaphore.isBound();
        checkAndBindJNDIName(semaphore, bound);
        semaphore.setBound(Boolean.TRUE);
    }

    /**
     * Builds connection, wraps it in {@link ConnectionSemaphore} locks and
     * caches appropriate instance
     *
     * @param unitName
     * @throws IOException
     */
    public void create(String unitName) throws IOException {

        ConnectionSemaphore semaphore = ConnectionContainer.getSemaphore(unitName);
        if (semaphore.isInProgress()) {
            EntityManagerFactory emf = createEntityManagerFactory(unitName);
            semaphore.setEmf(emf);
            semaphore.setInProgress(Boolean.FALSE);
            bindJndiName(semaphore);
        } else if (semaphore.getEmf() == null) {
            String errorMessage = String.format(NOT_IN_PROG_ERROR, unitName);
            throw new IOException(errorMessage);
        } else {
            bindJndiName(semaphore);
        }
    }

    /**
     * Closes passed {@link EntityManagerFactory}
     *
     * @param emf
     */
    public static void closeEntityManagerFactory(EntityManagerFactory emf) {

        if (ObjectUtils.notNull(emf) && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Closes passed {@link EntityManager} instance if it is not null and it is
     * open
     *
     * @param em
     */
    public static void closeEntityManager(EntityManager em) {

        if (ObjectUtils.notNull(em) && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Builder class to create {@link JpaManager} class object
     *
     * @author Levan Tsinadze
     * @since 0.0.79-SNAPSHOT
     */
    public static class Builder {

        private JpaManager manager;

        public Builder() {
            manager = new JpaManager();
            manager.scanArchives = Boolean.TRUE;
        }

        /**
         * Sets {@link javax.persistence.Entity} class names to initialize
         *
         * @param classes
         * @return {@link Builder}
         */
        public Builder setClasses(List<String> classes) {
            manager.classes = classes;
            return this;
        }

        /**
         * Sets {@link URL} for persistence.xml file
         *
         * @param url
         * @return {@link Builder}
         */
        public Builder setURL(URL url) {
            manager.url = url;
            return this;
        }

        /**
         * Sets path for persistence.xml file
         *
         * @param path
         * @return {@link Builder}
         */
        public Builder setPath(String path) {
            manager.path = path;
            return this;
        }

        /**
         * Sets additional persistence properties
         *
         * @param properties
         * @return {@link Builder}
         */
        public Builder setProperties(Map<Object, Object> properties) {
            manager.properties = manager.configure(properties);
            return this;
        }

        /**
         * Sets boolean check property to swap JTA data source value with non
         * JTA data source value
         *
         * @param swapDataSource
         * @return {@link Builder}
         */
        public Builder setSwapDataSource(boolean swapDataSource) {
            manager.swapDataSource = swapDataSource;
            return this;
        }

        /**
         * Sets boolean check to scan deployed archive files for
         * {@link javax.persistence.Entity} annotated classes
         *
         * @param scanArchives
         * @return {@link Builder}
         */
        public Builder setScanArchives(boolean scanArchives) {
            manager.scanArchives = scanArchives;
            return this;
        }

        /**
         * Sets {@link ClassLoader} for persistence classes
         *
         * @param loader
         * @return {@link Builder}
         */
        public Builder setClassLoader(ClassLoader loader) {
            manager.loader = loader;
            return this;
        }

        /**
         * Sets if JPA is configured over Spring data
         *
         * @param springPersistence
         * @return {@link Builder}
         */
        public Builder springPersistence(boolean springPersistence) {
            manager.springPersistence = springPersistence;
            return this;
        }

        /**
         * Sets data source name for Spring data configuration
         *
         * @param dataSourceName
         * @return {@link Builder}
         */
        public Builder dataSourceName(String dataSourceName) {
            manager.dataSourceName = dataSourceName;
            return this;
        }

        /**
         * Sets all parameters from passed {@link Configuration} instance
         *
         * @param configuration
         * @return {@link Builder}
         */
        public Builder configure(Configuration configuration) {
            // Sets all parameters from Configuration class
            setPath(configuration.getPersXmlPath()).setProperties(configuration.getPersistenceProperties())
                    .setSwapDataSource(configuration.isSwapDataSource()).setScanArchives(configuration.isScanArchives())
                    .springPersistence(configuration.isSpringPersistence());
            return this;
        }

        public JpaManager build() {
            return manager;
        }
    }
}

/**
 * Enumeration for JPA configuration prefixes
 *
 * @author Levan Tsinadze
 * @since 0.1.2
 *
 */
enum HibernatePrefixes {

    SPRING("spring."), JPA("jpa."), DAO("dao.");

    // Configuration properties prefixes
    private static final String HIBERNATE = "hibernate.";

    private final String prefix;

    /**
     * Validation class for key prefixes
     *
     * @author Levan Tsinadze
     *
     */
    private static class KeyValidator {

        String text;

        HibernatePrefixes prefix;

        boolean modified;

        String key;

        public KeyValidator(String text) {
            this.text = text;
            this.key = text;
        }

        public String getPrefix() {
            return this.prefix.prefix;
        }

        public boolean hibernateKey() {
            return Boolean.FALSE.equals(modified) && Boolean.FALSE.equals(text.startsWith(HIBERNATE));
        }
    }

    private HibernatePrefixes(String prefix) {
        this.prefix = prefix;
    }

    private static String replacePrefix(KeyValidator validator) {

        String key;

        String text = validator.text;
        if (validator.modified) {
            String prefix = validator.getPrefix();
            key = text.replace(prefix, HIBERNATE);
        } else {
            key = text;
        }

        return key;
    }

    private static void validateKey(KeyValidator validator) {

        HibernatePrefixes[] prefixes = HibernatePrefixes.values();
        int length = prefixes.length;
        String prefix;
        for (int i = CollectionUtils.FIRST_INDEX; i < length && Boolean.FALSE.equals(validator.modified); i++) {
            validator.prefix = prefixes[i];
            prefix = validator.getPrefix();
            validator.modified = validator.text.startsWith(prefix);
            validator.key = replacePrefix(validator);
        }
    }

    /**
     * Checks if passed key has appropriate prefix and if not adds this prefix
     * to passed key
     *
     * @param text
     * @return {@link String} key with appropriate prefix
     */
    public static String validKey(String text) {

        String key;

        KeyValidator validator = new KeyValidator(text);
        validateKey(validator);
        key = validator.key;
        if (validator.hibernateKey()) {
            key = StringUtils.concat(HIBERNATE, text);
        }

        return key;
    }
}
