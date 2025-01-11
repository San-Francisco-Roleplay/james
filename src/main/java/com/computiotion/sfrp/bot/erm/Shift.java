package com.computiotion.sfrp.bot.erm;


import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Shift {
    private final String username;
    @SerializedName("user_id")
    private final String userId;
    @SerializedName("start_epoch")
    private final Long startedAt;
    @SerializedName("end_epoch")
    private final Long endedAt;
    @SerializedName("guild")
    private final String guildId;
    @SerializedName("type_")
    private final String type;
    @SerializedName("added_time")
    private final Long addedTime;
    @SerializedName("removed_time")
    private final Long removedTime;
    private final List<ShiftBreak> breaks;


    public Shift(String username, String userId, Long startedAt, Long endedAt, String guildId, String type, Long addedTime, Long removedTime, List<ShiftBreak> breaks) {
        this.username = username;
        this.userId = userId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.guildId = guildId;
        this.type = type;
        this.addedTime = addedTime;
        this.removedTime = removedTime;
        this.breaks = breaks;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getStartedAt() {
        return Instant.ofEpochSecond(startedAt);
    }

    public Instant getEndedAt() {
        if (endedAt == 0) return null;
        return Instant.ofEpochSecond(endedAt);
    }

    public String getGuildId() {
        return guildId;
    }

    public String getType() {
        return type;
    }

    public Long getAddedTime() {
        return addedTime;
    }

    public Long getRemovedTime() {
        return removedTime;
    }

    public List<ShiftBreak> getBreaks() {
        return Collections.unmodifiableList(breaks);
    }
}
