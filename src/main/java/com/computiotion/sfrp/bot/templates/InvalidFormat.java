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
                .setDescription(String.format("The proper usage is: \n```\n%s\n```\nAs a reminder, for multi-word text/string inputs, you must wrap the parameter in quotes (i.e. `\"Lorem Ipsum dolor sit amet.\"`)", usage))
                .setColor(Colors.Red.getColor());
    }
}
