package com.computiotion.sfrp.bot.erm;

import java.util.List;

public class ShiftBuilder {
    private String username;
    private String userId;
    private long startedAt;
    private long endedAt;
    private String guildId;
    private String type;
    private long addedTime;
    private long removedTime;
    private List<ShiftBreak> breaks;

    public ShiftBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ShiftBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ShiftBuilder setStartedAt(long startedAt) {
        this.startedAt = startedAt;
        return this;
    }

    public ShiftBuilder setEndedAt(long endedAt) {
        this.endedAt = endedAt;
        return this;
    }

    public ShiftBuilder setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public ShiftBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ShiftBuilder setAddedTime(long addedTime) {
        this.addedTime = addedTime;
        return this;
    }

    public ShiftBuilder setRemovedTime(long removedTime) {
        this.removedTime = removedTime;
        return this;
    }

    public ShiftBuilder setBreaks(List<ShiftBreak> breaks) {
        this.breaks = breaks;
        return this;
    }

    public Shift createShift() {
        return build();
    }

    public Shift build() {
        return new Shift(username, userId, startedAt, endedAt, guildId, type, addedTime, removedTime, breaks);
    }
}