package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.config.erlc.ERLCConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

public class Config {
    private static final Log log = LogFactory.getLog(Config.class);
    private final @NotNull CommandConfig command;
    private final ERLCConfig erlc;
    private final StaffConfig staff;
    private final SessionConfig sessions;

    public Config(@NotNull CommandConfig command, ERLCConfig erlc, StaffConfig staff, SessionConfig sessions) {
        this.command = command;
        this.erlc = erlc;
        this.staff = staff;
        this.sessions = sessions;
    }

    public CommandConfig getCommands() {
        return command;
    }

    public ERLCConfig getErlc() {
        return erlc;
    }

    public StaffConfig getStaff() {
        return staff;
    }

    public SessionConfig getSessions() {
        return sessions;
    }
}
