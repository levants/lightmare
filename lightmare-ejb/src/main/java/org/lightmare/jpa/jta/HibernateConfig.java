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
package org.lightmare.jpa.jta;

import static org.hibernate.cfg.AvailableSettings.JTA_PLATFORM;
import static org.hibernate.cfg.AvailableSettings.TRANSACTION_STRATEGY;

import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;

/**
 * Properties to use JTA in Hibernate environment
 *
 * @author Levan Tsinadze
 * @since 0.0.34-SNAPSHOT
 */
public enum HibernateConfig {

    // JTA configuration for Hibernate deployment - JTA platform
    PLATFORM(JTA_PLATFORM, BitronixJtaPlatform.class.getName()),

    // factory class
    FACTORY(TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName());

    public final String key;

    public final String value;

    private HibernateConfig(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
