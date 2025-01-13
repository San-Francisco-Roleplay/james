package com.computiotion.sfrp.bot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class SlashCommandInteraction extends CommandInteraction {
    private final net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction interaction;

    protected SlashCommandInteraction(@NotNull Method method, @NotNull net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction interaction, @NotNull User user, @Nullable Guild guild, @NotNull CommandInteractionType type) {
        super(method, user, guild, type);
        this.interaction = interaction;
    }

    public net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction getInteraction() {
        return interaction;
    }
}
