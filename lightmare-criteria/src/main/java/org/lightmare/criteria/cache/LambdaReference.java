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
package org.lightmare.criteria.cache;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Objects;

/**
 * Phantom reference for lambda generated class
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReference extends PhantomReference<Class<?>> {

    public LambdaReference(Class<?> referent, ReferenceQueue<? super Class<?>> queue) {
	super(referent, queue);
    }

    /**
     * Clears {@link Class} from cache before dereference
     * 
     * @param lambdaType
     */
    public void removeFromCache(Class<?> lambdaType) {

	if (Objects.nonNull(lambdaType)) {
	    LambdaCache.remove(lambdaType);
	}
    }

    @Override
    public void clear() {

	try {
	    Class<?> lambdaType = get();
	    removeFromCache(lambdaType);
	} finally {
	    super.clear();
	}
    }
}
