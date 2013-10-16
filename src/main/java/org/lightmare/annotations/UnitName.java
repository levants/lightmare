package org.lightmare.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.lightmare.utils.StringUtils;

/**
 * Defines unit name of {@link javax.persistence.Entity} class for scanning on
 * start time
 * 
 * @author Levan
 * @since 0.0.16-SNAPSHOT
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitName {

    String value() default StringUtils.EMPTY_STRING;
}
