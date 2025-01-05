package com.computiotion.sfrp.bot.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandController {
    /**
     * @return the name of the command
     */
    String value();

    /**
     * @return whether the command is internal and may only be used in development servers
     */
    boolean internal() default false;
    /**
     * @return the description of the command
     */
    String description() default "No description was provided.";

    /**
     * @return whether the command may only be used in guilds.
     */
    boolean guildOnly() default false;
}
