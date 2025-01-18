package com.computiotion.sfrp.bot.listeners;

import com.computiotion.sfrp.bot.Emoji;
import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.reference.ReferenceData;
import com.computiotion.sfrp.bot.reference.ReferenceManager;
import com.computiotion.sfrp.bot.templates.InternalError;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ReferenceListener extends ListenerAdapter {
    private static final Log log = LogFactory.getLog(ReferenceListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageId = event.getMessageId();

        Message message = event.getMessage();
        Message reply = event.getMessage().getReferencedMessage();

        if (reply == null) return;

        ReferenceData data = ReferenceManager.getData(reply.getId());
        if (data == null) return;

        String content = message.getContentRaw();

        if (content.startsWith(";")) return;

        message.addReaction(Emoji.Loading.toJda())
                .complete();

        log.trace("Executing from message");
        try {
            data.execute(content);
            message.delete().queue();
        } catch (RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            log.error(e);
            message.replyEmbeds(new InternalError().makeEmbed().build()).queue();
            throw new RuntimeException(e);
        }
        log.trace("Finished executing from message");
    }
}
