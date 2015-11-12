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

import java.net.URL;
import java.util.List;

/**
 * Additional configuration parameters for JPA / Hibernate configuration
 *
 * @author Levan Tsinadze
 * @since 0.0.86-SNAPSHOT
 */
public class MetaConfig {

    // Additional Arguments for JPA / ORM initialization
    private transient ClassLoader overridenClassLoader;

    private transient List<String> classes;

    private transient List<URL> xmls;

    private transient boolean swapDataSource;

    private transient boolean scanArchives;

    // Default path of location for persistence XML configuration file
    private transient String shortPath = "/META-INF/persistence.xml";

    /**
     * Gets {@link ClassLoader} from passed {@link MetaConfig} configuration
     * container for instant EJB bean
     *
     * @param metaConfig
     * @return {@link ClassLoader} enriched with additional methods
     */
    public static ClassLoader getOverridenClassLoader(MetaConfig metaConfig) {

        ClassLoader loader;

        if (metaConfig == null) {
            loader = null;
        } else {
            loader = metaConfig.overridenClassLoader;
        }

        return loader;
    }

    public void setOverridenClassLoader(ClassLoader overridenClassLoader) {
        this.overridenClassLoader = overridenClassLoader;
    }

    public static List<String> getClasses(MetaConfig metaConfig) {

        List<String> entities;

        if (metaConfig == null) {
            entities = null;
        } else {
            entities = metaConfig.classes;
        }

        return entities;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public static List<URL> getXmls(MetaConfig metaConfig) {

        List<URL> urls;

        if (metaConfig == null) {
            urls = null;
        } else {
            urls = metaConfig.xmls;
        }

        return urls;
    }

    public void setXmls(List<URL> xmls) {
        this.xmls = xmls;
    }

    /**
     * Validates if swap of data sources is needed
     *
     * @param metaConfig
     * @return <code>boolean</code> validation result
     */
    public static boolean isSwapDataSource(MetaConfig metaConfig) {

        boolean valid;

        if (metaConfig == null) {
            valid = Boolean.FALSE;
        } else {
            valid = metaConfig.swapDataSource;
        }

        return valid;
    }

    /**
     * Sets flag for swap data sources
     *
     * @param swapDataSource
     */
    public void setSwapDataSource(boolean swapDataSource) {
        this.swapDataSource = swapDataSource;
    }

    /**
     * Validates if scan for archives in class path is needed
     *
     * @param metaConfig
     * @return <code>boolean</code> validation result
     */
    public static boolean isScanArchives(MetaConfig metaConfig) {

        boolean valid;

        if (metaConfig == null) {
            valid = Boolean.FALSE;
        } else {
            valid = metaConfig.scanArchives;
        }

        return valid;
    }

    /**
     * Sets flag for scan of archives in class path
     *
     * @param scanArchives
     */
    public void setScanArchives(boolean scanArchives) {
        this.scanArchives = scanArchives;
    }

    public static String getShortPath(MetaConfig metaConfig) {

        String path;

        if (metaConfig == null) {
            path = null;
        } else {
            path = metaConfig.shortPath;
        }

        return path;
    }

    public void setShortPath(String shortPath) {
        this.shortPath = shortPath;
    }
}
