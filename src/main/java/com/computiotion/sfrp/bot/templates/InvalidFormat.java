package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class InvalidFormat implements EmbedTemplate {
    private final String usage;

    public InvalidFormat(String usage) {
        this.usage = usage;
    }

    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("Incorrect Usage")
                .setDescription(String.format("The proper usage is: \n```\n%s\n```", usage))
                .setColor(Colors.Red.getColor());
    }
}
