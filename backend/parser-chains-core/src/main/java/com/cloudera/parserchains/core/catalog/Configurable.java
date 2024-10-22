package com.cloudera.parserchains.core.catalog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a parser author to describe how their parser can be configured.
 *
 * <p>A parser author can use this annotation along with {@link Parameter}
 * to describe how their parser can be configured.
 *
 * <p>This should be used to annotate a public method that can be invoked
 * to configure the parser. The method must accept one or more strings.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {

    /**
     * A unique key for this configurable parameter.
     */
    String key();

    /**
     * A label for this configurable parameter that is displayed to the user.
     */
    String label() default "";

    /**
     * A description of this configurable parameter that is displayed to
     * the user.
     */
    String description() default "";

    /**
     * Defines whether the user is required to define a value for this
     * configuration parameter.
     */
    boolean required() default false;

    /**
     * The default value of this configurable parameter.
     * <p>This value is optional.
     */
    String defaultValue() default "";

    /**
     * Defines the type of widget presented to the user when
     * configuring the parameter.
     * <p>Accepts either "text" or "textarea".
     */
    WidgetType widgetType() default WidgetType.TEXT;
}
