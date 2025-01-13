package com.computiotion.sfrp.bot.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComponentHandler {
    /**
     * @return The subcommand that this will handle for. If the command is an {@code *}, it will capture all component interactions.
     */
    String value() default "*";
}
