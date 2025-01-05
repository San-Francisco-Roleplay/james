package com.computiotion.sfrp.bot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandInteraction {
    private final @NotNull User user;
    private final @Nullable Guild guild;
    private final @NotNull CommandInteractionType type;

    protected CommandInteraction(@NotNull User user, @Nullable Guild guild, @NotNull CommandInteractionType type) {
        this.user = user;
        this.guild = guild;
        this.type = type;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @Nullable Guild getGuild() {
        return guild;
    }

    public @NotNull CommandInteractionType getType() {
        return type;
    }
}
