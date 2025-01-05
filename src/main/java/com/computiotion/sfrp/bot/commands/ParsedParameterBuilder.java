package com.computiotion.sfrp.bot.commands;

public class ParsedParameterBuilder {
    private String name;
    private String description;
    private boolean required = false;
    private ParameterType type;

    public String getName() {
        return name;
    }

    public ParsedParameterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ParsedParameterBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public ParsedParameterBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }
    public ParameterType getType() {
        return type;
    }

    public ParsedParameterBuilder setType(ParameterType type) {
        this.type = type;
        return this;
    }

    public ParsedParameter createParsedParamater() {
        return build();
    }

    public ParsedParameter build() {
        return new ParsedParameter(name, description, required, type);
    }
}