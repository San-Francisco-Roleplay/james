package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class NoPerms implements EmbedTemplate {
    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("Lacking Permissions")
                .setDescription("You don't have permissions to access this resource.")
                .setColor(Colors.Red.getColor());
    }
}
