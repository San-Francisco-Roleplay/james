package com.computiotion.sfrp.bot.config.erlc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class ERLCConfig {
    @NotNull
    private final String logSender;
    @NotNull
    private final List<Message> messages;
    private final long messageDelay;
    private final HashMap<CommandLogType, String> logs;
    private final Set<String> alwaysAllowed;
    private final Set<String> offDutyExempt;

    public ERLCConfig(@NotNull String logSender, @NotNull List<Message> messages, long messageDelay, @NotNull HashMap<CommandLogType, String> logs, Set<String> alwaysAllowed, Set<String> offDutyExempt) {
        this.logSender = logSender;
        this.messages = messages;
        this.messageDelay = messageDelay;
        this.logs = logs;
        this.alwaysAllowed = alwaysAllowed;
        this.offDutyExempt = offDutyExempt;
    }

    @UnmodifiableView
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * @return The time between messages.
     */
    public long getMessageDelay() {
        return messageDelay;
    }

    @UnmodifiableView
    public Map<CommandLogType, String> getLogs() {
        return Collections.unmodifiableMap(logs);
    }

    @UnmodifiableView
    public Set<String> getAlwaysAllowedCommands() {
        return Collections.unmodifiableSet(alwaysAllowed);
    }

    public @NotNull String getLogSenderId() {
        return logSender;
    }

    @UnmodifiableView
    public Set<String> getOffDutyExempt() {
        return Collections.unmodifiableSet(offDutyExempt);
    }
}
