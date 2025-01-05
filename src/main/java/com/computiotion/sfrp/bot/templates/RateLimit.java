package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class RateLimit implements EmbedTemplate {
    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("Rate Limit Exceeded")
                .setDescription("Too many requests. You will be able to interact with this again shortly.")
                .setColor(Colors.Red.getColor());
    }
}
