package com.computiotion.sfrp.bot.listeners;

import com.computiotion.sfrp.bot.Emoji;
import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.models.Config;
import com.computiotion.sfrp.bot.templates.InternalError;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SlashCommandListener extends ListenerAdapter {
    private static final Log log = LogFactory.getLog(SlashCommandListener.class);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;

        log.trace("Executing from slash");
        try {
            Command.executeFromSlash(event);
        } catch (RuntimeException e) {
            event.replyEmbeds(new InternalError().makeEmbed().build())
                    .setEphemeral(true)
                    .queue();
            throw new RuntimeException(e);
        }
        log.trace("Finished executing from slash");
    }
}
