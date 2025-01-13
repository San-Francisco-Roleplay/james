package com.computiotion.sfrp.bot.commands;

public enum PermissionLevel {
    Enabled("enabled"),
    Staff("staff"),
    HighRank("hr"),
    Developer("dev"),
    InternalAffairs("ia"),
    SeniorHighRank("shr"),
    BoardOfExecutives("boe"),
    Management("m"),
    Supervisors("svb"),
    SeniorManagement("sm"),
    Disabled("disabled");

    private final String code;

    PermissionLevel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
