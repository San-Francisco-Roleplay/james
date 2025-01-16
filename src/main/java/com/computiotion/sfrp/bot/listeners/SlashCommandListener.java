package com.computiotion.sfrp.bot.listeners;

import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.templates.InternalError;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class SlashCommandListener extends ListenerAdapter {
    private static final Log log = LogFactory.getLog(SlashCommandListener.class);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;

        log.trace("Executing from slash");
        try {
            Command.executeFromSlash(event);
        } catch (RuntimeException | ParserConfigurationException | IOException | SAXException e) {
            SentryId id = Sentry.captureException(e);

            event.replyEmbeds(new InternalError(id.toString(), true).makeEmbed().build())
                    .setEphemeral(true)
                    .queue();
            throw new RuntimeException(e);
        }
        log.trace("Finished executing from slash");
    }
}
