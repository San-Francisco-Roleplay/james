package com.computiotion.sfrp.bot.templates;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface EmbedTemplate {
    @Contract(value = " -> new", pure = true)
    @NotNull
    default EmbedBuilder makeEmbed() {
        return new EmbedBuilder();
    }
}
