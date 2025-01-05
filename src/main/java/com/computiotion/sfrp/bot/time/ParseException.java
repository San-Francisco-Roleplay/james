package com.computiotion.sfrp.bot.time;

public class ParseException extends RuntimeException {
    private final String unit;

    public ParseException(String message, String unit) {
        super(message);
        this.unit = unit;
    }

    public String getFailedAt() {
        return unit;
    }
}
