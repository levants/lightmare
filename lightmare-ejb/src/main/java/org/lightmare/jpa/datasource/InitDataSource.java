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
package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.logging.LogUtils;

/**
 * Initializes and bind to {@link Context} pooled {@link DataSource} object
 *
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
 */
public abstract class InitDataSource {

    // Additional data source / connection pooling properties
    protected Properties properties;

    // Pool configuration with defaults
    protected PoolConfig poolConfig;

    // Initial properties for data source initialization
    protected String driver;
    protected String url;
    protected String user;
    protected String password;

    protected static final Logger LOG = Logger.getLogger(Initializer.class);

    /**
     * Constructor with configuration {@link Properties} instance
     *
     * @param properties
     */
    public InitDataSource(Properties properties) {

        if (CollectionUtils.valid(properties)) {
            this.properties = properties;
            this.poolConfig = Configuration.getPoolConfig();

            driver = properties.getProperty(ConnectionConfig.DRIVER_PROPERTY.name).trim();
            url = properties.getProperty(ConnectionConfig.URL_PROPERTY.name).trim();
            user = properties.getProperty(ConnectionConfig.USER_PROPERTY.name).trim();
            password = properties.getProperty(ConnectionConfig.PASSWORD_PROPERTY.name).trim();
        }
    }

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     *
     * @return {@link DataSource}
     * @throws IOException
     */
    protected abstract DataSource initializeDataSource() throws IOException;

    /**
     * Checks if passed {@link DataSource} is instance of appropriated
     *
     * @param dataSource
     * @throws IOException
     */
    protected abstract boolean checkInstance(DataSource dataSource) throws IOException;

    /**
     * Binds data source JNDI name
     *
     * @param dataSource
     * @param jndiName
     * @throws IOException
     */
    private void bindDataSource(DataSource dataSource, String jndiName) throws IOException {

        boolean valid = checkInstance(dataSource);
        if (valid) {
            JndiManager.rebind(jndiName, dataSource);
        } else {
            String message = String.format(InitMessages.NOT_APPR_INSTANCE_ERROR.message, jndiName);
            throw new IOException(message);
        }
    }

    /**
     * Initializes and registers {@link DataSource} object in JNDI
     * {@link javax.naming.Context}
     *
     * @throws IOException
     */
    public void create() throws IOException {

        String jndiName = Initializer.getJndiName(properties);
        LogUtils.info(LOG, InitMessages.INITIALIZING_MESSAGE.message, jndiName);
        try {
            DataSource dataSource = initializeDataSource();
            bindDataSource(dataSource, jndiName);
            LogUtils.info(LOG, InitMessages.INITIALIZED_MESSAGE.message, jndiName);
        } catch (IOException ex) {
            LogUtils.error(LOG, ex, InitMessages.COULD_NOT_INIT_ERROR.message, jndiName);
        } catch (Exception ex) {
            LogUtils.error(LOG, ex, InitMessages.COULD_NOT_INIT_ERROR.message, jndiName);
        }
    }

    /**
     * Destroys passed {@link DataSource} instance
     *
     * @param dataSource
     */
    public abstract void cleanUp(DataSource dataSource);
}