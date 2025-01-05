package com.computiotion.sfrp.bot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlashCommandInteraction extends CommandInteraction {
    private final net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction interaction;

    protected SlashCommandInteraction(@NotNull net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction interaction, @NotNull User user, @Nullable Guild guild, @NotNull CommandInteractionType type) {
        super(user, guild, type);
        this.interaction = interaction;
    }

    public net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction getInteraction() {
        return interaction;
    }
}
