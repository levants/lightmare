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
package org.lightmare.criteria.utils;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
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
     * Validates passed {@link java.util.function.BooleanSupplier} on
     * <code>null</code> and gets it result
     * 
     * @param supplier
     * @return <code>boolean</code> validation result
     */
    public static boolean getAsBoolean(BooleanSupplier supplier) {
        return (Objects.nonNull(supplier) && supplier.getAsBoolean());
    }

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
     * <code>null</code> and returns predicate test result for passed value
     * 
     * @param predicate
     * @param value
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean test(Predicate<T> predicate, T value) {
        return (Objects.nonNull(predicate) && predicate.test(value));
    }

    /**
     * Validates if passed {@link java.util.function.Predicate} is not
     * <code>null</code> returns predicate test contrary result for passed value
     * 
     * @param predicate
     * @param value
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean reverceTest(Predicate<T> predicate, T value) {
        return (Objects.nonNull(predicate) && ObjectUtils.notTrue(predicate.test(value)));
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

        if (Objects.nonNull(consumer)) {
            consumer.accept(value);
        }
    }

    /**
     * Validates passed command on <code>null</code> and executes it
     * 
     * @param command
     */
    public static void execute(Command command) {

        if (Objects.nonNull(command)) {
            command.execute();
        }
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

        T result = get(supplier);
        accept(consumer, result);

        return result;
    }

    /**
     * Validates and calls passed {@link java.util.function.Function}
     * implementation
     * 
     * @param argument
     * @param function
     * @return R function result
     */
    public static <T, R> R apply(T argument, Function<T, R> function) {

        R result;

        if (Objects.nonNull(function)) {
            result = function.apply(argument);
        } else {
            result = null;
        }

        return result;
    }
}
