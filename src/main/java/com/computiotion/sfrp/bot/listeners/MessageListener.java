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

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class MessageListener extends ListenerAdapter {
    private static final Log log = LogFactory.getLog(MessageListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (event.getAuthor().isBot()) return;

        String mention = event.getJDA().getSelfUser().getAsMention();
        String trimmed = message.getContentRaw().trim();
        Config config = Config.getInstance();

        Set<String> prefixes = new HashSet<>(config.getPrefixes());
        prefixes.add(mention);

        String prefix = prefixes.stream().filter(trimmed::startsWith).findFirst().orElse(null);
        if (prefix == null) return;
        String content = trimmed.substring(prefix.length()).trim();

        if (content.isEmpty()) return;

        log.trace("Executing from message");
        try {
            Command.executeFromMessage(message, content);
        } catch (RuntimeException e) {
            message.replyEmbeds(new InternalError().makeEmbed().build()).queue();
            throw new RuntimeException(e);
        }
        message.removeReaction(Emoji.Loading.toJda()).complete();
        log.trace("Finished executing from message");
    }
}
