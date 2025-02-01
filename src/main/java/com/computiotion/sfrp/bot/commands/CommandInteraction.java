package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.components.ComponentData;
import com.computiotion.sfrp.bot.components.ComponentHandler;
import com.computiotion.sfrp.bot.components.ComponentManager;
import com.computiotion.sfrp.bot.components.ReferenceData;
import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public abstract class CommandInteraction {
    private final @NotNull Method method;
    private final @NotNull User user;
    private final @Nullable Guild guild;
    private final @NotNull CommandInteractionType type;

    protected CommandInteraction(@NotNull Method method, @NotNull User user, @Nullable Guild guild, @NotNull CommandInteractionType type) {
        this.method = method;
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

    public @NotNull String registerComponent() {
        return registerComponent(false, null);
    }

    public @NotNull String registerComponent(ComponentData data) {
        return registerComponent(false, data);
    }

    public @NotNull String registerComponent(boolean permanent, ComponentData data) {
        if (permanent) {
            var forwardTo = Arrays.stream(method.getDeclaringClass().getMethods())
                    .filter(check -> {
                        ComponentHandler handler = check.getAnnotation(ComponentHandler.class);
                        if (handler == null) return false;

                        String subcommand = handler.value();
                        return Objects.equals(subcommand, "*") || Objects.equals(subcommand, method.getAnnotation(CommandExecutor.class).value());
                    })
                    .findFirst()
                    .orElse(null);
            Preconditions.checkNotNull(forwardTo, "No forwardTo was found \uD83D\uDE22");

            ComponentHandler handler = forwardTo.getAnnotation(ComponentHandler.class);
            ComponentManager.registerGeneric(forwardTo);

            return "TODO"; // TODO
        }

        @SuppressWarnings("unchecked") ReferenceData ref = new ReferenceData(method, (Class<? extends Command>) method.getDeclaringClass(), data);
        return ComponentManager.registerComponent(ref);
    }
}
