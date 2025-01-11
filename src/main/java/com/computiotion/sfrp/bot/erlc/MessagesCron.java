package com.computiotion.sfrp.bot.erlc;

import com.computiotion.sfrp.bot.config.erlc.ERLCConfig;
import com.computiotion.sfrp.bot.config.erlc.Message;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

public class MessagesCron extends TimerTask {
    private final long interval;
    private final List<Message> messages;
    private int index = 0;

    public MessagesCron(@NotNull ERLCConfig config) {
        interval = config.getMessageDelay();
        messages = config.getMessages();
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        if (index >= messages.size()) index = 0;

        Rest erlc = Rest.fromEnv();
        if (messages.isEmpty()) {
            return;
        }

        Message message = messages.get(index);
        String text = message.text();

        try {
            switch (message.getType()) {
                case Hint -> erlc.hint(text);
                case Message -> erlc.message(text);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        index++;
    }
}
