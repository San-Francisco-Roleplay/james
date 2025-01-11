package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.config.erlc.ERLCConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

public class Config {
    private static final Log log = LogFactory.getLog(Config.class);
    private final @NotNull CommandConfig command;
    private final ERLCConfig erlc;

    public Config(@NotNull CommandConfig command, ERLCConfig erlc) {
        this.command = command;
        this.erlc = erlc;
    }

    public CommandConfig getCommands() {
        return command;
    }

    public ERLCConfig getErlc() {
        return erlc;
    }
}
