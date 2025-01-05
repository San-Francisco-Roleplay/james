package com.computiotion.sfrp.bot.commands;

import org.jetbrains.annotations.NotNull;

public class ParsedParameter {
    private final String name;
    private final String description;
    private final boolean required;
    private final ParameterType type;

    public ParsedParameter(@NotNull String name, @NotNull String description, boolean required, @NotNull ParameterType type) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public ParameterType getType() {
        return type;
    }
}
