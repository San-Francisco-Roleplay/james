package com.computiotion.sfrp.bot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class MessageCommandInteraction extends CommandInteraction {
    private final Message message;

    protected MessageCommandInteraction(@NotNull Method method, @NotNull Message message, @NotNull User user, @Nullable Guild guild, @NotNull CommandInteractionType type) {
        super(method, user, guild, type);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
