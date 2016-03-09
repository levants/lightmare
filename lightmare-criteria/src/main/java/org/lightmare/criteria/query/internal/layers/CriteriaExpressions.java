package org.lightmare.criteria.query.internal.layers;

import java.util.function.BiFunction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * Expressions for JPA criteria API
 * 
 * @author Levan Tsinadze
 *
 */
public interface CriteriaExpressions {

    /**
     * Binary expression with parameters for criteria function
     * 
     * @author Levan Tsinadze
     *
     */
    @FunctionalInterface
    public static interface ParamFunction {

        Predicate apply(CriteriaBuilder builder, Expression<?> expression, Object value);
    }

    /**
     * Binary expressions
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Binaries {

        EQ("equal", CriteriaBuilder::equal);

        public final String key;

        public final ParamFunction function;

        private Binaries(final String key, final ParamFunction function) {
            this.key = key;
            this.function = function;
        }
    }

    /**
     * Unary operators
     * 
     * @author Levan Tsinadze
     *
     */
    public static enum Unaries {

        IS_NULL("isNull", CriteriaBuilder::isNull),

        NOT_NULL("isNotNull", CriteriaBuilder::isNotNull);

        public final String key;

        public final BiFunction<CriteriaBuilder, Expression<?>, Predicate> function;

        private Unaries(final String key, final BiFunction<CriteriaBuilder, Expression<?>, Predicate> function) {
            this.key = key;
            this.function = function;
        }
    }
}
