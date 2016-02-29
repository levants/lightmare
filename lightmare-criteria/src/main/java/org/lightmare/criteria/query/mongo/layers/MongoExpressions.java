package org.lightmare.criteria.query.mongo.layers;

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
