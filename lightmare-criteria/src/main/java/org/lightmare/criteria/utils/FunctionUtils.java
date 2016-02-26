package org.lightmare.criteria.utils;

/**
 * Utility class for functional expressions
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class FunctionUtils {

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
    public static interface ErrorWrapperConsumer<T, E extends Exception> {

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
    public static interface ErrorWrapperBiConsumer<T, K, E extends Exception> {

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
    public static interface ErrorWrapperSupplier<T, E extends Exception> {

        /**
         * Get value and throws appropriated exception
         * 
         * @param value
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
    public static interface ErrorWrapperFunction<T, R, E extends Exception> {

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
    public static interface ErrorWrapperBiFunction<T, K, R, E extends Exception> {

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

    /**
     * Calls consumer implementation and wraps errors
     * 
     * @param value
     * @param consumer
     */
    public static <T, E extends Exception> void acceptWrap(T value, ErrorWrapperConsumer<T, E> consumer) {

        try {
            consumer.accept(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Calls consumer implementation and wraps errors
     * 
     * @param value1
     * @param value2
     * @param consumer
     */
    public static <T, K, E extends Exception> void acceptWrap(T value1, K value2,
            ErrorWrapperBiConsumer<T, K, E> consumer) {

        try {
            consumer.accept(value1, value2);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Calls supplier implementation and wraps errors
     * 
     * @param supplier
     * @return T result from supplier
     */
    public static <T, E extends Exception> T getWrap(ErrorWrapperSupplier<T, E> supplier) {

        T result;

        try {
            result = supplier.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    /**
     * Calls function implementation and wraps errors
     * 
     * @param value
     * @param function
     * @return R result from function
     */
    public static <T, R, E extends Exception> R applyWrap(T value, ErrorWrapperFunction<T, R, E> function) {

        R result;

        try {
            result = function.apply(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    /**
     * Calls function implementation and wraps errors
     * 
     * @param value1
     * @param value2
     * @param function
     * @return R result from function
     */
    public static <T, K, R, E extends Exception> R applyWrap(T value1, K value2,
            ErrorWrapperBiFunction<T, K, R, E> function) {

        R result;

        try {
            result = function.apply(value1, value2);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }
}
