package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class NoCommand implements EmbedTemplate {
    private final String command;

    public NoCommand(String command) {
        this.command = command;
    }

    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("No Command Found")
                .setDescription(String.format("There's no command with the name `%s`.", command))
                .setColor(Colors.Red.getColor());
    }
}
