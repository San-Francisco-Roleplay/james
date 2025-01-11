package com.computiotion.sfrp.bot.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandExecutor {
    /**
     * @return the subcommand name
     */
    String value() default "";

    /**
     * @return the description of the command
     */
    String description() default "No description was provided.";

    PermissionLevel level() default PermissionLevel.Enabled;
}
