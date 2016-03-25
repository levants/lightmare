package org.lightmare.criteria.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Abstract utility class for for functional expressions
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractFunctionUtils implements Functions {

    /**
     * Validates passed {@link java.util.function.Supplier} on <code>null</code>
     * and get result from it
     * 
     * @param supplier
     * @return T result from {@link java.util.function.Supplier} or
     *         <code>null</code>
     */
    public static <T> T get(Supplier<T> supplier) {

        T result;

        if (Objects.nonNull(supplier)) {
            result = supplier.get();
        } else {
            result = null;
        }

        return result;
    }

    /**
     * Validates if passed {@link java.util.function.Predicate} is not
     * <code>null</code> returns <code>true</code> for passed value
     * 
     * @param predicate
     * @param value
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean test(Predicate<T> predicate, T value) {
        return (Objects.nonNull(predicate) && predicate.test(value));
    }

    /**
     * Validates if {@link java.util.function.Consumer} is not <code>null</code>
     * and if not calls {@link java.util.function.Consumer#accept(Object)}
     * method accepts for passed value
     * 
     * @param consumer
     * @param value
     */
    public static <T> void accept(Consumer<T> consumer, T value) {
        ObjectUtils.nonNull(consumer, c -> c.accept(value));
    }

    /**
     * Gets value from {@link java.util.function.Supplier} and calls
     * {@link java.util.function.Consumer} implementation for it
     * 
     * @param supplier
     * @param consumer
     * @return T value from {@link java.util.function.Supplier}
     */
    public static <T> T acceptAndGet(Supplier<T> supplier, Consumer<T> consumer) {

        T result = supplier.get();
        accept(consumer, result);

        return result;
    }
}
