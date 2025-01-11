package com.computiotion.sfrp.bot.config;

public class SchemaViolationError extends RuntimeException {
    public SchemaViolationError(String message) {
        super(message);
    }
}
