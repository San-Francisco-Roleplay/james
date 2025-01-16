package com.computiotion.sfrp.bot.listeners;

import com.computiotion.sfrp.bot.Emoji;
import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.config.CommandConfig;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.templates.InternalError;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MessageListener extends ListenerAdapter {
    private static final Log log = LogFactory.getLog(MessageListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) return;

        String mention = event.getJDA().getSelfUser().getAsMention();
        String trimmed = message.getContentRaw().trim();
        Config config = null;
        try {
            config = ConfigReader.fromApplicationDefaults();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        CommandConfig commands = config.getCommands();

        Set<String> prefixes = new HashSet<>(commands.getPrefixes());
        if (commands.includesMention()) prefixes.add(mention);

        String prefix = prefixes.stream().filter(trimmed::startsWith).findFirst().orElse(null);
        if (prefix == null) return;
        String content = trimmed.substring(prefix.length()).trim();

        if (content.isEmpty()) return;

        log.trace("Executing from message");
        try {
            Command.executeFromMessage(message, content);
        } catch (RuntimeException | ParserConfigurationException | IOException | SAXException e) {
            SentryId id = Sentry.captureException(e);

            message.replyEmbeds(new InternalError(id.toString(), false).makeEmbed().build()).queue();
        }
        message.removeReaction(Emoji.Loading.toJda()).complete();
        log.trace("Finished executing from message");
    }
}
