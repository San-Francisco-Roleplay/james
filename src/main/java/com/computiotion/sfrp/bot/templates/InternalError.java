package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class InternalError implements EmbedTemplate {
    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("An Error Occurred")
                .setDescription("Unfortunately, an internal error occurred. This issue has been logged and will be fixed as quickly as possible.")
                .setColor(Colors.Red.getColor());
    }
}
