package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.config.erlc.CommandLogType;
import com.computiotion.sfrp.bot.erlc.CommandLog;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandFormat implements EmbedTemplate {
    private final String discordUserId;
    private final CommandLog log;
    private final CommandLogType type;

    public CommandFormat(String discordUserId, CommandLog log, CommandLogType type) {
        this.discordUserId = discordUserId;
        this.log = log;
        this.type = type;
    }

    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle(type.getReadable() + " Captured")
                .setDescription("An " + type.getReadable().toLowerCase() + " was captured, sent by <@" + discordUserId + ">\n```\n:" + log.command() + "\n```")
                .setColor(Colors.Red.getColor());
    }
}
