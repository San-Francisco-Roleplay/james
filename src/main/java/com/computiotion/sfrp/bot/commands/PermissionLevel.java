package com.computiotion.sfrp.bot.commands;

public enum PermissionLevel {
    Enabled("enabled"),
    Staff("staff"),
    InternalAffairs("ia"),
    Developer("dev"),
    SeniorManagement("seniormanagement"),
    Disabled("disabled");

    private final String code;

    PermissionLevel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
