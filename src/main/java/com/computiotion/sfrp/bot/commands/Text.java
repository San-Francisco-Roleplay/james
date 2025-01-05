package com.computiotion.sfrp.bot.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Text {
    /**
     * @return the name of the parameter
     */
    String value();
    /**
     * @return the description of the parameter
     */
    String description() default "No description was provided.";
    /**
     * @return whether the parameter is required
     */
    boolean required() default true;
}
