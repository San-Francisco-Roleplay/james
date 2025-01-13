package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

public class RateLimit implements EmbedTemplate {
    public @NotNull EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("Slow Down!")
                .setDescription("Too many requests. You will be able to interact with this again shortly.")
                .setColor(Colors.Red.getColor());
    }
}
