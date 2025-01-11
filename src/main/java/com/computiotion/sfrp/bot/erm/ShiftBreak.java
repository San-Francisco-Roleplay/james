package com.computiotion.sfrp.bot.erm;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Objects;

public final class ShiftBreak {
    private static final Log log = LogFactory.getLog(ShiftBreak.class);
    @SerializedName("start_epoch")
    private long startedAt;
    @SerializedName("end_epoch")
    private long endedAt;

    private ShiftBreak() { log.trace("Creating ShiftBreak object."); }

    public ShiftBreak(long startedAt, long endedAt) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    @SerializedName("start_epoch")
    public long startedAt() {
        return startedAt;
    }

    @SerializedName("end_epoch")
    public long endedAt() {
        return endedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ShiftBreak) obj;
        return this.startedAt == that.startedAt &&
                this.endedAt == that.endedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startedAt, endedAt);
    }

    @Override
    public String toString() {
        return "ShiftBreak[" +
                "startedAt=" + startedAt + ", " +
                "endedAt=" + endedAt + ']';
    }

}
