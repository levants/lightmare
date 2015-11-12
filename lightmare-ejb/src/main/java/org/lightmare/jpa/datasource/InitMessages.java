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

/**
 * Contains error messages for data source initializations
 *
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
 */
public enum InitMessages {

    // Error messages
    NOT_APPR_INSTANCE_ERROR("Could not initialize data source %s (it is not appropriated DataSource instance)"),

    COULD_NOT_INIT_ERROR("Could not initialize data source %s"),

    COULD_NOT_CLOSE_ERROR("Could not close DataSource %s"),

    INITIALIZING_ERROR("Could not initialize datasource"),

    // Info Messages
    INITIALIZING_MESSAGE("Initializing data source %s"),

    INITIALIZED_MESSAGE("Data source %s initialized");

    public final String message;

    private InitMessages(final String message) {
        this.message = message;
    }
}
