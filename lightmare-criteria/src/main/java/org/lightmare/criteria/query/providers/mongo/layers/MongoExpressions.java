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
package org.lightmare.criteria.query.providers.mongo.layers;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

/**
 * Expressions of MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 */
public interface MongoExpressions {

    /**
     * Unary expressions
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Unaries {

        EXISTS("exists", Filters::exists);

        public final String expression;

        public final Function<String, Bson> function;

        private Unaries(String expression, Function<String, Bson> function) {
            this.expression = expression;
            this.function = function;
        }
    }

    /**
     * Binary MongoDB query expressions
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Binaries {

        EQ("eq", Filters::eq), // Equals
        GT("gt", Filters::gt), // Greater than
        GTE("gte", Filters::gte), // Greater than or equals
        LT("lt", Filters::lt), // Less than
        LTE("lte", Filters::lte), // Less than or equals
        NE("ne", Filters::ne), // Not equals
        IN("in", Filters::in), // In
        NIN("nin", Filters::nin); // Not in

        public final String expression;

        public final BiFunction<String, Object, Bson> function;

        private Binaries(String expression, BiFunction<String, Object, Bson> function) {
            this.expression = expression;
            this.function = function;
        }
    }
}
