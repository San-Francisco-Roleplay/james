package com.computiotion.sfrp.bot.reference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReferenceHandler {
    /**
     * @return The id of this handler.
     */
    String value();
}
