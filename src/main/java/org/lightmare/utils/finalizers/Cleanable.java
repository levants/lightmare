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
package org.lightmare.utils.finalizers;

import java.io.IOException;

/**
 * Interface which should be implemented for phantom reference to close unused
 * resources
 * 
 * @author Levan Tsinadze
 * @since 0.0.85-SNAPSHOT
 * @see FinalizationUtils
 */
public interface Cleanable {

    /**
     * Should be implemented as resources cleaner after object was collected
     * instead of override {@link Object}'s finalize method
     * 
     * @throws IOException
     */
    void clean() throws IOException;
}
