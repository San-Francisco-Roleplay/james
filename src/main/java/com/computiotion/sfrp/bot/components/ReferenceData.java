package com.computiotion.sfrp.bot.components;

import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.commands.CommandExecutor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class ReferenceData {
    private Method commandMethod = null;
    private Class<? extends Command> controller = null;
    private final ComponentData payload;
    private Method handler;

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull ReferenceData generic(Method handler, ComponentData payload) {
        return new ReferenceData(handler, payload);
    }

    private ReferenceData(Method handler, ComponentData payload) {
        this.handler = handler;
        this.payload = payload;
    }

    public ReferenceData(Method commandMethod, Class<? extends Command> controller, ComponentData payload) {
        this.commandMethod = commandMethod;
        this.controller = controller;
        this.payload = payload;
    }

    public Method getCommandMethod() {
        return commandMethod;
    }

    public Class<? extends Command> getController() {
        return controller;
    }

    public @Nullable Method getComponentMethod() {
        if (handler != null) return handler;

        CommandExecutor executor = commandMethod.getAnnotation(CommandExecutor.class);
        if (executor == null) return null;


        Method[] methods = controller.getMethods();

        handler = Arrays.stream(methods)
                .filter(check -> {
                    ComponentHandler handler = check.getAnnotation(ComponentHandler.class);
                    if (handler == null) return false;

                    String subcommand = handler.value();
                    return Objects.equals(subcommand, "*") || Objects.equals(subcommand, executor.value());
                })
                .findFirst()
                .orElse(null);

        return handler;
    }

    public ComponentData getPayload() {
        return payload;
    }
}
