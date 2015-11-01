/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Caches lambda classes with phantom reference and cleans appropriated cache
 * when class is reclaimed
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaCache {

    private static final ConcurrentMap<Class<?>, QueryTuple> LAMBDAS = new ConcurrentHashMap<>();

    public static void putLambda(Class<?> key, QueryTuple value) {
	LAMBDAS.putIfAbsent(key, value);
    }

    public static void putByInstance(Object lambda, QueryTuple value) {
	Class<?> key = lambda.getClass();
	putLambda(key, value);
    }

    public static QueryTuple getLambda(Class<?> key) {
	return LAMBDAS.get(key);
    }

    public static QueryTuple getByInstance(Object lambda) {

	QueryTuple tuple;

	Class<?> key = lambda.getClass();
	tuple = getLambda(key);

	return tuple;
    }
}
