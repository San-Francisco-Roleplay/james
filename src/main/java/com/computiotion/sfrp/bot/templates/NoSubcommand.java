package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class NoSubcommand implements EmbedTemplate {
    private final String command;

    public NoSubcommand(String command) {
        this.command = command.trim();
    }

    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("No Sub-Command Found")
                .setDescription(String.format("There's no subcommand with the name \n```\n%s\n```", command))
                .setColor(Colors.Red.getColor());
    }
}
