package com.computiotion.sfrp.bot.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentReference {
    /**
     * @return A token for identifying this reference.
     */
    String value();
}
