package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

public class NoDMs implements EmbedTemplate {
    public EmbedBuilder makeEmbed() {
        return new EmbedBuilder()
                .setTitle("Direct Messages")
                .setDescription("Unfortunately, this command doesn't work in DMs. Try running this in a server.")
                .setColor(Colors.Red.getColor());
    }
}
