package com.computiotion.sfrp.bot.config.erlc;

public enum CommandLogType {
    Power("power-commands", "Power"),
    OffDuty("offduty-commands", "Off-Duty");

    private final String tag;
    private final String readable;

    CommandLogType(String tag, String readable) {
        this.tag = tag;
        this.readable = readable;
    }

    public String getTag() {
        return tag;
    }

    public String getReadable() {
        return readable;
    }
}
