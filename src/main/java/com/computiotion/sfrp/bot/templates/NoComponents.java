package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class NoComponents implements EmbedTemplate {
    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("Lacking Permission")
                .setDescription("Unfortunately, you're not allowed to interact with this.")
                .setColor(Colors.Red.getColor());
    }
}
