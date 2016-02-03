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

import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of {@link java.lang.ref.PhantomReference} for tracking lambda
 * generated classes
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReference extends PhantomReference<Class<?>> {

    public LambdaReference(Class<?> referent, ReferenceQueue<? super Class<?>> queue) {
        super(referent, queue);
    }

    @Override
    public void clear() {

        try {
            ObjectUtils.ifNotNull(this::get, LambdaCache::remove);
        } finally {
            super.clear();
        }
    }
}
