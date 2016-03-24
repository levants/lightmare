package org.lightmare.criteria.utils;

/**
 * Utility class for functional expressions
 * 
 * @author Levan Tsinadze
 *
 */
abstract class FunctionUtils extends AbstractFunctionUtils {

    /**
     * Calls consumer implementation and wraps errors
     * 
     * @param value
     * @param consumer
     */
    public static <T, E extends Exception> void call(T value, ConsumerEx<T, E> consumer) {

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
    public static <T, K, E extends Exception> void call(T value1, K value2, BiConsumerEx<T, K, E> consumer) {

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
    public static <T, E extends Exception> T get(SupplierEx<T, E> supplier) {

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
    public static <T, R, E extends Exception> R apply(T value, FunctionEx<T, R, E> function) {

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
    public static <T, K, R, E extends Exception> R apply(T value1, K value2, BiFunctionEx<T, K, R, E> function) {

        R result;

        try {
            result = function.apply(value1, value2);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }
}
