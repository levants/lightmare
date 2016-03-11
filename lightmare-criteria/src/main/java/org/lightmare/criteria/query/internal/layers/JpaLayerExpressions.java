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
package org.lightmare.criteria.query.internal.layers;

import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * Implementation of expressions for JPA and JDBC layers
 * 
 * @author Levan Tsinadze
 *
 */
abstract class JpaLayerExpressions implements LayerExpressions {

    @Override
    public String and() {
        return Operators.AND;
    }

    @Override
    public String or() {
        return Operators.OR;
    }

    @Override
    public String equal() {
        return Operators.EQ;
    }

    @Override
    public String notEqual() {
        return Operators.NOT_EQ;
    }

    @Override
    public String lessThan() {
        return Operators.LESS;
    }

    @Override
    public String lessThanOrEqual() {
        return Operators.LESS_OR_EQ;
    }

    @Override
    public String greaterThan() {
        return Operators.GREATER;
    }

    @Override
    public String greaterThanOrEqual() {
        return Operators.GREATER_OR_EQ;
    }

    @Override
    public String like() {
        return Operators.LIKE;
    }

    @Override
    public String notLike() {
        return Operators.NOT_LIKE;
    }

    @Override
    public String in() {
        return Operators.IN;
    }

    @Override
    public String notIn() {
        return Operators.NOT_IN;
    }

    @Override
    public String isNull() {
        return Operators.IS_NULL;
    }

    @Override
    public String isNotNull() {
        return Operators.NOT_NULL;
    }
}
