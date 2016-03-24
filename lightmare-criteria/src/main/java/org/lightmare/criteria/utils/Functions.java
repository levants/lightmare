package org.lightmare.criteria.utils;

/**
 * Defines functions with exceptions
 * 
 * @author Levan Tsinadze
 *
 */
public interface Functions {

    /**
     * Wraps exceptions and errors to avoid <code>throws</code> statement
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            argument type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface ConsumerEx<T, E extends Exception> {

        /**
         * Accepts passed value and throws appropriated exception
         * 
         * @param value
         * @throws E
         *             exception implementation
         */
        void accept(T value) throws E;
    }

    /**
     * Wraps exceptions and errors to avoid <code>throws</code> statement
     * 
     * @author Levan
     *
     * @param <T>
     *            first argument type
     * @param <K>
     *            second argument type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface BiConsumerEx<T, K, E extends Exception> {

        /**
         * Accepts passed value1 and value2 and throws appropriated exception
         * 
         * @param value1
         * @param value2
         * @throws E
         *             exception implementation
         */
        void accept(T value1, K value2) throws E;
    }

    /**
     * Wraps exceptions and errors to avoid <code>throws</code> statement
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            result type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface SupplierEx<T, E extends Exception> {

        /**
         * Get value and throws appropriated exception
         * 
         * @throws E
         *             exception implementation
         */
        T get() throws E;
    }

    /**
     * Wraps exceptions and errors to avoid <code>throws</code> statement
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            function argument type
     * @param <R>
     *            result type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface FunctionEx<T, R, E extends Exception> {

        /**
         * Applies for value and throws appropriated exception
         * 
         * @param value
         * @return R
         * @throws E
         *             exception implementation
         */
        R apply(T value) throws E;
    }

    /**
     * Wraps exceptions and errors to avoid <code>throws</code> statement
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            first parameter type
     * @param <K>
     *            second parameter type
     * @param <R>
     *            return type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface BiFunctionEx<T, K, R, E extends Exception> {

        /**
         * Applies for value1 and value2, and throws appropriated exception
         * 
         * @param value1
         * @param value2
         * @return R
         * @throws E
         *             exception implementation
         */
        R apply(T value1, K value2) throws E;
    }
}
