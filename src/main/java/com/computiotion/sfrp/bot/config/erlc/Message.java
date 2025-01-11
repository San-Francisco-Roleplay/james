package com.computiotion.sfrp.bot.config.erlc;

public record Message(String text, MessageType type) {
    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
